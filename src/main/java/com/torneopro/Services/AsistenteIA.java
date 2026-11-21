
package com.torneopro.Services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.torneopro.Exceptions.AsistenteIAException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

public class AsistenteIA {

    // Compatible con el historial del chat existente
    public record Mensaje(String rol, String texto) {}

    private static final String URL =
        "https://api.groq.com/openai/v1/chat/completions";

    private static final String MODELO =
        "openai/gpt-oss-120b";

    private static final int MAX_HISTORIAL = 10;
    private static final int MAX_INTENTOS = 3;

    private static final String INSTRUCCIONES = """
        Eres el asistente oficial de TorneoPro,
        una plataforma de torneos de esports.

        La plataforma administra equipos de 5 jugadores,
        jugadores con roles, partidos y un bracket
        de eliminacion directa.

        Responde siempre en espanol, de forma breve,
        clara y amigable.

        Usa exclusivamente los datos actuales del torneo
        proporcionados por la aplicacion.

        Puedes responder preguntas sobre:
        - Equipos registrados.
        - Jugadores y sus roles.
        - Partidos y resultados.
        - Bracket y enfrentamientos.
        - Estadisticas disponibles.
        - Equipos favoritos segun su rendimiento.

        No inventes equipos, jugadores ni resultados.

        Si no tienes informacion suficiente,
        explica que el dato no esta disponible.

        Si te piden una prediccion, aclara que
        es una opinion basada en los datos.

        Responde en texto plano, sin formato Markdown.
        """;

    private final String apiKey;
    private final HttpClient http;
    private final ObjectMapper json;

    public AsistenteIA() {
        this.apiKey = System.getenv("GROQ_API_KEY");

        this.http = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(15))
            .build();

        this.json = new ObjectMapper();
    }

    public boolean disponible() {
        return apiKey != null && !apiKey.isBlank();
    }

    public String preguntar(
        String contextoTorneo,
        List<Mensaje> historial,
        String pregunta
    ) {

        if (!disponible()) {
            throw new AsistenteIAException(
                "Falta configurar GROQ_API_KEY."
            );
        }

        if (pregunta == null || pregunta.isBlank()) {
            throw new AsistenteIAException(
                "Escribe una pregunta para el asistente."
            );
        }

        String cuerpo = construirCuerpo(
            contextoTorneo, historial, pregunta
        );

        for (int intento = 1; intento <= MAX_INTENTOS; intento++) {

            HttpResponse<String> respuesta = enviar(cuerpo);
            int estado = respuesta.statusCode();

            if (estado == 200) {
                return extraerTexto(respuesta.body());
            }

            System.err.println(
                "Groq respondio " + estado + ": "
                + respuesta.body()
            );

            if (estado == 401 || estado == 403) {
                throw new AsistenteIAException(
                    "La clave GROQ_API_KEY no es valida "
                    + "o no tiene permisos."
                );
            }

            if (estado == 404) {
                throw new AsistenteIAException(
                    "El modelo de Groq no esta disponible."
                );
            }

            if (estado == 400 || estado == 413) {
                throw new AsistenteIAException(
                    "Groq rechazo la solicitud. "
                    + "Revisa los datos enviados o su tamano."
                );
            }

            boolean temporal =
                estado == 429 ||
                estado == 500 ||
                estado == 502 ||
                estado == 503 ||
                estado == 504;

            if (!temporal) {
                throw new AsistenteIAException(
                    "Error de Groq. Codigo: " + estado
                );
            }

            if (intento < MAX_INTENTOS) {
                pausar(2000L * intento);
            } else {
                if (estado == 429) {
                    throw new AsistenteIAException(
                        "Se alcanzo el limite de uso de Groq. "
                        + "Espera e intenta nuevamente."
                    );
                }

                throw new AsistenteIAException(
                    "Groq no esta disponible temporalmente. "
                    + "Intenta de nuevo mas tarde."
                );
            }
        }

        throw new AsistenteIAException(
            "No fue posible obtener una respuesta."
        );
    }

    private String construirCuerpo(
        String contextoTorneo,
        List<Mensaje> historial,
        String pregunta
    ) {

        ObjectNode raiz = json.createObjectNode();

        raiz.put("model", MODELO);
        raiz.put("temperature", 0.4);
        raiz.put("max_completion_tokens", 500);
        raiz.put("stream", false);

        ArrayNode mensajes = raiz.putArray("messages");

        agregarMensaje(
            mensajes,
            "system",
            INSTRUCCIONES
            + "\nDATOS ACTUALES DEL TORNEO:\n"
            + (contextoTorneo == null ? "" : contextoTorneo)
        );

        if (historial != null) {

            int desde = Math.max(
                0, historial.size() - MAX_HISTORIAL
            );

            for (Mensaje mensaje :
                    historial.subList(desde, historial.size())) {

                if (mensaje == null ||
                    mensaje.texto() == null ||
                    mensaje.texto().isBlank()) {
                    continue;
                }

                String rol =
                    "model".equals(mensaje.rol()) ||
                    "assistant".equals(mensaje.rol())
                    ? "assistant" : "user";

                agregarMensaje(
                    mensajes, rol, mensaje.texto()
                );
            }
        }

        agregarMensaje(mensajes, "user", pregunta);

        try {
            return json.writeValueAsString(raiz);
        } catch (IOException e) {
            throw new AsistenteIAException(
                "No se pudo preparar la consulta.", e
            );
        }
    }

    private void agregarMensaje(
        ArrayNode mensajes,
        String rol,
        String contenido
    ) {
        ObjectNode mensaje = mensajes.addObject();
        mensaje.put("role", rol);
        mensaje.put("content", contenido);
    }

    private HttpResponse<String> enviar(String cuerpo) {

        HttpRequest peticion = HttpRequest.newBuilder()
            .uri(URI.create(URL))
            .timeout(Duration.ofSeconds(45))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + apiKey)
            .POST(HttpRequest.BodyPublishers.ofString(cuerpo))
            .build();

        try {
            return http.send(
                peticion,
                HttpResponse.BodyHandlers.ofString()
            );

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new AsistenteIAException(
                "La consulta fue interrumpida.", e
            );

        } catch (IOException e) {
            System.err.println(
                "Error de conexion con Groq: " + e
            );

            throw new AsistenteIAException(
                "No se pudo conectar con Groq. "
                + "Revisa la conexion.", e
            );
        }
    }

    private String extraerTexto(String cuerpo) {

        try {
            JsonNode raiz = json.readTree(cuerpo);

            String texto = raiz
                .path("choices")
                .path(0)
                .path("message")
                .path("content")
                .asText("");

            if (texto.isBlank()) {
                throw new AsistenteIAException(
                    "Groq devolvio una respuesta vacia."
                );
            }

            return texto.trim();

        } catch (IOException e) {
            throw new AsistenteIAException(
                "No se pudo leer la respuesta de Groq.", e
            );
        }
    }

    private void pausar(long milisegundos) {
        try {
            Thread.sleep(milisegundos);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new AsistenteIAException(
                "La consulta fue interrumpida.", e
            );
        }
    }
}
