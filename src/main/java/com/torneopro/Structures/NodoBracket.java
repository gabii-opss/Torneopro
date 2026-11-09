package com.torneopro.Structures;


import com.torneopro.models.Equipo;
import com.torneopro.models.Partido;

public class NodoBracket {

    private Equipo equipo;          // solo relevante mientras el nodo no tiene partido jugado
    private Partido partido;        // se crea cuando el nodo combina dos equipos
    private NodoBracket izquierdo;
    private NodoBracket derecho;
    private NodoBracket padre;

    /** Constructor para un nodo hoja: un equipo que entra directo al bracket. */
    public NodoBracket(Equipo equipo) {
        this.equipo = equipo;
    }

    /** Constructor para un nodo interno: combina dos nodos hijos en un nuevo partido. */
    public NodoBracket(NodoBracket izquierdo, NodoBracket derecho) {
        this.izquierdo = izquierdo;
        this.derecho = derecho;
        izquierdo.padre = this;
        if (derecho != null) {
            derecho.padre = this;
        }
        recalcular();
    }

    /** Reconstruye el partido de este nodo a partir de sus hijos. Se llama al crear
     *  el nodo y cada vez que un resultado sube desde abajo. */
    public void recalcular() {
        if (izquierdo == null) {
            return; // es una hoja, no hay nada que recalcular
        }
        Equipo equipoA = izquierdo.getEquipoResultante();
        Equipo equipoB = (derecho != null) ? derecho.getEquipoResultante() : null;

        if (equipoA != null && equipoB == null) {
            // "bye": el equipo de la izquierda pasa directo, no hay partido en este nodo
            this.equipo = equipoA;
            this.partido = null;
        } else if (equipoA != null && equipoB != null && partido == null) {
            this.partido = new Partido(equipoA, equipoB);
        }
    }

    /** El equipo que "sale" de este nodo: el ganador del partido, o el equipo de la
     *  hoja/bye si todavia no se ha jugado nada. */
    public Equipo getEquipoResultante() {
        if (partido != null) {
            return partido.getGanador(); // null si el partido aun no termina
        }
        return equipo;
    }

    public boolean esHoja() {
        return izquierdo == null && derecho == null;
    }

    public Partido getPartido() {
        return partido;
    }

    public NodoBracket getIzquierdo() {
        return izquierdo;
    }

    public NodoBracket getDerecho() {
        return derecho;
    }

    public NodoBracket getPadre() {
        return padre;
    }
}