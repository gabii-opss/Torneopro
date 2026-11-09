package com.torneopro.models;

import com.torneopro.Exceptions.EquipoCompletoException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Equipo {

    public static final int MAX_JUGADORES = 5;

    private final String nombre;
    private final List<Jugador> jugadores;
    private int victorias;
    private int derrotas;

    public Equipo(String nombre) {
        this.nombre = nombre;
        this.jugadores = new ArrayList<>();
        this.victorias = 0;
        this.derrotas = 0;
    }

    public String getNombre() {
        return nombre;
    }

    public List<Jugador> getJugadores() {
        return Collections.unmodifiableList(jugadores);
    }

    public boolean estaCompleto() {
        return jugadores.size() == MAX_JUGADORES;
    }

    public int getVictorias() {
        return victorias;
    }

    public int getDerrotas() {
        return derrotas;
    }

    public void agregarJugador(Jugador jugador) {
        if (estaCompleto()) {
            throw new EquipoCompletoException(
                "El equipo '" + nombre + "' ya tiene el maximo de " + MAX_JUGADORES + " jugadores."
            );
        }
        if (jugadores.contains(jugador)) {
            throw new IllegalArgumentException(
                "El jugador " + jugador.getGamertag() + " ya esta en el equipo."
            );
        }
        jugadores.add(jugador);
    }

    public void registrarResultado(boolean gano) {
        if (gano) {
            victorias++;
        } else {
            derrotas++;
        }
    }

    @Override
    public String toString() {
        return "Equipo{nombre='" + nombre + "', jugadores=" + jugadores.size() + "}";
    }
}