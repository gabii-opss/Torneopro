package com.torneopro.Structures;

import com.torneopro.models.Equipo;
import com.torneopro.models.Jugador;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BracketTest {

    private Equipo equipoCompleto(String nombre) {
        Equipo equipo = new Equipo(nombre);
        for (int i = 1; i <= 5; i++) {
            equipo.agregarJugador(new Jugador("J" + i, nombre + i, "DPS"));
        }
        return equipo;
    }

    @Test
    void elGanadorDeLaFinalEsElCampeon() {
        Equipo halcones = equipoCompleto("Halcones");
        Equipo tigres = equipoCompleto("Tigres");
        Equipo lobos = equipoCompleto("Lobos");
        Equipo aguilas = equipoCompleto("Aguilas");

        Bracket bracket = new Bracket(List.of(halcones, tigres, lobos, aguilas));

        NodoBracket semi1 = bracket.getRaiz().getIzquierdo();
        NodoBracket semi2 = bracket.getRaiz().getDerecho();

        bracket.registrarGanador(semi1, halcones);
        bracket.registrarGanador(semi2, lobos);
        bracket.registrarGanador(bracket.getRaiz(), halcones);

        assertEquals(halcones, bracket.getCampeon());
    }

    @Test
    void unBracketConUnSoloEquipoLanzaExcepcion() {
        Equipo halcones = equipoCompleto("Halcones");
        assertThrows(IllegalArgumentException.class, () -> new Bracket(List.of(halcones)));
    }
}