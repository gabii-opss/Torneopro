package com.torneopro.Exceptions;

public class TorneoProException extends RuntimeException {
    public TorneoProException(String mensaje) {
        super(mensaje);
    }
}