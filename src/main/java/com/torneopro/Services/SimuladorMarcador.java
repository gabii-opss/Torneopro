package com.torneopro.Services;

import com.torneopro.models.Partido;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

/** Simula la actualizacion de un marcador en vivo de forma concurrente,
 *  sin bloquear el hilo principal del programa. */
public class SimuladorMarcador {

    /** Lanza la simulacion de un partido en un hilo aparte. Devuelve de inmediato
     *  un CompletableFuture que se completa cuando el partido "termina". */
    public CompletableFuture<Void> simular(Partido partido, Notificador notificador) {
        return CompletableFuture.runAsync(() -> {
            try {
                for (int ronda = 1; ronda <= 3; ronda++) {
                    Thread.sleep(500); // simula tiempo real pasando
                    int puntosA = ThreadLocalRandom.current().nextInt(0, 10);
                    int puntosB = ThreadLocalRandom.current().nextInt(0, 10);
                    System.out.println(
                        "[EN VIVO] " + partido.getEquipoA().getNombre() + " " + puntosA +
                        " - " + puntosB + " " + partido.getEquipoB().getNombre() +
                        " (ronda " + ronda + ")"
                    );
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }
}