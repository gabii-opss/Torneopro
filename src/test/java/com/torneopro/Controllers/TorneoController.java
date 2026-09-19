package com.torneopro.Controllers;

import com.torneopro.Exceptions.TorneoProException;
import com.torneopro.models.Equipo;
import com.torneopro.models.Jugador;
import com.torneopro.models.Torneo;
import com.torneopro.Services.GestorTorneo;
import com.torneopro.Structures.Bracket;
import com.torneopro.Structures.HistorialPartido;
import com.torneopro.Structures.NodoBracket;
import com.torneopro.Views.ConsolaView;
import com.torneopro.models.Partido;
import java.util.ArrayList;
import java.util.List;

public class TorneoController {

    private final ConsolaView view;
    private final Torneo torneo;
    private final GestorTorneo gestorTorneo;
    private final HistorialPartido historial = new HistorialPartido();
    private Bracket bracket;

    public TorneoController(ConsolaView view, Torneo torneo, GestorTorneo gestorTorneo) {
        this.view = view;
        this.torneo = torneo;
        this.gestorTorneo = gestorTorneo;
    }

    public void iniciar() {
        boolean continuar = true;
        while (continuar) {
            view.mostrarMenu();
            try {
                int opcion = view.leerOpcion();
                continuar = procesarOpcion(opcion);
            } catch (TorneoProException e) {
                view.mostrarError(e.getMessage());
            } catch (Exception e) {
                view.mostrarError("Entrada invalida, intenta de nuevo.");
            }
        }
    }

    private boolean procesarOpcion(int opcion) {
        switch (opcion) {
            case 1 -> inscribirEquipoDesdeConsola();
            case 2 -> generarBracket();
            case 3 -> verBracketCompleto();
            case 4 -> registrarResultadoDesdeConsola();
            case 5 -> view.mostrarHistorial(historial.comoLista());
            case 6 -> { return false; }
            default -> view.mostrarMensaje("Opcion no valida.");
        }
        return true;
    }

    private void inscribirEquipoDesdeConsola() {
        String nombre = view.leerTexto("Nombre del equipo");
        Equipo equipo = new Equipo(nombre);
        for (int i = 1; i <= Equipo.MAX_JUGADORES; i++) {
            String gamertag = view.leerTexto("Gamertag jugador " + i);
            String rol = view.leerTexto("Rol jugador " + i);
            equipo.agregarJugador(new Jugador(gamertag, gamertag, rol));
        }
        gestorTorneo.inscribirEquipo(equipo);
        view.mostrarMensaje("Equipo inscrito correctamente.");
    }

    private void generarBracket() {
        if (torneo.getEquipos().size() < 2) {
            view.mostrarMensaje("Necesitas inscribir al menos 2 equipos antes de generar el bracket.");
            return;
        }
        bracket = new Bracket(torneo.getEquipos().stream().toList());
        view.mostrarMensaje("Bracket generado con " + torneo.getEquipos().size() + " equipos.");
    }

    private void verBracketCompleto() {
        if (bracket == null) {
            view.mostrarMensaje("Aun no se ha generado el bracket. Usa la opcion 2 primero.");
            return;
        }
        view.mostrarBracketCompleto(bracket.getRaiz());
        view.mostrarCampeon(bracket.getCampeon());
    }

    private void registrarResultadoDesdeConsola() {
        if (bracket == null) {
            view.mostrarMensaje("Aun no se ha generado el bracket. Usa la opcion 2 primero.");
            return;
        }

        List<NodoBracket> pendientes = obtenerPartidosPendientes(bracket.getRaiz(), new ArrayList<>());
        view.mostrarPartidosPendientes(pendientes);
        if (pendientes.isEmpty()) {
            return;
        }

        int indice = Integer.parseInt(view.leerTexto("Numero del partido a registrar")) - 1;
        if (indice < 0 || indice >= pendientes.size()) {
            view.mostrarMensaje("Numero fuera de rango.");
            return;
        }

        NodoBracket nodoElegido = pendientes.get(indice);
        String nombreGanador = view.leerTexto("Nombre exacto del equipo ganador");

        Equipo ganador = nodoElegido.getPartido().getEquipoA().getNombre().equals(nombreGanador)
            ? nodoElegido.getPartido().getEquipoA()
            : nodoElegido.getPartido().getEquipoB();

        if (ganador == null || !ganador.getNombre().equals(nombreGanador)) {
            view.mostrarMensaje("Ese nombre no coincide con ninguno de los dos equipos del partido.");
            return;
        }

        bracket.registrarGanador(nodoElegido, ganador);
        historial.agregar(nodoElegido.getPartido());
        view.mostrarMensaje("Resultado registrado. Ganador: " + ganador.getNombre());
    }

    /** Recorre el bracket buscando nodos con un partido activo (no finalizado). */
    private List<NodoBracket> obtenerPartidosPendientes(NodoBracket nodo, List<NodoBracket> acumulado) {
        if (nodo == null || nodo.esHoja()) {
            return acumulado;
        }
        obtenerPartidosPendientes(nodo.getIzquierdo(), acumulado);
        if (nodo.getPartido() != null
            && nodo.getPartido().getEstado() != Partido.EstadoPartido.FINALIZADO) {
            acumulado.add(nodo);
        }
        obtenerPartidosPendientes(nodo.getDerecho(), acumulado);
        return acumulado;
    }
}