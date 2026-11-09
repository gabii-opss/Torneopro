package com.torneopro.Structures;


import com.torneopro.Exceptions.PartidoInvalidoException;
import com.torneopro.models.Equipo;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/** Arbol binario de eliminacion directa: cada nodo es un cruce entre dos equipos. */
public class Bracket {

    private final NodoBracket raiz;

    public Bracket(List<Equipo> equipos) {
        if (equipos.size() < 2) {
            throw new IllegalArgumentException("Se necesitan al menos 2 equipos para construir un bracket.");
        }

        Queue<NodoBracket> nivelActual = new LinkedList<>();
        for (Equipo equipo : equipos) {
            nivelActual.offer(new NodoBracket(equipo));
        }

        // Se combinan los nodos de a pares, subiendo nivel por nivel, hasta llegar a la raiz (la final).
        while (nivelActual.size() > 1) {
            Queue<NodoBracket> siguienteNivel = new LinkedList<>();
            while (!nivelActual.isEmpty()) {
                NodoBracket izquierdo = nivelActual.poll();
                NodoBracket derecho = nivelActual.poll(); // null si quedo un numero impar (bye)
                siguienteNivel.offer(new NodoBracket(izquierdo, derecho));
            }
            nivelActual = siguienteNivel;
        }

        this.raiz = nivelActual.poll();
    }

    public NodoBracket getRaiz() {
        return raiz;
    }

    /** Registra el ganador de un partido y propaga el avance hacia el nodo padre. */
    public void registrarGanador(NodoBracket nodo, Equipo ganador) {
        if (nodo.getPartido() == null) {
            throw new PartidoInvalidoException("Este nodo no tiene un partido activo para finalizar.");
        }
        nodo.getPartido().finalizar(ganador);
        propagarHaciaArriba(nodo.getPadre());
    }

    private void propagarHaciaArriba(NodoBracket nodo) {
        if (nodo == null) {
            return;
        }
        nodo.recalcular();
        propagarHaciaArriba(nodo.getPadre());
    }

    /** El campeon del torneo, si ya se jugo la final. Null si aun no termina. */
    public Equipo getCampeon() {
        return raiz.getEquipoResultante();
    }
}