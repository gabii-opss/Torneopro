# TorneoPro — Sistema de Gestión de Torneos

TorneoPro es una aplicación web desarrollada en **Java**, diseñada para administrar torneos de eliminación directa. Permite registrar equipos y jugadores, generar enfrentamientos, gestionar resultados y consultar el historial de los torneos.

El proyecto integra un **asistente de inteligencia artificial mediante Groq**, capaz de responder preguntas sobre equipos, jugadores, partidos y resultados utilizando la información proporcionada por la aplicación.

## Funcionalidades

### 👥 Gestión de equipos
- Registro de equipos participantes.
- Inscripción de cinco jugadores por equipo.
- Asignación de roles a los jugadores.
- Visualización de los equipos inscritos.
- Validación de equipos completos.

### Gestión de torneos
- Generación de brackets de eliminación directa.
- Organización de enfrentamientos por rondas.
- Registro de equipos ganadores.
- Avance de equipos a las siguientes fases.
- Identificación del campeón.

### Historial de partidos
- Consulta de enfrentamientos disputados.
- Visualización de equipos ganadores.
- Seguimiento de resultados anteriores.

###  Asistente de inteligencia artificial
TorneoPro incorpora un asistente conectado a la API de Groq.

El asistente permite:
- Consultar equipos registrados.
- Identificar jugadores y sus roles.
- Consultar enfrentamientos del bracket.
- Obtener información sobre partidos y resultados.
- Consultar el historial del torneo.
- Responder preguntas sobre el desarrollo de la competición.

El asistente utiliza el modelo `openai/gpt-oss-120b` mediante la API de Groq.

## Tecnologías utilizadas

| Tecnología | Función |
|---|---|
| Java | Lógica principal de la aplicación |
| Javalin | Servidor web y API REST |
| HTML5 | Estructura de la interfaz |
| CSS3 | Diseño visual |
| JavaScript | Interactividad y comunicación con el servidor |
| Maven | Gestión de dependencias y ejecución |
| Groq API | Integración de inteligencia artificial |
| Git y GitHub | Control de versiones |

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

## Instalación y ejecución

### Requisitos
- Java JDK 17 o superior.
- Apache Maven.
- Git.
- Una API Key de Groq para utilizar el asistente de IA.

### 1. Clonar el repositorio

```bash
git clone https://github.com/gabii-opss/Torneopro.git
cd Torneopro
```

### 2. Configurar Groq

Crear una API Key desde:

https://console.groq.com/keys

En PowerShell:

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

## 💬 Ejemplos de preguntas al asistente

- ¿Cuántos equipos están inscritos?
- ¿Qué jugadores pertenecen a un equipo?
- ¿Qué rol tiene determinado jugador?
- ¿Qué equipos se enfrentan en el bracket?
- ¿Qué partidos se han disputado?
- ¿Quién ganó el último torneo?
- ¿Qué equipo está clasificado para la final?

Las respuestas dependen de la información disponible en el sistema.

## Seguridad

La integración con Groq utiliza una variable de entorno denominada `GROQ_API_KEY`, evitando almacenar la clave directamente en el repositorio.

Las solicitudes a la API se realizan desde el backend Java.

## Objetivo del proyecto

Desarrollar una aplicación que integre programación orientada a objetos, estructuras de datos, servicios web, persistencia de información e inteligencia artificial para facilitar la administración de torneos.

## Integrantes
Gabriela Puentes
Nicolas Solano
