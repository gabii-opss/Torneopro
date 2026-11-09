package com.torneopro.Persistence;

import com.torneopro.models.Equipo;
import com.torneopro.models.Jugador;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** Encargado de guardar y leer Equipos (y sus Jugadores) en la base de datos. */
public class EquipoRepositorio {

    public void guardar(Equipo equipo) {
        String sqlEquipo = "INSERT INTO equipos (nombre) VALUES (?)";
        String sqlJugador = "INSERT INTO jugadores (nombre, gamertag, rol, equipo_id) VALUES (?, ?, ?, ?)";

        try (Connection con = ConexionBD.obtenerConexion()) {
            con.setAutoCommit(false); // transaccion: o se guarda todo, o nada

            long equipoId;
            try (PreparedStatement stmtEquipo = con.prepareStatement(sqlEquipo, Statement.RETURN_GENERATED_KEYS)) {
                stmtEquipo.setString(1, equipo.getNombre());
                stmtEquipo.executeUpdate();

                try (ResultSet keys = stmtEquipo.getGeneratedKeys()) {
                    keys.next();
                    equipoId = keys.getLong(1);
                }
            }

            try (PreparedStatement stmtJugador = con.prepareStatement(sqlJugador)) {
                for (Jugador jugador : equipo.getJugadores()) {
                    stmtJugador.setString(1, jugador.getNombre());
                    stmtJugador.setString(2, jugador.getGamertag());
                    stmtJugador.setString(3, jugador.getRol());
                    stmtJugador.setLong(4, equipoId);
                    stmtJugador.addBatch();
                }
                stmtJugador.executeBatch();
            }

            con.commit();
            System.out.println("Equipo '" + equipo.getNombre() + "' guardado en la base de datos.");
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar el equipo", e);
        }
    }

    public List<Equipo> listarTodos() {
        String sqlEquipos = "SELECT id, nombre FROM equipos";
        String sqlJugadores = "SELECT nombre, gamertag, rol FROM jugadores WHERE equipo_id = ?";

        List<Equipo> resultado = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             Statement stmtEquipos = con.createStatement();
             ResultSet rsEquipos = stmtEquipos.executeQuery(sqlEquipos)) {

            while (rsEquipos.next()) {
                long id = rsEquipos.getLong("id");
                Equipo equipo = new Equipo(rsEquipos.getString("nombre"));

                try (PreparedStatement stmtJugadores = con.prepareStatement(sqlJugadores)) {
                    stmtJugadores.setLong(1, id);
                    try (ResultSet rsJugadores = stmtJugadores.executeQuery()) {
                        while (rsJugadores.next()) {
                            equipo.agregarJugador(new Jugador(
                                rsJugadores.getString("nombre"),
                                rsJugadores.getString("gamertag"),
                                rsJugadores.getString("rol")
                            ));
                        }
                    }
                }
                resultado.add(equipo);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar equipos", e);
        }

        return resultado;
    }
}