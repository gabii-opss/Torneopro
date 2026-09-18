package com.torneopro;

import com.torneopro.Controllers.TorneoController;
import com.torneopro.models.Equipo;
import com.torneopro.models.Jugador;
import com.torneopro.models.Partido;
import com.torneopro.models.Torneo;
import com.torneopro.Services.GestorNotificaciones;
import com.torneopro.Services.GestorTorneo;
import com.torneopro.Services.NotificadorConsola;
import com.torneopro.Services.SimuladorMarcador;
import com.torneopro.Views.ConsolaView;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Main {
    public static void main(String[] args) {
        Torneo torneo = new Torneo("Copa Otonio");
        GestorTorneo gestorTorneo = new GestorTorneo(torneo, new NotificadorConsola());
        ConsolaView view = new ConsolaView();
        TorneoController controller = new TorneoController(view, torneo, gestorTorneo);

        controller.iniciar();

        // --- Sesion 6: concurrencia ---
        Equipo halconesDemo = crearEquipoDemo("Halcones Demo");
        Equipo tigresDemo = crearEquipoDemo("Tigres Demo");

        SimuladorMarcador simulador = new SimuladorMarcador();
        Partido partidoEnVivo = new Partido(halconesDemo, tigresDemo);
        partidoEnVivo.iniciar();

        System.out.println("Iniciando simulacion en vivo (no bloquea el hilo principal)...");
        CompletableFuture<Void> simulacion = simulador.simular(partidoEnVivo, new NotificadorConsola());
        System.out.println("Esta linea se imprime YA, mientras el marcador sigue actualizandose en paralelo.");
        simulacion.join(); // esperamos aqui solo para que el programa no termine antes

        GestorNotificaciones gestorNotif = new GestorNotificaciones(new NotificadorConsola());
        gestorNotif.notificarResultadoATodos(
            List.of(halconesDemo, tigresDemo),
            "El torneo ha finalizado, gracias por participar."
        );
    }

    private static Equipo crearEquipoDemo(String nombre) {
        Equipo equipo = new Equipo(nombre);
        for (int i = 1; i <= Equipo.MAX_JUGADORES; i++) {
            equipo.agregarJugador(new Jugador("Jugador" + i, nombre.replace(" ", "") + i, "Flex"));
        }
        return equipo;
    }
}