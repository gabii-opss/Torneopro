package com.torneopro;

import com.torneopro.Controllers.TorneoController;
import com.torneopro.models.Torneo;
import com.torneopro.Services.GestorTorneo;
import com.torneopro.Services.NotificadorConsola;
import com.torneopro.Views.ConsolaView;

public class Main {
    public static void main(String[] args) {
        Torneo torneo = new Torneo("Copa Otonio");
        GestorTorneo gestorTorneo = new GestorTorneo(torneo, new NotificadorConsola());
        ConsolaView view = new ConsolaView();
        TorneoController controller = new TorneoController(view, torneo, gestorTorneo);

        controller.iniciar();
    }
}