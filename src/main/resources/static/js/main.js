// TorneoPro · Interfaz conectada al backend (API en /api/...)

document.addEventListener("DOMContentLoaded", () => {
  // ---- Menú móvil ----
  const navToggle = document.getElementById("navToggle");
  const nav = document.getElementById("nav");

  navToggle.addEventListener("click", () => {
    const open = nav.classList.toggle("is-open");
    navToggle.setAttribute("aria-expanded", String(open));
  });

  nav.querySelectorAll("a").forEach((link) => {
    link.addEventListener("click", () => {
      nav.classList.remove("is-open");
      navToggle.setAttribute("aria-expanded", "false");
    });
  });

  // ---- Resaltar el enlace de la sección visible ----
  const links = [...nav.querySelectorAll(".nav__link:not(.nav__link--cta)")];
  const sections = links
    .map((l) => document.querySelector(l.getAttribute("href")))
    .filter(Boolean);

  const observer = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          links.forEach((l) =>
            l.classList.toggle("is-active", l.getAttribute("href") === "#" + entry.target.id)
          );
        }
      });
    },
    { rootMargin: "-40% 0px -55% 0px" }
  );
  sections.forEach((s) => observer.observe(s));

  // ---- Llamadas a la API ----
  async function api(url, opciones) {
    const res = await fetch(url, opciones);
    const datos = await res.json().catch(() => ({}));
    if (!res.ok) throw new Error(datos.error || "Error en el servidor");
    return datos;
  }

  function enviar(url, cuerpo) {
    return api(url, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(cuerpo || {}),
    });
  }

  // crea un elemento con clase y texto (textContent evita meter HTML por accidente)
  function crear(tag, clase, texto) {
    const el = document.createElement(tag);
    if (clase) el.className = clase;
    if (texto !== undefined) el.textContent = texto;
    return el;
  }

  const letras = ["a", "b", "c"];
  function badge(nombre, i) {
    return crear("span", "badge badge--" + letras[i % 3], nombre.slice(0, 2).toUpperCase());
  }

  // ---- Contadores ----
  function contar(el, objetivo) {
    const duracion = 900;
    const inicio = performance.now();
    const tick = (ahora) => {
      const progreso = Math.min((ahora - inicio) / duracion, 1);
      el.textContent = Math.round(objetivo * progreso);
      if (progreso < 1) requestAnimationFrame(tick);
    };
    requestAnimationFrame(tick);
  }

  // ---- Equipos ----
  const teamsBox = document.getElementById("teams");
  const addBtn = document.getElementById("addTeamBtn");

  function pintarEquipos(equipos) {
    teamsBox.querySelectorAll(".team:not(.team--add)").forEach((t) => t.remove());

    equipos.forEach((equipo, i) => {
      const card = crear("article", "team");
      const info = document.createElement("div");
      info.appendChild(crear("h3", "", equipo.nombre));
      info.appendChild(crear("p", "", equipo.jugadores.length + " jugadores · Completo"));

      const lista = crear("ul", "players");
      equipo.jugadores.forEach((j) => {
        const li = document.createElement("li");
        li.appendChild(crear("b", "", j.gamertag));
        li.appendChild(document.createTextNode(" "));
        li.appendChild(crear("span", "", j.rol));
        lista.appendChild(li);
      });
      info.appendChild(lista);

      card.appendChild(badge(equipo.nombre, i));
      card.appendChild(info);
      teamsBox.insertBefore(card, addBtn);
    });
  }

  // ---- Bracket ----
  const bracketBox = document.getElementById("bracketBox");

  function tituloRonda(nivel) {
    if (nivel === 0) return "Final";
    if (nivel === 1) return "Semifinales";
    if (nivel === 2) return "Cuartos de final";
    if (nivel === 3) return "Octavos de final";
    return "Ronda previa";
  }

  // recorre el árbol y junta los cruces por nivel (0 = final)
  function juntarCruces(nodo, nivel, niveles) {
    if (!nodo || !nodo.izquierdo) return;
    if (nodo.derecho) {
      if (!niveles[nivel]) niveles[nivel] = [];
      niveles[nivel].push(nodo);
    }
    juntarCruces(nodo.izquierdo, nivel + 1, niveles);
    juntarCruces(nodo.derecho, nivel + 1, niveles);
  }

  function pintarSlot(nombre, cruce) {
    const gano = cruce.partido && cruce.partido.ganador === nombre;
    const slot = crear("div", "slot" + (gano ? " slot--win" : ""));
    slot.appendChild(crear("span", "", nombre || "Por definir"));
    slot.appendChild(crear("b", "", gano ? "✓" : "–"));

    // solo se puede elegir ganador si el partido ya tiene los dos equipos y no se ha jugado
    const p = cruce.partido;
    if (p && p.estado !== "FINALIZADO" && nombre) {
      slot.classList.add("slot--click");
      slot.title = "Marcar como ganador";
      slot.addEventListener("click", () => registrarGanador(p, nombre));
    }
    return slot;
  }

  function pintarBracket(datos) {
    bracketBox.innerHTML = "";
    const niveles = [];
    juntarCruces(datos.raiz, 0, niveles);

    // de la primera ronda hacia la final
    for (let nivel = niveles.length - 1; nivel >= 0; nivel--) {
      const ronda = crear("div", "round");
      ronda.appendChild(crear("h3", "round__title", tituloRonda(nivel)));

      niveles[nivel].forEach((cruce) => {
        const tie = crear("div", "tie" + (nivel === 0 ? " tie--final" : ""));
        tie.appendChild(pintarSlot(cruce.izquierdo.resultante, cruce));
        tie.appendChild(pintarSlot(cruce.derecho.resultante, cruce));
        ronda.appendChild(tie);
      });
      bracketBox.appendChild(ronda);
    }

    if (datos.campeon) {
      const ronda = crear("div", "round");
      ronda.appendChild(crear("h3", "round__title", "Campeón"));
      const tie = crear("div", "tie tie--final");
      const slot = crear("div", "slot slot--win");
      slot.appendChild(crear("span", "", datos.campeon));
      slot.appendChild(crear("b", "", "★"));
      tie.appendChild(slot);
      ronda.appendChild(tie);
      bracketBox.appendChild(ronda);
    }
  }

  async function registrarGanador(partido, ganador) {
    if (!confirm("¿Registrar a " + ganador + " como ganador?")) return;
    try {
      // el backend numera los partidos pendientes; buscamos el que tenga estos dos equipos
      const pendientes = await api("/api/partidos/pendientes");
      const buscado = pendientes.find(
        (p) => p.equipoA === partido.equipoA && p.equipoB === partido.equipoB
      );
      if (!buscado) throw new Error("No se encontró el partido pendiente.");
      await enviar("/api/resultados", { partido: buscado.numero, ganador: ganador });
      cargarTodo();
    } catch (err) {
      alert(err.message);
    }
  }

  document.getElementById("genBracketBtn").addEventListener("click", async () => {
    try {
      await enviar("/api/bracket");
      cargarTodo();
    } catch (err) {
      alert(err.message);
    }
  });

  // ---- Historial ----
  const historyList = document.getElementById("history");

  function claveRonda(ronda) {
    if (ronda === "Cuartos") return "cuartos";
    if (ronda === "Semifinal") return "semis";
    if (ronda === "Final") return "final";
    return "otra";
  }

  function pintarHistorial(partidos) {
    historyList.innerHTML = "";
    if (partidos.length === 0) {
      historyList.appendChild(crear("li", "empty", "Todavía no hay partidos jugados."));
      return;
    }

    // los más recientes primero
    [...partidos].reverse().forEach((p, i) => {
      const li = crear("li", "entry");
      li.dataset.round = claveRonda(p.ronda);
      li.appendChild(crear("span", "entry__round", p.ronda || "Partido"));

      const equipos = crear("span", "entry__teams");
      const a = crear(p.ganador === p.equipoA ? "strong" : "span", "", p.equipoA);
      const b = crear(p.ganador === p.equipoB ? "strong" : "span", "", p.equipoB);
      equipos.append(a, crear("b", "", "vs"), b);
      li.appendChild(equipos);

      li.appendChild(crear("span", "entry__time", "Partido " + (partidos.length - i)));
      historyList.appendChild(li);
    });
    aplicarFiltro();
  }

  // filtro por ronda
  const chips = document.querySelectorAll(".chip");
  let filtroActual = "all";

  function aplicarFiltro() {
    historyList.querySelectorAll(".entry").forEach((e) => {
      e.classList.toggle("is-hidden", filtroActual !== "all" && e.dataset.round !== filtroActual);
    });
  }

  chips.forEach((chip) => {
    chip.addEventListener("click", () => {
      chips.forEach((c) => c.classList.remove("is-active"));
      chip.classList.add("is-active");
      filtroActual = chip.dataset.filter;
      aplicarFiltro();
    });
  });

  // ---- Próximo enfrentamiento (tarjeta de inicio) ----
  function pintarProximo(pendientes) {
    const label = document.getElementById("heroLabel");
    const meta = document.getElementById("heroMeta");
    const nombreA = document.getElementById("heroA");
    const nombreB = document.getElementById("heroB");
    const badgeA = document.getElementById("heroBadgeA");
    const badgeB = document.getElementById("heroBadgeB");

    if (pendientes.length === 0) {
      label.textContent = "Próximo enfrentamiento";
      nombreA.textContent = "Por definir";
      nombreB.textContent = "Por definir";
      badgeA.textContent = "?";
      badgeB.textContent = "?";
      meta.textContent = "Aún no hay partidos pendientes";
      return;
    }
    const p = pendientes[0];
    label.textContent = "Próximo enfrentamiento";
    nombreA.textContent = p.equipoA;
    nombreB.textContent = p.equipoB;
    badgeA.textContent = p.equipoA.slice(0, 2).toUpperCase();
    badgeB.textContent = p.equipoB.slice(0, 2).toUpperCase();
    meta.textContent = pendientes.length + " partido(s) pendiente(s)";
  }

  // ---- Cargar todo desde el backend ----
  async function cargarTodo() {
    const equipos = await api("/api/equipos");
    const historial = await api("/api/historial");

    let bracket = null;
    let pendientes = [];
    try {
      bracket = await api("/api/bracket"); // 404 si todavía no se genera
      pendientes = await api("/api/partidos/pendientes");
    } catch (err) {
      bracket = null;
    }

    pintarEquipos(equipos);
    pintarHistorial(historial);
    pintarProximo(pendientes);

    if (bracket) {
      pintarBracket(bracket);
    } else {
      bracketBox.innerHTML = "";
      bracketBox.appendChild(crear("p", "empty", "Todavía no se ha generado el bracket."));
    }

    const niveles = [];
    if (bracket) juntarCruces(bracket.raiz, 0, niveles);
    const totalPartidos = niveles.reduce((suma, n) => suma + n.length, 0);

    contar(document.getElementById("statEquipos"), equipos.length);
    contar(document.getElementById("statJugadores"), equipos.length * 5);
    contar(document.getElementById("statPartidos"), totalPartidos);
    contar(document.getElementById("statJugados"), historial.length);
  }

  // ---- Botón "Inscribir equipo": lleva al formulario ----
  addBtn.addEventListener("click", () => {
    document.getElementById("inscribir").scrollIntoView({ behavior: "smooth" });
    document.getElementById("teamName").focus({ preventScroll: true });
  });

  // ---- Formulario de inscripción ----
  // Regla del proyecto: un equipo debe tener exactamente 5 jugadores.
  const form = document.getElementById("teamForm");
  const msg = document.getElementById("formMsg");

  function mostrarMensaje(texto, ok) {
    msg.classList.toggle("is-error", !ok);
    msg.classList.toggle("is-ok", ok);
    msg.textContent = texto;
  }

  form.addEventListener("submit", async (e) => {
    e.preventDefault();

    const nombre = form.teamName.value.trim();
    const filas = [...form.querySelectorAll(".player-row")];
    const completo = filas.every((fila) =>
      [...fila.querySelectorAll("input")].every((i) => i.value.trim() !== "")
    );

    if (!nombre) {
      mostrarMensaje("Escribe el nombre del equipo.", false);
      return;
    }
    if (!completo) {
      mostrarMensaje("El equipo debe tener 5 jugadores, cada uno con gamertag y rol.", false);
      return;
    }

    const jugadores = filas.map((fila) => {
      const campos = fila.querySelectorAll("input");
      return { gamertag: campos[0].value.trim(), rol: campos[1].value.trim() };
    });

    try {
      const equipo = await enviar("/api/equipos", { nombre: nombre, jugadores: jugadores });
      mostrarMensaje(equipo.aviso || 'Equipo "' + nombre + '" inscrito correctamente.', !equipo.aviso);
      form.reset();
      cargarTodo();
    } catch (err) {
      mostrarMensaje(err.message, false);
    }
  });

  cargarTodo().catch(() => {
    mostrarMensaje("No se pudo conectar con el servidor.", false);
  });
});
