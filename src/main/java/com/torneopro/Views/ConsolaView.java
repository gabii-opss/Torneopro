package com.torneopro.Views;


import com.torneopro.models.Equipo;
import com.torneopro.models.Partido;
import com.torneopro.Structures.NodoBracket;
import java.util.List;
import java.util.Scanner;

/** Capa de Vista: todo lo que imprime en pantalla o lee del usuario vive aqui.
 *  No contiene logica de negocio, solo presentacion. */
public class ConsolaView {

    private final Scanner scanner = new Scanner(System.in);

    public void mostrarMenu() {
        System.out.println("\n=== TorneoPro ===");
        System.out.println("1. Inscribir equipo");
        System.out.println("2. Ver bracket");
        System.out.println("3. Registrar resultado de un partido");
        System.out.println("4. Ver historial");
        System.out.println("5. Salir");
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

    public void mostrarPartido(NodoBracket nodo, String etiqueta) {
        Partido partido = nodo.getPartido();
        if (partido == null) {
            System.out.println(etiqueta + ": (aun no definido)");
            return;
        }
        System.out.println(etiqueta + ": " + partido);
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