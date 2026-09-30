package co.edu.poli.examen2_Soto.infraestructura.persistencia;

import co.edu.poli.examen2_Soto.aplicacion.puerto.salida.TarjetaRepository;
import co.edu.poli.examen2_Soto.dominio.modelo.Credito;
import co.edu.poli.examen2_Soto.dominio.modelo.Debito;
import co.edu.poli.examen2_Soto.dominio.modelo.Tarjeta;
import co.edu.poli.examen2_Soto.dominio.modelo.Titular;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Adaptador de salida — implementación PostgreSQL del puerto TarjetaRepository.
 *
 * Usa la misma estrategia JOINED TABLE que MySQL:
 *   titular          → datos del titular
 *   tarjeta          → campos comunes de la jerarquía
 *   tarjeta_debito   → campo propio: saldo
 *   tarjeta_credito  → campo propio: limite
 *
 * Compatible con el script ScriptDB_postgres.sql incluido en el proyecto.
 */
public class PostgresTarjetaRepository implements TarjetaRepository {

    // ── SQL ────────────────────────────────────────────────────────────────

    private static final String SQL_INSERT_TARJETA =
            "INSERT INTO tarjeta (numero, fecha_exp, estado, titular_id) " +
            "VALUES (?, ?, ?, ?)";

    private static final String SQL_INSERT_DEBITO =
            "INSERT INTO tarjeta_debito (numero, saldo) VALUES (?, ?)";

    private static final String SQL_INSERT_CREDITO =
            "INSERT INTO tarjeta_credito (numero, limite) VALUES (?, ?)";

    private static final String SQL_SELECT_DEBITO =
            "SELECT t.numero, t.fecha_exp, t.estado, " +
            "       ti.id AS titular_id, ti.nombre AS titular_nombre, d.saldo " +
            "FROM   tarjeta_debito d " +
            "JOIN   tarjeta  t  ON d.numero     = t.numero " +
            "JOIN   titular  ti ON t.titular_id = ti.id " +
            "WHERE  d.numero = ?";

    private static final String SQL_SELECT_CREDITO =
            "SELECT t.numero, t.fecha_exp, t.estado, " +
            "       ti.id AS titular_id, ti.nombre AS titular_nombre, c.limite " +
            "FROM   tarjeta_credito c " +
            "JOIN   tarjeta  t  ON c.numero     = t.numero " +
            "JOIN   titular  ti ON t.titular_id = ti.id " +
            "WHERE  c.numero = ?";

    // ── guardar ────────────────────────────────────────────────────────────

    @Override
    public String guardar(Tarjeta tarjeta) throws Exception {
        Connection con = ConexionPostgresBD.getInstancia().getConexion();
        con.setAutoCommit(false);

        try {
            // 1. Insertar en la tabla base
            PreparedStatement ps = con.prepareStatement(SQL_INSERT_TARJETA);
            ps.setString(1, tarjeta.getNumero());
            ps.setString(2, tarjeta.getFechaExp());
            ps.setBoolean(3, tarjeta.isEstado());
            ps.setString(4, tarjeta.getTitular().getId());
            ps.executeUpdate();

            // 2. Insertar en la tabla de la subclase
            String sqlSubclase = (tarjeta instanceof Debito)
                    ? SQL_INSERT_DEBITO
                    : SQL_INSERT_CREDITO;

            ps = con.prepareStatement(sqlSubclase);
            ps.setString(1, tarjeta.getNumero());
            if (tarjeta instanceof Debito) {
                ps.setDouble(2, ((Debito) tarjeta).getSaldo());
            } else {
                ps.setDouble(2, ((Credito) tarjeta).getLimite());
            }
            ps.executeUpdate();

            con.commit();
            return "✔ " + tarjeta.getClass().getSimpleName()
                    + " [" + tarjeta.getNumero() + "] guardada correctamente en PostgreSQL.";

        } catch (Exception e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
        }
    }

    // ── buscarPorNumero ────────────────────────────────────────────────────

    @Override
    public Tarjeta buscarPorNumero(String numero) throws Exception {
        Connection con = ConexionPostgresBD.getInstancia().getConexion();

        // Intentar como Débito
        PreparedStatement ps = con.prepareStatement(SQL_SELECT_DEBITO);
        ps.setString(1, numero);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return new Debito(
                    rs.getString("numero"),
                    rs.getString("fecha_exp"),
                    rs.getBoolean("estado"),
                    new Titular(rs.getString("titular_id"), rs.getString("titular_nombre")),
                    rs.getDouble("saldo")
            );
        }

        // Intentar como Crédito
        ps = con.prepareStatement(SQL_SELECT_CREDITO);
        ps.setString(1, numero);
        rs = ps.executeQuery();
        if (rs.next()) {
            return new Credito(
                    rs.getString("numero"),
                    rs.getString("fecha_exp"),
                    rs.getBoolean("estado"),
                    new Titular(rs.getString("titular_id"), rs.getString("titular_nombre")),
                    rs.getDouble("limite")
            );
        }

        return null;
    }
}
