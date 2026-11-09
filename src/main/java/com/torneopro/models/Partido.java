package com.torneopro.models;

public class Partido {

    public enum EstadoPartido {
        PENDIENTE,
        EN_CURSO,
        FINALIZADO
    }

    private final Equipo equipoA;
    private final Equipo equipoB;
    private EstadoPartido estado;
    private Equipo ganador;

    public Partido(Equipo equipoA, Equipo equipoB) {
        this.equipoA = equipoA;
        this.equipoB = equipoB;
        this.estado = EstadoPartido.PENDIENTE;
        this.ganador = null;
    }

    public Equipo getEquipoA() {
        return equipoA;
    }

    public Equipo getEquipoB() {
        return equipoB;
    }

    public EstadoPartido getEstado() {
        return estado;
    }

    public Equipo getGanador() {
        return ganador;
    }

    public void iniciar() {
        this.estado = EstadoPartido.EN_CURSO;
    }

    public void finalizar(Equipo ganador) {
        if (!ganador.equals(equipoA) && !ganador.equals(equipoB)) {
            throw new IllegalArgumentException("El ganador debe ser uno de los dos equipos del partido.");
        }
        this.ganador = ganador;
        this.estado = EstadoPartido.FINALIZADO;
        ganador.registrarResultado(true);

        Equipo perdedor = ganador.equals(equipoB) ? equipoA : equipoB;
        if (perdedor != null) {
            perdedor.registrarResultado(false);
        }
    }

    @Override
    public String toString() {
        String nombreB = (equipoB != null) ? equipoB.getNombre() : "BYE";
        return "Partido{" + equipoA.getNombre() + " vs " + nombreB + ", estado=" + estado + "}";
    }
}