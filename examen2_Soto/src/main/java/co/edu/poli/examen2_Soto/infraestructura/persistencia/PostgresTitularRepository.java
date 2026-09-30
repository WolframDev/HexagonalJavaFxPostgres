package co.edu.poli.examen2_Soto.infraestructura.persistencia;

import co.edu.poli.examen2_Soto.aplicacion.puerto.salida.TitularRepository;
import co.edu.poli.examen2_Soto.dominio.modelo.Titular;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Adaptador de salida — implementación PostgreSQL del puerto TitularRepository.
 */
public class PostgresTitularRepository implements TitularRepository {

    private static final String SQL_SELECT_TODOS =
            "SELECT id AS titular_id, nombre AS titular_nombre FROM titular";

    @Override
    public List<Titular> listarTodos() throws Exception {
        Connection con = ConexionPostgresBD.getInstancia().getConexion();
        List<Titular> lista = new ArrayList<>();

        PreparedStatement ps = con.prepareStatement(SQL_SELECT_TODOS);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            lista.add(new Titular(
                    rs.getString("titular_id"),
                    rs.getString("titular_nombre")
            ));
        }
        return lista;
    }
}
