package com.torneopro.Persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/** Maneja la conexion a la base de datos SQLite y la creacion de tablas. */
public class ConexionBD {

    private static final String URL = "jdbc:sqlite:torneopro.db";

    public static Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    /** Crea las tablas si no existen. Se llama una vez al iniciar el programa. */
    public static void inicializarEsquema() {
        String sqlEquipos = """
            CREATE TABLE IF NOT EXISTS equipos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL UNIQUE
            )
            """;

        String sqlJugadores = """
            CREATE TABLE IF NOT EXISTS jugadores (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                gamertag TEXT NOT NULL UNIQUE,
                rol TEXT NOT NULL,
                equipo_id INTEGER NOT NULL,
                FOREIGN KEY (equipo_id) REFERENCES equipos(id)
            )
            """;

        try (Connection con = obtenerConexion();
             Statement stmt = con.createStatement()) {
            stmt.execute(sqlEquipos);
            stmt.execute(sqlJugadores);
            System.out.println("Base de datos inicializada correctamente.");
        } catch (SQLException e) {
            throw new RuntimeException("Error al inicializar la base de datos", e);
        }
    }
}