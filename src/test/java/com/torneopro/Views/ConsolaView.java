package com.torneopro.Views;

import com.torneopro.models.Equipo;
import com.torneopro.models.Partido;
import com.torneopro.Structures.NodoBracket;
import java.util.List;
import java.util.Scanner;

public class ConsolaView {

    private final Scanner scanner = new Scanner(System.in);

    public void mostrarMenu() {
        System.out.println("\n=== TorneoPro ===");
        System.out.println("1. Inscribir equipo");
        System.out.println("2. Generar bracket");
        System.out.println("3. Ver bracket completo");
        System.out.println("4. Registrar resultado de un partido");
        System.out.println("5. Ver historial");
        System.out.println("6. Salir");
        System.out.print("Elige una opcion: ");
    }

    public int leerOpcion() {
        return Integer.parseInt(scanner.nextLine().trim());
    }

    public String leerTexto(String etiqueta) {
        System.out.print(etiqueta + ": ");
        return scanner.nextLine().trim();
    }

    public void mostrarMensaje(String mensaje) {
        System.out.println(mensaje);
    }

    public void mostrarError(String mensaje) {
        System.out.println("[ERROR] " + mensaje);
    }

    /** Imprime el bracket completo de forma recursiva, indentando por nivel. */
    public void mostrarBracketCompleto(NodoBracket nodo) {
        mostrarNodoRecursivo(nodo, 0);
    }

    private void mostrarNodoRecursivo(NodoBracket nodo, int nivel) {
        if (nodo == null) {
            return;
        }
        String indentacion = "  ".repeat(nivel);
        if (nodo.esHoja()) {
            System.out.println(indentacion + "- " + nodo.getEquipoResultante().getNombre());
        } else {
            mostrarNodoRecursivo(nodo.getIzquierdo(), nivel + 1);
            String texto = (nodo.getPartido() != null)
                ? nodo.getPartido().toString()
                : "(pendiente de definir)";
            System.out.println(indentacion + "* " + texto);
            mostrarNodoRecursivo(nodo.getDerecho(), nivel + 1);
        }
    }

    /** Muestra una lista numerada de partidos que aun no tienen resultado. */
    public void mostrarPartidosPendientes(List<NodoBracket> pendientes) {
        if (pendientes.isEmpty()) {
            System.out.println("No hay partidos pendientes por registrar.");
            return;
        }
        System.out.println("--- Partidos pendientes ---");
        for (int i = 0; i < pendientes.size(); i++) {
            System.out.println((i + 1) + ". " + pendientes.get(i).getPartido());
        }
    }

    public void mostrarHistorial(List<Partido> partidas) {
        System.out.println("--- Historial (" + partidas.size() + " partidas) ---");
        partidas.forEach(p -> System.out.println("  " + p));
    }

    public void mostrarCampeon(Equipo campeon) {
        if (campeon != null) {
            System.out.println("CAMPEON: " + campeon.getNombre());
        } else {
            System.out.println("El torneo aun no tiene campeon.");
        }
    }
}