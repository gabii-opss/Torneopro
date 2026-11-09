package com.torneopro.models;

import com.torneopro.Exceptions.EquipoCompletoException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EquipoTest {

    @Test
    void unEquipoNuevoNoEstaCompleto() {
        Equipo equipo = new Equipo("Los Halcones");
        assertFalse(equipo.estaCompleto());
    }

    @Test
    void agregarCincoJugadoresCompletaElEquipo() {
        Equipo equipo = new Equipo("Los Halcones");
        for (int i = 1; i <= 5; i++) {
            equipo.agregarJugador(new Jugador("J" + i, "tag" + i, "DPS"));
        }
        assertTrue(equipo.estaCompleto());
    }

    @Test
    void agregarUnSextoJugadorLanzaExcepcion() {
        Equipo equipo = new Equipo("Los Halcones");
        for (int i = 1; i <= 5; i++) {
            equipo.agregarJugador(new Jugador("J" + i, "tag" + i, "DPS"));
        }
        Jugador sexto = new Jugador("J6", "tag6", "DPS");

        assertThrows(EquipoCompletoException.class, () -> equipo.agregarJugador(sexto));
    }

    @Test
    void noSePuedeAgregarElMismoJugadorDosVeces() {
        Equipo equipo = new Equipo("Los Halcones");
        Jugador jugador = new Jugador("Ana", "AnaFPS", "DPS");
        equipo.agregarJugador(jugador);

        assertThrows(IllegalArgumentException.class, () -> equipo.agregarJugador(jugador));
    }
}
