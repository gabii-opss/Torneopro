package com.torneopro.models;

import java.util.Objects;

public class Jugador {

    private final String nombre;
    private final String gamertag;
    private final String rol;
    private int partidasJugadas;

    public Jugador(String nombre, String gamertag, String rol) {
        this.nombre = nombre;
        this.gamertag = gamertag;
        this.rol = rol;
        this.partidasJugadas = 0;
    }

    public String getNombre() {
        return nombre;
    }

    public String getGamertag() {
        return gamertag;
    }

    public String getRol() {
        return rol;
    }

    public int getPartidasJugadas() {
        return partidasJugadas;
    }

    public void registrarPartidaJugada() {
        this.partidasJugadas++;
    }

    @Override
    public String toString() {
        return "Jugador{gamertag='" + gamertag + "', rol='" + rol + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Jugador)) return false;
        Jugador jugador = (Jugador) o;
        return Objects.equals(gamertag, jugador.gamertag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gamertag);
    }
}