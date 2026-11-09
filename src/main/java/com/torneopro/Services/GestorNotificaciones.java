package com.torneopro.Services;

import com.torneopro.models.Equipo;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/** Envia notificaciones a varios equipos en paralelo, en vez de uno por uno. */
public class GestorNotificaciones {

    private final Notificador notificador;

    public GestorNotificaciones(Notificador notificador) {
        this.notificador = notificador;
    }

    /** Notifica a todos los equipos de la lista al mismo tiempo (concurrentemente)
     *  y espera a que todas las notificaciones terminen antes de continuar. */
    public void notificarResultadoATodos(List<Equipo> equipos, String mensaje) {
        List<CompletableFuture<Void>> tareas = equipos.stream()
            .map(equipo -> CompletableFuture.runAsync(() ->
                notificador.notificar(equipo.getNombre(), mensaje)
            ))
            .toList();

        // Espera a que TODAS las notificaciones concurrentes terminen
        CompletableFuture.allOf(tareas.toArray(new CompletableFuture[0])).join();

        System.out.println("Todas las notificaciones fueron enviadas (en paralelo).");
    }
}