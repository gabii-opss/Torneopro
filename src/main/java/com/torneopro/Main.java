package com.torneopro;

import com.torneopro.Exceptions.EquipoIncompletoException;
import com.torneopro.models.Equipo;
import com.torneopro.models.Jugador;
import com.torneopro.models.Torneo;
import com.torneopro.Services.GestorTorneo;
import com.torneopro.Services.NotificadorConsola;

public class Main {
    public static void main(String[] args) {
        Torneo torneo = new Torneo("Copa Otonio");
        GestorTorneo gestor = new GestorTorneo(torneo, new NotificadorConsola());

        Equipo halcones = new Equipo("Los Halcones");
        halcones.agregarJugador(new Jugador("Ana", "AnaFPS", "DPS"));
        halcones.agregarJugador(new Jugador("Luis", "LuisTank", "Tank"));
        halcones.agregarJugador(new Jugador("Marta", "MartaSup", "Support"));
        halcones.agregarJugador(new Jugador("Pedro", "PedroDPS", "DPS"));
        halcones.agregarJugador(new Jugador("Eva", "EvaFlex", "Flex"));

        gestor.inscribirEquipo(halcones);

        // Probamos que la excepcion se lance con un equipo incompleto
        Equipo incompleto = new Equipo("Los Novatos");
        incompleto.agregarJugador(new Jugador("Tom", "TomNoob", "DPS"));

        try {
            gestor.inscribirEquipo(incompleto);
        } catch (EquipoIncompletoException e) {
            System.out.println("Error esperado: " + e.getMessage());
        }
    }
}