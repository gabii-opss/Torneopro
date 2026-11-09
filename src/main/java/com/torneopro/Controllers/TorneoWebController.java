package com.torneopro.Controllers;

import com.torneopro.Exceptions.TorneoProException;
import com.torneopro.models.Equipo;
import com.torneopro.models.Jugador;
import com.torneopro.models.Partido;
import com.torneopro.models.Torneo;
import com.torneopro.Persistence.EquipoRepositorio;
import com.torneopro.Services.GestorTorneo;
import com.torneopro.Structures.Bracket;
import com.torneopro.Structures.HistorialPartido;
import com.torneopro.Structures.NodoBracket;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.staticfiles.Location;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Version web de TorneoController: en vez de leer del teclado, recibe JSON por HTTP
 * y responde JSON. Reutiliza Torneo, GestorTorneo y EquipoRepositorio sin cambios.
 * El HTML se sirve desde src/main/resources/static/.
 */
public class TorneoWebController {

    // DTOs de entrada
    public record JugadorDTO(String gamertag, String rol) { }
    public record EquipoDTO(String nombre, List<JugadorDTO> jugadores) { }
    public record ResultadoDTO(int partido, String ganador) { }

    private final Torneo torneo;
    private final GestorTorneo gestorTorneo;
    private final EquipoRepositorio equipoRepositorio;
    private final HistorialPartido historial = new HistorialPartido();
    private final Map<Partido, String> rondas = new IdentityHashMap<>();
    private Bracket bracket;

    public TorneoWebController(Torneo torneo, GestorTorneo gestorTorneo,
                               EquipoRepositorio equipoRepositorio) {
        this.torneo = torneo;
        this.gestorTorneo = gestorTorneo;
        this.equipoRepositorio = equipoRepositorio;
    }

    public Javalin iniciar(int puerto) {
        Javalin app = Javalin.create(config ->
            config.staticFiles.add("/static", Location.CLASSPATH));

        app.get("/api/torneo", this::verTorneo);
        app.get("/api/equipos", this::listarEquipos);
        app.post("/api/equipos", this::inscribirEquipo);
        app.post("/api/bracket", this::generarBracket);
        app.get("/api/bracket", this::verBracket);
        app.get("/api/partidos/pendientes", this::partidosPendientes);
        app.post("/api/resultados", this::registrarResultado);
        app.get("/api/historial", this::verHistorial);

        // Errores de negocio (equipo incompleto, torneo iniciado, ...) -> 400 con mensaje
        app.exception(TorneoProException.class, (e, ctx) ->
            ctx.status(400).json(Map.of("error", e.getMessage())));
        app.exception(IllegalArgumentException.class, (e, ctx) ->
            ctx.status(400).json(Map.of("error", e.getMessage())));
        app.exception(IllegalStateException.class, (e, ctx) ->
            ctx.status(400).json(Map.of("error", e.getMessage())));

        app.start(puerto);
        System.out.println("Servidor listo en http://localhost:" + puerto);
        return app;
    }

    // ---- Endpoints (synchronized: Javalin atiende peticiones en varios hilos) ----

    private synchronized void verTorneo(Context ctx) {
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("nombre", torneo.getNombre());
        r.put("equipos", torneo.getEquipos().size());
        r.put("bracketGenerado", bracket != null);
        ctx.json(r);
    }

    private synchronized void listarEquipos(Context ctx) {
        List<Map<String, Object>> lista = new ArrayList<>();
        for (Equipo e : torneo.getEquipos()) {
            lista.add(equipoAMapa(e));
        }
        ctx.json(lista);
    }

    private synchronized void inscribirEquipo(Context ctx) {
        EquipoDTO dto = ctx.bodyAsClass(EquipoDTO.class);
        if (dto.nombre() == null || dto.nombre().isBlank()) {
            ctx.status(400).json(Map.of("error", "El nombre del equipo es obligatorio."));
            return;
        }
        boolean existe = torneo.getEquipos().stream()
            .anyMatch(e -> e.getNombre().equalsIgnoreCase(dto.nombre().trim()));
        if (existe) {
            ctx.status(409).json(Map.of("error", "Ya existe un equipo con ese nombre."));
            return;
        }

        Equipo equipo = new Equipo(dto.nombre().trim());
        if (dto.jugadores() != null) {
            for (JugadorDTO j : dto.jugadores()) {
                equipo.agregarJugador(new Jugador(j.gamertag(), j.gamertag(), j.rol()));
            }
        }

        gestorTorneo.inscribirEquipo(equipo); // valida que tenga los 5 jugadores

        Map<String, Object> r = equipoAMapa(equipo);
        try {
            equipoRepositorio.guardar(equipo);
            r.put("guardadoEnBD", true);
        } catch (RuntimeException e) {
            r.put("guardadoEnBD", false);
            r.put("aviso", "Inscrito en esta sesion, pero no se pudo guardar en la base de datos "
                + "(gamertag o nombre repetido de una ejecucion anterior).");
        }
        ctx.status(201).json(r);
    }

    private synchronized void generarBracket(Context ctx) {
        if (torneo.getEquipos().size() < 2) {
            ctx.status(400).json(Map.of("error", "Necesitas al menos 2 equipos para generar el bracket."));
            return;
        }
        bracket = new Bracket(torneo.getEquipos().stream().toList());
        ctx.status(201).json(bracketAMapa());
    }

    private synchronized void verBracket(Context ctx) {
        if (bracket == null) {
            ctx.status(404).json(Map.of("error", "Aun no se ha generado el bracket."));
            return;
        }
        ctx.json(bracketAMapa());
    }

    private synchronized void partidosPendientes(Context ctx) {
        if (bracket == null) {
            ctx.status(404).json(Map.of("error", "Aun no se ha generado el bracket."));
            return;
        }
        List<NodoBracket> pendientes = obtenerPartidosPendientes(bracket.getRaiz(), new ArrayList<>());
        List<Map<String, Object>> lista = new ArrayList<>();
        for (int i = 0; i < pendientes.size(); i++) {
            Map<String, Object> m = partidoAMapa(pendientes.get(i).getPartido());
            m.put("numero", i + 1); // es el valor que se envia como "partido" en POST /api/resultados
            lista.add(m);
        }
        ctx.json(lista);
    }

    private synchronized void registrarResultado(Context ctx) {
        if (bracket == null) {
            ctx.status(404).json(Map.of("error", "Aun no se ha generado el bracket."));
            return;
        }
        ResultadoDTO dto = ctx.bodyAsClass(ResultadoDTO.class);
        List<NodoBracket> pendientes = obtenerPartidosPendientes(bracket.getRaiz(), new ArrayList<>());
        int indice = dto.partido() - 1;
        if (indice < 0 || indice >= pendientes.size()) {
            ctx.status(400).json(Map.of("error", "Numero de partido fuera de rango."));
            return;
        }

        NodoBracket nodo = pendientes.get(indice);
        Partido partido = nodo.getPartido();
        Equipo ganador = null;
        if (partido.getEquipoA().getNombre().equals(dto.ganador())) {
            ganador = partido.getEquipoA();
        } else if (partido.getEquipoB() != null && partido.getEquipoB().getNombre().equals(dto.ganador())) {
            ganador = partido.getEquipoB();
        }
        if (ganador == null) {
            ctx.status(400).json(Map.of("error", "Ese nombre no coincide con ninguno de los dos equipos del partido."));
            return;
        }

        bracket.registrarGanador(nodo, ganador);
        historial.agregar(partido);
        rondas.put(partido, nombreRonda(nodo));

        Map<String, Object> r = new LinkedHashMap<>();
        r.put("ganador", ganador.getNombre());
        r.put("campeon", bracket.getCampeon() != null ? bracket.getCampeon().getNombre() : null);
        ctx.json(r);
    }

    private synchronized void verHistorial(Context ctx) {
        List<Map<String, Object>> lista = new ArrayList<>();
        for (Partido p : historial.comoLista()) {
            lista.add(partidoAMapa(p));
        }
        ctx.json(lista);
    }

    // ---- Conversion a JSON (se evita serializar los objetos del dominio: NodoBracket tiene ciclos por "padre") ----

    private Map<String, Object> equipoAMapa(Equipo e) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("nombre", e.getNombre());
        m.put("victorias", e.getVictorias());
        m.put("derrotas", e.getDerrotas());
        List<Map<String, String>> jugadores = new ArrayList<>();
        for (Jugador j : e.getJugadores()) {
            jugadores.add(Map.of("gamertag", j.getGamertag(), "rol", j.getRol()));
        }
        m.put("jugadores", jugadores);
        return m;
    }

    private Map<String, Object> partidoAMapa(Partido p) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("equipoA", p.getEquipoA().getNombre());
        m.put("equipoB", p.getEquipoB() != null ? p.getEquipoB().getNombre() : "BYE");
        m.put("estado", p.getEstado().name());
        m.put("ganador", p.getGanador() != null ? p.getGanador().getNombre() : null);
        m.put("ronda", rondas.get(p));
        return m;
    }

    private Map<String, Object> bracketAMapa() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("raiz", nodoAMapa(bracket.getRaiz()));
        m.put("campeon", bracket.getCampeon() != null ? bracket.getCampeon().getNombre() : null);
        return m;
    }

    private Map<String, Object> nodoAMapa(NodoBracket n) {
        if (n == null) {
            return null;
        }
        Map<String, Object> m = new LinkedHashMap<>();
        Equipo resultante = n.getEquipoResultante();
        m.put("resultante", resultante != null ? resultante.getNombre() : null);
        if (n.esHoja()) {
            m.put("equipo", n.getEquipoResultante().getNombre());
        } else {
            m.put("partido", n.getPartido() != null ? partidoAMapa(n.getPartido()) : null);
            m.put("izquierdo", nodoAMapa(n.getIzquierdo()));
            m.put("derecho", nodoAMapa(n.getDerecho()));
        }
        return m;
    }

    /** La ronda se saca de la distancia del nodo a la raiz (0 = final). */
    private String nombreRonda(NodoBracket nodo) {
        int nivel = 0;
        for (NodoBracket n = nodo.getPadre(); n != null; n = n.getPadre()) {
            nivel++;
        }
        return switch (nivel) {
            case 0 -> "Final";
            case 1 -> "Semifinal";
            case 2 -> "Cuartos";
            case 3 -> "Octavos";
            default -> "Ronda previa";
        };
    }

    private List<NodoBracket> obtenerPartidosPendientes(NodoBracket nodo, List<NodoBracket> acumulado) {
        if (nodo == null || nodo.esHoja()) {
            return acumulado;
        }
        obtenerPartidosPendientes(nodo.getIzquierdo(), acumulado);
        if (nodo.getPartido() != null
            && nodo.getPartido().getEstado() != Partido.EstadoPartido.FINALIZADO) {
            acumulado.add(nodo);
        }
        obtenerPartidosPendientes(nodo.getDerecho(), acumulado);
        return acumulado;
    }
}
