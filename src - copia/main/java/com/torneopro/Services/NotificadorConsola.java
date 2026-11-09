package com.torneopro.Services;


public class NotificadorConsola implements Notificador {
    @Override
    public void notificar(String destinatario, String mensaje) {
        System.out.println("[Consola] Para " + destinatario + ": " + mensaje);
    }
}