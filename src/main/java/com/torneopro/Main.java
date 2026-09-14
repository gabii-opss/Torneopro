package com.orneopro;

import com.torneopro.models.Equipo;
import com.torneopro.models.Jugador;
import com.torneopro.models.Torneo;

public class Main {
    public static void main(String[] args) {
        Jugador j1 = new Jugador("Ana", "AnaFPS", "DPS");
        Equipo equipo = new Equipo("Los Halcones");
        equipo.agregarJugador(j1);
        System.out.println(equipo);
        System.out.println(j1);

        Torneo torneo = new Torneo("Copa Otonio");
        System.out.println(torneo);
    }
}