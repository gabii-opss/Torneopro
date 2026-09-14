package com.torneopro.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Torneo {

    private final String nombre;
    private final List<Equipo> equipos;
    private boolean iniciado;

    public Torneo(String nombre) {
        this.nombre = nombre;
        this.equipos = new ArrayList<>();
        this.iniciado = false;
    }

    public String getNombre() {
        return nombre;
    }

    public List<Equipo> getEquipos() {
        return Collections.unmodifiableList(equipos);
    }

    public boolean isIniciado() {
        return iniciado;
    }

    public void inscribirEquipo(Equipo equipo) {
        if (iniciado) {
            throw new IllegalStateException("No se pueden inscribir equipos: el torneo ya inicio.");
        }
        if (!equipo.estaCompleto()) {
            throw new IllegalArgumentException(
                "El equipo '" + equipo.getNombre() + "' no esta completo, no puede inscribirse."
            );
        }
        equipos.add(equipo);
    }

    public void iniciar() {
        if (equipos.size() < 2) {
            throw new IllegalStateException("Se necesitan al menos 2 equipos para iniciar el torneo.");
        }
        this.iniciado = true;
    }

    @Override
    public String toString() {
        return "Torneo{nombre='" + nombre + "', equipos=" + equipos.size() + "}";
    }
}