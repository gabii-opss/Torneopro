package com.torneopro.Structures;


import com.torneopro.models.Partido;
import java.util.ArrayList;
import java.util.List;

/** Lista enlazada simple: guarda las partidas jugadas en orden cronologico.
 *  Se mantiene un puntero a la cola para que agregar() sea O(1). */
public class HistorialPartido {

    private NodoHistorial cabeza;
    private NodoHistorial cola;
    private int tamano;

    public void agregar(Partido partido) {
        NodoHistorial nuevo = new NodoHistorial(partido);
        if (cabeza == null) {
            cabeza = nuevo;
            cola = nuevo;
        } else {
            cola.setSiguiente(nuevo);
            cola = nuevo;
        }
        tamano++;
    }

    public int getTamano() {
        return tamano;
    }

    /** Recorre la lista enlazada y la convierte a una lista normal, O(n). */
    public List<Partido> comoLista() {
        List<Partido> resultado = new ArrayList<>();
        NodoHistorial actual = cabeza;
        while (actual != null) {
            resultado.add(actual.getPartido());
            actual = actual.getSiguiente();
        }
        return resultado;
    }
}