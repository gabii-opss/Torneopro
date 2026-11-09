package com.torneopro.Services;


public class NotificadorEmail implements Notificador {
    @Override
    public void notificar(String destinatario, String mensaje) {
        System.out.println("[Email simulado] Enviando a " + destinatario + ": " + mensaje);
    }
}