package com.torneopro.Services;


import com.torneopro.Exceptions.EquipoIncompletoException;
import com.torneopro.Exceptions.TorneoYaIniciadoException;
import com.torneopro.models.Equipo;
import com.torneopro.models.Torneo;

public class GestorTorneo {
    private final Torneo torneo;
    private final Notificador notificador;

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
        notificador.notificar(equipo.getNombre(), "Inscripcion confirmada al torneo " + torneo.getNombre());
    }
}