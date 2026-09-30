-- ================================================================
--  Script PostgreSQL — examen2_soto
--
--  INSTRUCCIONES DE EJECUCIÓN:
--  ─────────────────────────────────────────────────────────────
--  Opción A — psql (línea de comandos):
--    1. Crear la base de datos primero:
--       psql -U postgres -c "CREATE DATABASE examen2_soto ENCODING 'UTF8';"
--    2. Ejecutar este script sobre esa base:
--       psql -U postgres -d examen2_soto -f ScriptDB_postgres.sql
--
--  Opción B — pgAdmin / DBeaver:
--    1. Conectarse al servidor PostgreSQL.
--    2. Crear manualmente la base de datos "examen2_soto".
--    3. Abrir este archivo y ejecutarlo sobre esa base de datos.
--
--  Estrategia ORM: JOINED TABLE (una tabla por clase)
--  ┌────────────────────────────────────────────────────────────┐
--  │  Clase Java             │  Tabla PostgreSQL  │  Relación  │
--  ├────────────────────────────────────────────────────────────┤
--  │  Titular                │  titular           │  —         │
--  │  Tarjeta  (abstracta)   │  tarjeta           │  —         │
--  │  Debito  extends Tarjeta│  tarjeta_debito    │  1:1 FK    │
--  │  Credito extends Tarjeta│  tarjeta_credito   │  1:1 FK    │
--  └────────────────────────────────────────────────────────────┘
-- ================================================================

-- ================================================================
-- TABLA 1 — titular
-- Diferencias vs MySQL: ninguna, VARCHAR funciona igual.
-- ================================================================
CREATE TABLE IF NOT EXISTS titular (

    id      VARCHAR(50)  NOT NULL,
    nombre  VARCHAR(100) NOT NULL,

    PRIMARY KEY (id)

);

COMMENT ON TABLE  titular        IS '@Entity Titular';
COMMENT ON COLUMN titular.id     IS '@Id';
COMMENT ON COLUMN titular.nombre IS '@Column';

-- ================================================================
-- TABLA 2 — tarjeta
-- Diferencias vs MySQL:
--   · TINYINT(1)  →  BOOLEAN  (PostgreSQL tiene tipo booleano nativo)
--   · DEFAULT 1   →  DEFAULT TRUE
-- ================================================================
CREATE TABLE IF NOT EXISTS tarjeta (

    numero      VARCHAR(20)  NOT NULL,
    fecha_exp   VARCHAR(10)  NOT NULL,   -- formato dd/MM/yyyy  (ej: 12/2027)
    estado      BOOLEAN      NOT NULL DEFAULT TRUE,   -- TRUE=activa | FALSE=bloqueada
    titular_id  VARCHAR(50)  NOT NULL,

    PRIMARY KEY (numero),

    CONSTRAINT fk_tarjeta_titular
        FOREIGN KEY (titular_id)
        REFERENCES  titular (id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT

);

COMMENT ON TABLE  tarjeta            IS '@Entity Tarjeta — tabla base herencia JOINED';
COMMENT ON COLUMN tarjeta.numero     IS '@Id — PK compartida con subclases';
COMMENT ON COLUMN tarjeta.fecha_exp  IS '@Column — formato dd/MM/yyyy';
COMMENT ON COLUMN tarjeta.estado     IS '@Column — TRUE activa / FALSE bloqueada';
COMMENT ON COLUMN tarjeta.titular_id IS '@ManyToOne FK - titular.id';

-- ================================================================
-- TABLA 3 — tarjeta_debito
-- Diferencias vs MySQL:
--   · DOUBLE  →  DOUBLE PRECISION  (nombre estándar en PostgreSQL)
-- ================================================================
CREATE TABLE IF NOT EXISTS tarjeta_debito (

    numero  VARCHAR(20)      NOT NULL,
    saldo   DOUBLE PRECISION NOT NULL DEFAULT 0.0,

    PRIMARY KEY (numero),

    CONSTRAINT fk_debito_tarjeta
        FOREIGN KEY (numero)
        REFERENCES  tarjeta (numero)
        ON UPDATE CASCADE
        ON DELETE CASCADE

);

COMMENT ON TABLE  tarjeta_debito        IS '@Entity Debito extends Tarjeta — JOINED';
COMMENT ON COLUMN tarjeta_debito.numero IS '@PrimaryKeyJoinColumn — FK 1:1 - tarjeta.numero';
COMMENT ON COLUMN tarjeta_debito.saldo  IS '@Column propio Debito — saldo disponible';

-- ================================================================
-- TABLA 4 — tarjeta_credito
-- Diferencias vs MySQL:
--   · DOUBLE  →  DOUBLE PRECISION
-- ================================================================
CREATE TABLE IF NOT EXISTS tarjeta_credito (

    numero  VARCHAR(20)      NOT NULL,
    limite  DOUBLE PRECISION NOT NULL DEFAULT 0.0,

    PRIMARY KEY (numero),

    CONSTRAINT fk_credito_tarjeta
        FOREIGN KEY (numero)
        REFERENCES  tarjeta (numero)
        ON UPDATE CASCADE
        ON DELETE CASCADE

);

COMMENT ON TABLE  tarjeta_credito        IS '@Entity Credito extends Tarjeta — JOINED';
COMMENT ON COLUMN tarjeta_credito.numero IS '@PrimaryKeyJoinColumn — FK 1:1 - tarjeta.numero';
COMMENT ON COLUMN tarjeta_credito.limite IS '@Column propio Credito — límite de crédito';

-- ================================================================
-- Datos de prueba (respetar orden de FKs)
-- ================================================================

INSERT INTO titular (id, nombre) VALUES
    ('T001', 'Juan Soto'),
    ('T002', 'María López');

INSERT INTO tarjeta (numero, fecha_exp, estado, titular_id) VALUES
    ('4111111111111111', '12/2027', TRUE,  'T001'),
    ('5500005555555559', '06/2026', TRUE,  'T001'),
    ('4000056655665556', '09/2025', FALSE, 'T002'),
    ('5105105105105100', '03/2027', TRUE,  'T002');

INSERT INTO tarjeta_debito (numero, saldo) VALUES
    ('4111111111111111', 1500000.00),
    ('4000056655665556',  800000.00);

INSERT INTO tarjeta_credito (numero, limite) VALUES
    ('5500005555555559', 5000000.00),
    ('5105105105105100', 2000000.00);
