package com.torneopro.Exceptions;

/** Error al hablar con el servicio de IA (sin clave, sin conexion, limite de uso, etc.). */
public class AsistenteIAException extends RuntimeException {

    public AsistenteIAException(String mensaje) {
        super(mensaje);
    }

    public AsistenteIAException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
