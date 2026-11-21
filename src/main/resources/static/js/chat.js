// TorneoPro · Chat con el asistente de IA (usa POST /api/chat)

(function () {
  const fab = document.getElementById("chatFab");
  const panel = document.getElementById("chatPanel");
  const cerrar = document.getElementById("chatClose");
  const mensajes = document.getElementById("chatMsgs");
  const sugerencias = document.getElementById("chatSug");
  const form = document.getElementById("chatForm");
  const entrada = document.getElementById("chatInput");
  const boton = document.getElementById("chatSend");

  // conversación que se envía al backend en cada pregunta
  const historial = [];
  let esperando = false;

  function abrir() {
    panel.hidden = false;
    fab.setAttribute("aria-expanded", "true");
    if (mensajes.children.length === 0) {
      agregar("bot", "Hola, soy el asistente de TorneoPro. Puedo contarte cómo van los equipos, quién juega y qué equipo parece favorito.");
    }
    entrada.focus();
  }

  function cerrarPanel() {
    panel.hidden = true;
    fab.setAttribute("aria-expanded", "false");
  }

  fab.addEventListener("click", () => (panel.hidden ? abrir() : cerrarPanel()));
  cerrar.addEventListener("click", cerrarPanel);

  // agrega una burbuja (textContent evita meter HTML por accidente)
  function agregar(tipo, texto) {
    const burbuja = document.createElement("div");
    burbuja.className = "chat__msg chat__msg--" + tipo;
    burbuja.textContent = texto;
    mensajes.appendChild(burbuja);
    mensajes.scrollTop = mensajes.scrollHeight;
    return burbuja;
  }

  async function enviar(pregunta) {
    if (esperando || !pregunta.trim()) return;
    esperando = true;
    boton.disabled = true;
    sugerencias.hidden = true;

    agregar("user", pregunta);
    const escribiendo = agregar("bot", "Escribiendo...");
    escribiendo.classList.add("chat__msg--espera");

    try {
      const res = await fetch("/api/chat", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ mensaje: pregunta, historial: historial }),
      });
      const datos = await res.json().catch(() => ({}));
      if (!res.ok) throw new Error(datos.error || "Error en el servidor");

      escribiendo.remove();
      agregar("bot", datos.respuesta);
      historial.push({ rol: "user", texto: pregunta });
      historial.push({ rol: "model", texto: datos.respuesta });
    } catch (err) {
      escribiendo.remove();
      const aviso = agregar("bot", err.message || "No se pudo contactar al asistente.");
      aviso.classList.add("chat__msg--error");
    } finally {
      esperando = false;
      boton.disabled = false;
      entrada.focus();
    }
  }

  form.addEventListener("submit", (e) => {
    e.preventDefault();
    const pregunta = entrada.value;
    entrada.value = "";
    enviar(pregunta);
  });

  sugerencias.querySelectorAll("button").forEach((b) => {
    b.addEventListener("click", () => enviar(b.textContent));
  });
})();
