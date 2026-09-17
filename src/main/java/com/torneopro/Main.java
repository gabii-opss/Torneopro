package com.torneopro;

import com.torneopro.models.Equipo;
import com.torneopro.models.Jugador;
import com.torneopro.Structures.Bracket;
import com.torneopro.Structures.HistorialPartido;
import com.torneopro.Structures.NodoBracket;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Equipo halcones = crearEquipoCompleto("Los Halcones");
        Equipo tigres = crearEquipoCompleto("Los Tigres");
        Equipo lobos = crearEquipoCompleto("Los Lobos");
        Equipo aguilas = crearEquipoCompleto("Las Aguilas");

        Bracket bracket = new Bracket(List.of(halcones, tigres, lobos, aguilas));

        NodoBracket semi1 = bracket.getRaiz().getIzquierdo();
        NodoBracket semi2 = bracket.getRaiz().getDerecho();

        bracket.registrarGanador(semi1, halcones);
        bracket.registrarGanador(semi2, lobos);
        bracket.registrarGanador(bracket.getRaiz(), halcones);

        System.out.println("Campeon: " + bracket.getCampeon());

        HistorialPartido historial = new HistorialPartido();
        historial.agregar(semi1.getPartido());
        historial.agregar(semi2.getPartido());
        historial.agregar(bracket.getRaiz().getPartido());
        System.out.println("Partidas en el historial: " + historial.getTamano());
        historial.comoLista().forEach(System.out::println);
    }

    private static Equipo crearEquipoCompleto(String nombre) {
        Equipo equipo = new Equipo(nombre);
        for (int i = 1; i <= Equipo.MAX_JUGADORES; i++) {
            equipo.agregarJugador(new Jugador("Jugador" + i, nombre.replace(" ", "") + i, "Flex"));
        }
        return equipo;
    }
}