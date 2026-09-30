# Gestión de Tarjetas Bancarias — Arquitectura Hexagonal + JavaFX

Aplicación de escritorio desarrollada en **Java 11 + JavaFX** que gestiona tarjetas bancarias (débito y crédito) con sus titulares. Implementa **Arquitectura Hexagonal (Ports & Adapters)** y soporta dos motores de base de datos: **MySQL** y **PostgreSQL**.

---

## Tabla de contenidos

- [Arquitectura](#arquitectura)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Tecnologías](#tecnologías)
- [Requisitos previos](#requisitos-previos)
- [Configuración de la base de datos](#configuración-de-la-base-de-datos)
- [Configuración del entorno (.env)](#configuración-del-entorno-env)
- [Ejecutar la aplicación](#ejecutar-la-aplicación)
- [Funcionalidades](#funcionalidades)
- [Diagrama de capas](#diagrama-de-capas)
- [Adaptadores disponibles](#adaptadores-disponibles)
- [Patrón Adapter GoF vs Hexagonal](#patrón-adapter-gof-vs-hexagonal)

---

## Arquitectura

El proyecto sigue los principios de la **Arquitectura Hexagonal** (también conocida como *Ports & Adapters*), propuesta por Alistair Cockburn:

- El **dominio** no depende de ninguna tecnología externa.
- La **capa de aplicación** define puertos (interfaces) de entrada y salida.
- Los **adaptadores** conectan el sistema con tecnologías concretas (JavaFX, MySQL, PostgreSQL).
- El **wiring** (composición de dependencias) ocurre únicamente en `App.java`.

```
┌─────────────────────────────────────────────────────────────────────┐
│                          HEXÁGONO                                   │
│                                                                     │
│  ┌──────────────┐    ┌─────────────────────┐    ┌───────────────┐  │
│  │  ADAPTADOR   │    │    APLICACIÓN        │    │  ADAPTADOR   │  │
│  │  DE ENTRADA  │───▶│  (Casos de Uso)      │───▶│  DE SALIDA   │  │
│  │              │    │                      │    │              │  │
│  │ControlForm   │    │ BuscarTarjetaServicio │    │MySqlTarjeta  │  │
│  │Card.java     │    │ CrearTarjetaServicio  │    │Repository    │  │
│  │(JavaFX UI)   │    │ ListarTitulares       │    │              │  │
│  └──────────────┘    │      Servicio         │    │PostgresTarje │  │
│                      └─────────────────────┘    │taRepository  │  │
│                                                   └───────────────┘  │
│                          DOMINIO                                    │
│                  Tarjeta · Debito · Credito · Titular               │
└─────────────────────────────────────────────────────────────────────┘
```

---

## Estructura del proyecto

```
examen2_Soto/
├── src/main/java/co/edu/poli/examen2_Soto/
│   │
│   ├── dominio/
│   │   └── modelo/
│   │       ├── Tarjeta.java          ← Entidad abstracta (lógica: bloquear/activar)
│   │       ├── Debito.java           ← Subentidad (campo: saldo)
│   │       ├── Credito.java          ← Subentidad (campo: limite)
│   │       └── Titular.java          ← Entidad
│   │
│   ├── aplicacion/
│   │   ├── puerto/
│   │   │   ├── entrada/              ← PUERTOS DE ENTRADA (input ports)
│   │   │   │   ├── BuscarTarjetaUseCase.java
│   │   │   │   ├── CrearTarjetaUseCase.java
│   │   │   │   └── ListarTitularesUseCase.java
│   │   │   └── salida/               ← PUERTOS DE SALIDA (output ports)
│   │   │       ├── TarjetaRepository.java
│   │   │       └── TitularRepository.java
│   │   └── servicio/                 ← CASOS DE USO (lógica de aplicación)
│   │       ├── BuscarTarjetaServicio.java
│   │       ├── CrearTarjetaServicio.java
│   │       └── ListarTitularesServicio.java
│   │
│   ├── infraestructura/
│   │   ├── persistencia/             ← ADAPTADORES DE SALIDA
│   │   │   ├── ConexionBD.java             (Singleton JDBC MySQL)
│   │   │   ├── ConexionPostgresBD.java     (Singleton JDBC PostgreSQL)
│   │   │   ├── MySqlTarjetaRepository.java
│   │   │   ├── MySqlTitularRepository.java
│   │   │   ├── PostgresTarjetaRepository.java
│   │   │   └── PostgresTitularRepository.java
│   │   └── ui/                       ← ADAPTADOR DE ENTRADA
│   │       └── ControlFormCard.java        (Controlador JavaFX / FXML)
│   │
│   └── vista/
│       └── App.java                  ← Entry point + wiring de dependencias
│
├── src/main/resources/
│   └── co/edu/poli/examen2_Soto/
│       └── formCard.fxml             ← Definición de la UI
│
├── src/main/java/module-info.java
├── pom.xml
├── .env                              ← Credenciales DB (no se sube a git)
├── ScriptDB.sql                      ← Script MySQL
└── ScriptDB_postgres.sql             ← Script PostgreSQL
```

---

## Tecnologías

| Tecnología | Versión | Uso |
|---|---|---|
| Java | 11 | Lenguaje principal |
| JavaFX | 13 | Interfaz gráfica de escritorio |
| Maven | 3.9+ | Gestión de dependencias y build |
| MySQL | 8.x | Motor de base de datos (opción A) |
| PostgreSQL | 15.x | Motor de base de datos (opción B) |
| mysql-connector-j | 8.4.0 | Driver JDBC MySQL |
| postgresql | 42.7.3 | Driver JDBC PostgreSQL |
| dotenv-java | 3.0.0 | Carga de variables de entorno desde `.env` |

---

## Requisitos previos

- **JDK 11** o superior
- **Maven 3.6+**
- **MySQL 8** o **PostgreSQL 15** (según el adaptador que uses)
- Git

---

## Configuración de la base de datos

### MySQL

```bash
mysql -u root -p < ScriptDB.sql
```

### PostgreSQL

```bash
# 1. Crear la base de datos
psql -U postgres -c "CREATE DATABASE examen2_soto ENCODING 'UTF8';"

# 2. Ejecutar el script
psql -U postgres -d examen2_soto -f ScriptDB_postgres.sql
```

Ambos scripts crean las mismas 4 tablas con estrategia **JOINED TABLE**:

```
titular
  └── tarjeta  (campos comunes de la jerarquía)
        ├── tarjeta_debito   (campo propio: saldo)
        └── tarjeta_credito  (campo propio: limite)
```

---

## Configuración del entorno (.env)

Crea el archivo `.env` en la raíz del módulo `examen2_Soto/`:

```env
# MySQL
DB_URL=jdbc:mysql://localhost:3306/examen2_soto?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
DB_USER=root
DB_PASSWORD=tu_password

# PostgreSQL
PG_URL=jdbc:postgresql://localhost:5432/examen2_soto
PG_USER=postgres
PG_PASSWORD=tu_password
```

> ⚠️ El archivo `.env` está en `.gitignore` y **nunca se sube al repositorio**.

---

## Ejecutar la aplicación

```bash
cd examen2_Soto
mvn clean javafx:run
```

### Cambiar entre MySQL y PostgreSQL

En `App.java` cambia las dos líneas del wiring:

```java
// MySQL (default)
MySqlTarjetaRepository tarjetaRepo = new MySqlTarjetaRepository();
MySqlTitularRepository titularRepo = new MySqlTitularRepository();

// PostgreSQL
PostgresTarjetaRepository tarjetaRepo = new PostgresTarjetaRepository();
PostgresTitularRepository titularRepo = new PostgresTitularRepository();
```

Solo cambian esas 2 líneas — el resto del sistema no se toca.

---

## Funcionalidades

### Pestaña Consultar
- Buscar una tarjeta por número.
- Muestra tipo (Débito/Crédito), fecha de expedición, estado y titular.
- Validación: solo acepta dígitos en el campo de número.

### Pestaña Crear
- Registrar una nueva tarjeta débito o crédito.
- Selección de titular desde ComboBox cargado desde la BD.
- Selección de fecha con DatePicker.
- Valores por defecto: saldo `0.0` para débito, límite `1.000.000` para crédito.
- Limpia el formulario automáticamente tras una creación exitosa.

---

## Diagrama de capas

```
Usuario
   │  evento (clic botón)
   ▼
ControlFormCard          ← Adaptador de ENTRADA  (infraestructura/ui)
   │  llama interfaz
   ▼
BuscarTarjetaUseCase     ← Puerto de ENTRADA      (aplicacion/puerto/entrada)
   │  implementado por
   ▼
BuscarTarjetaServicio    ← Servicio de aplicación (aplicacion/servicio)
   │  llama interfaz
   ▼
TarjetaRepository        ← Puerto de SALIDA       (aplicacion/puerto/salida)
   │  implementado por
   ▼
MySqlTarjetaRepository   ← Adaptador de SALIDA    (infraestructura/persistencia)
   │  SQL JDBC
   ▼
Base de datos MySQL / PostgreSQL
```

---

## Adaptadores disponibles

| Adaptador | Tipo | Puerto que implementa | Tecnología |
|---|---|---|---|
| `ControlFormCard` | Entrada | `BuscarTarjetaUseCase` `CrearTarjetaUseCase` `ListarTitularesUseCase` | JavaFX |
| `MySqlTarjetaRepository` | Salida | `TarjetaRepository` | MySQL / JDBC |
| `MySqlTitularRepository` | Salida | `TitularRepository` | MySQL / JDBC |
| `PostgresTarjetaRepository` | Salida | `TarjetaRepository` | PostgreSQL / JDBC |
| `PostgresTitularRepository` | Salida | `TitularRepository` | PostgreSQL / JDBC |

---

## Patrón Adapter GoF vs Hexagonal

El concepto de **Adapter** en arquitectura hexagonal es el mismo patrón GoF aplicado a nivel arquitectónico:

| GoF | Hexagonal | Ejemplo en este proyecto |
|---|---|---|
| `Target` | Puerto (interfaz) | `TarjetaRepository` |
| `Adaptee` | Sistema externo | MySQL, PostgreSQL |
| `Adapter` | Adaptador de salida | `MySqlTarjetaRepository` |
| `Client` | Servicio de aplicación | `BuscarTarjetaServicio` |

La diferencia clave: en hexagonal el objetivo no es solo compatibilidad — es **proteger el dominio** de tecnologías externas. El dominio nunca importa `java.sql`, nunca conoce `ResultSet`, nunca sabe que existe una base de datos.

---

## Autor

**Soto** — Proyecto académico Politécnico  
Arquitectura Hexagonal · JavaFX · MySQL · PostgreSQL
