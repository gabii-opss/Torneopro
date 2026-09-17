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

/** Capa de Controlador: recibe las acciones del usuario (desde la View),
 *  las traduce en llamadas al Model, y le pide a la View que muestre el resultado.
 *  No contiene logica de negocio propia, ni imprime directamente. */
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
            case 2 -> verBracket();
            case 3 -> view.mostrarMensaje("Usa el metodo del controlador para registrar resultado (ver ejemplo en Main).");
            case 4 -> view.mostrarHistorial(historial.comoLista());
            case 5 -> { return false; }
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

    public void generarBracket() {
        bracket = new Bracket(torneo.getEquipos().stream().toList());
        view.mostrarMensaje("Bracket generado con " + torneo.getEquipos().size() + " equipos.");
    }

    public void registrarResultado(NodoBracket nodo, Equipo ganador) {
        bracket.registrarGanador(nodo, ganador);
        if (nodo.getPartido() != null) {
            historial.agregar(nodo.getPartido());
        }
        view.mostrarMensaje("Resultado registrado.");
    }

    private void verBracket() {
        if (bracket == null) {
            view.mostrarMensaje("Aun no se ha generado el bracket.");
            return;
        }
        view.mostrarCampeon(bracket.getCampeon());
    }

    public Bracket getBracket() {
        return bracket;
    }
}