# TorneoPro

Plataforma para gestionar torneos de esports: inscripción de equipos de 5 jugadores, bracket de eliminación directa, registro de resultados e historial de partidos.

## Características

- Inscripción de equipos con exactamente 5 jugadores (gamertag y rol).
- Generación de bracket de eliminación directa (árbol binario).
- Registro del ganador de cada partido, que avanza a la siguiente ronda.
- Historial de partidos en orden cronológico (lista enlazada).
- Persistencia de equipos y jugadores en SQLite con JDBC.
- Interfaz web conectada al backend, además del menú de consola original.

## Tecnologías

- Java 17 y Maven
- Javalin (servidor web) y Jackson (JSON)
- SQLite con JDBC
- JUnit 5 para las pruebas
- HTML, CSS y JavaScript para el frontend

## Estructura

```
src/
├── main/
│   ├── java/com/torneopro/
│   │   ├── Controllers/   TorneoController (consola) y TorneoWebController (web)
│   │   ├── Exceptions/    Excepciones propias del torneo
│   │   ├── Persistence/   Conexión y repositorio de SQLite
│   │   ├── Services/      Gestor del torneo y notificaciones
│   │   ├── Structures/    Bracket, historial y nodos
│   │   ├── Views/         Vista de consola
│   │   ├── models/        Equipo, Jugador, Partido y Torneo
│   │   └── Main.java
│   └── resources/static/  Frontend (index.html, css/, js/)
└── test/                  Pruebas unitarias
```

## Cómo ejecutarlo

Requisitos: Java 17 o superior y Maven.

Versión web:

```
mvn compile exec:java
```

Abre http://localhost:8080 en el navegador. La base de datos `torneopro.db` se crea sola la primera vez.




Pruebas:

```
mvn test
```
```

## Uso de la interfaz

1. Inscribe al menos 2 equipos con el formulario.
2. Pulsa **Generar bracket**.
3. Haz clic sobre el equipo ganador de cada partido para registrar el resultado.
4. Consulta el historial, que puedes filtrar por ronda.

## Notas

- El bracket y el historial viven en memoria y se reinician al reiniciar el servidor. Los equipos sí se guardan en la base de datos.
- La interfaz no guarda marcadores, solo quién ganó cada partido.
```
#Integrantes 
Gabriela Puentes
Nicolas Solano
