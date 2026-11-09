package com.torneopro.Services;

import com.torneopro.Exceptions.EquipoIncompletoException;
import com.torneopro.Exceptions.TorneoYaIniciadoException;
import com.torneopro.models.Equipo;
import com.torneopro.models.Torneo;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;

public class GestorTorneo {
    private final Torneo torneo;
    private final Notificador notificador;
    private final Queue<Equipo> listaEspera = new LinkedList<>();
    private final Deque<String> historialAcciones = new ArrayDeque<>();

    public GestorTorneo(Torneo torneo, Notificador notificador) {
        this.torneo = torneo;
        this.notificador = notificador;
    }

    public void inscribirEquipo(Equipo equipo) {
        if (torneo.isIniciado()) {
            throw new TorneoYaIniciadoException(
                "No se puede inscribir a '" + equipo.getNombre() + "': el torneo ya inicio."
            );
        }
        if (!equipo.estaCompleto()) {
            throw new EquipoIncompletoException(
                "El equipo '" + equipo.getNombre() + "' no tiene los " + Equipo.MAX_JUGADORES + " jugadores requeridos."
            );
        }
        torneo.inscribirEquipo(equipo);
        historialAcciones.push("Inscripcion de " + equipo.getNombre());
        notificador.notificar(equipo.getNombre(), "Inscripcion confirmada al torneo " + torneo.getNombre());
    }

    /** Cola FIFO: equipos que llegan tarde o exceden el cupo esperan aqui. */
    public void agregarAListaEspera(Equipo equipo) {
        listaEspera.offer(equipo);
        historialAcciones.push("Equipo " + equipo.getNombre() + " agregado a lista de espera");
    }

    public Equipo procesarSiguienteEnEspera() {
        Equipo equipo = listaEspera.poll();
        if (equipo != null) {
            historialAcciones.push("Procesado " + equipo.getNombre() + " desde lista de espera");
        }
        return equipo;
    }

    public int tamanoListaEspera() {
        return listaEspera.size();
    }

    /** Pila LIFO: deshace la ultima accion registrada (a nivel de bitacora). */
    public String deshacerUltimaAccion() {
        if (historialAcciones.isEmpty()) {
            return "No hay acciones para deshacer.";
        }
        return "Deshecho: " + historialAcciones.pop();
    }
}