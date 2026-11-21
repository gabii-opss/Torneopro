# TorneoPro — Sistema de Gestión de Torneos

TorneoPro es una aplicación web desarrollada en **Java**, diseñada para administrar torneos de eliminación directa. Permite registrar equipos y jugadores, generar enfrentamientos, gestionar resultados y consultar el historial de los torneos.

El proyecto integra un **asistente de inteligencia artificial mediante Groq**, capaz de responder preguntas sobre equipos, jugadores, partidos y resultados utilizando la información proporcionada por la aplicación.

##  Funcionalidades

###  Gestión de equipos
- Registro de equipos participantes.
- Inscripción de exactamente cinco jugadores por equipo.
- Asignación de gamertag y rol a cada jugador.
- Visualización de los equipos inscritos.
- Validación de equipos completos.
- Persistencia de equipos y jugadores mediante SQLite y JDBC.

###  Gestión de torneos
- Generación de brackets de eliminación directa mediante árboles binarios.
- Organización de enfrentamientos por rondas.
- Registro del ganador de cada partido.
- Avance automático de los ganadores a las siguientes fases.
- Identificación del campeón del torneo.

###  Historial de partidos
- Registro de partidos disputados.
- Almacenamiento del historial mediante una lista enlazada.
- Consulta de enfrentamientos en orden cronológico.
- Visualización de equipos ganadores.
- Filtrado del historial por ronda.

###  Asistente de inteligencia artificial

TorneoPro incorpora un asistente conectado a la API de **Groq**, que permite consultar información del torneo mediante lenguaje natural.

El asistente permite:
- Consultar los equipos registrados.
- Identificar jugadores y sus respectivos roles.
- Consultar enfrentamientos del bracket.
- Obtener información sobre partidos y resultados.
- Consultar el historial del torneo.
- Identificar equipos clasificados y eliminados.
- Responder preguntas relacionadas con el desarrollo de la competición.

El asistente utiliza el modelo `openai/gpt-oss-120b` mediante la API de Groq.

Las respuestas se generan a partir de la información del torneo proporcionada por la aplicación.

##  Tecnologías utilizadas

| Tecnología | Función |
|---|---|
| Java 17 | Desarrollo de la lógica principal |
| Maven | Gestión de dependencias |
| Javalin | Servidor web y API REST |
| Jackson | Procesamiento de datos JSON |
| SQLite | Base de datos |
| JDBC | Conexión y persistencia de datos |
| HTML5 | Estructura de la interfaz web |
| CSS3 | Diseño visual |
| JavaScript | Interactividad del frontend |
| Groq API | Integración del asistente de IA |
| JUnit 5 | Pruebas unitarias |
| Git y GitHub | Control de versiones |

## Estructuras de datos implementadas

**Árbol binario:** utilizado para representar el bracket de eliminación directa, organizar los enfrentamientos y determinar el avance de los equipos.

**Lista enlazada:** utilizada para gestionar el historial de partidos disputados en orden cronológico.

## 📁 Estructura del proyecto

```text
Torneopro/
├── src/
│   ├── main/
│   │   ├── java/com/torneopro/
│   │   │   ├── Controllers/
│   │   │   ├── Exceptions/
│   │   │   ├── Persistence/
│   │   │   ├── Services/
│   │   │   ├── Structures/
│   │   │   ├── Views/
│   │   │   ├── models/
│   │   │   └── Main.java
│   │   └── resources/static/
│   │       ├── css/
│   │       ├── js/
│   │       └── index.html
│   └── test/
├── .gitignore
├── pom.xml
└── README.md
```

### Organización del código

- **Controllers:** controladores de consola y web.
- **Exceptions:** excepciones personalizadas del sistema.
- **Persistence:** conexión y repositorio de SQLite.
- **Services:** lógica del torneo, notificaciones y asistente de IA.
- **Structures:** implementación del bracket, historial y nodos.
- **Views:** interfaz de consola.
- **models:** clases Equipo, Jugador, Partido y Torneo.
- **resources/static:** interfaz web desarrollada con HTML, CSS y JavaScript.
- **test:** pruebas unitarias.

## ⚙️ Instalación y ejecución

### Requisitos

- Java JDK 17 o superior.
- Apache Maven.
- Git.
- API Key de Groq para utilizar el asistente de inteligencia artificial.

### 1. Clonar el repositorio

```bash
git clone https://github.com/gabii-opss/Torneopro.git
cd Torneopro
```

### 2. Configurar Groq

Crear una API Key desde:

https://console.groq.com/keys

En PowerShell, configurar la variable de entorno:

```powershell
$env:GROQ_API_KEY = "TU_API_KEY"
```

La clave debe mantenerse privada y no debe incluirse directamente en el código fuente ni publicarse en GitHub.

### 3. Ejecutar la aplicación

```bash
mvn clean compile exec:java
```

### 4. Abrir la aplicación

Ingresar desde el navegador a:

http://localhost:8080

La base de datos `torneopro.db` se crea automáticamente durante la inicialización de la aplicación.


##  Uso de la interfaz

1. Ingresar a la aplicación web.
2. Inscribir al menos dos equipos con cinco jugadores cada uno.
3. Seleccionar **Generar bracket**.
4. Visualizar los enfrentamientos del torneo.
5. Hacer clic sobre el equipo ganador de cada partido.
6. Continuar hasta determinar el campeón.
7. Consultar el historial de enfrentamientos.
8. Utilizar el asistente de IA para realizar preguntas sobre el torneo.

## Ejemplos de preguntas al asistente

- ¿Cuántos equipos están inscritos?
- ¿Qué jugadores pertenecen al equipo Águilas?
- ¿Qué rol tiene determinado jugador?
- ¿Qué equipos se enfrentan en las semifinales?
- ¿Qué partidos se han disputado?
- ¿Quién ganó el último torneo?
- ¿Qué equipo está clasificado para la final?
- ¿Cuántos partidos quedan pendientes?

Las respuestas dependen de la información disponible en el sistema.

##  Seguridad

La integración con Groq utiliza una variable de entorno denominada `GROQ_API_KEY`, evitando almacenar credenciales directamente en el código fuente.

Las solicitudes a la API se realizan desde el backend Java.

##  Notas importantes

- Los equipos y jugadores se almacenan en una base de datos SQLite.
- El bracket y el historial se mantienen en memoria y se reinician cuando se reinicia el servidor.
- La interfaz registra el ganador de cada partido, sin almacenar marcadores numéricos.
- El sistema conserva una interfaz de consola además de la aplicación web.
- El asistente requiere conexión a internet y una API Key válida de Groq.

##  Objetivo del proyecto

Desarrollar una aplicación que integre programación orientada a objetos, estructuras de datos, persistencia de información, servicios web e inteligencia artificial para facilitar la administración de torneos de eliminación directa.

## Integrantes

- Gabriela Puentes
- Nicolas Solano

---

**TorneoPro — Proyecto académico de Ingeniería de Software.**