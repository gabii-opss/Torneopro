package com.torneopro.Structures;


import com.torneopro.models.Partido;

public class NodoHistorial {

    private final Partido partido;
    private NodoHistorial siguiente;

    public NodoHistorial(Partido partido) {
        this.partido = partido;
    }

    public Partido getPartido() {
        return partido;
    }

    public NodoHistorial getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(NodoHistorial siguiente) {
        this.siguiente = siguiente;
    }
}