package com.torneopro.models;

import com.torneopro.Exceptions.EquipoIncompletoException;
import com.torneopro.Exceptions.TorneoYaIniciadoException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TorneoTest {

    private Equipo equipoCompleto(String nombre) {
        Equipo equipo = new Equipo(nombre);
        for (int i = 1; i <= 5; i++) {
            equipo.agregarJugador(new Jugador("J" + i, nombre + i, "DPS"));
        }
        return equipo;
    }

    @Test
    void noSePuedeInscribirUnEquipoIncompleto() {
        Torneo torneo = new Torneo("Copa Otonio");
        Equipo incompleto = new Equipo("Los Novatos");
        incompleto.agregarJugador(new Jugador("Tom", "TomNoob", "DPS"));

        assertThrows(EquipoIncompletoException.class, () -> torneo.inscribirEquipo(incompleto));
    }

    @Test
    void noSePuedeIniciarConMenosDeDosEquipos() {
        Torneo torneo = new Torneo("Copa Otonio");
        torneo.inscribirEquipo(equipoCompleto("Halcones"));

        assertThrows(IllegalStateException.class, torneo::iniciar);
    }

    @Test
    void noSePuedeInscribirDespuesDeIniciado() {
        Torneo torneo = new Torneo("Copa Otonio");
        torneo.inscribirEquipo(equipoCompleto("Halcones"));
        torneo.inscribirEquipo(equipoCompleto("Tigres"));
        torneo.iniciar();

        assertThrows(TorneoYaIniciadoException.class, () -> torneo.inscribirEquipo(equipoCompleto("Lobos")));
    }
}