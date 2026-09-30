package co.edu.poli.examen2_Soto.aplicacion.puerto.salida;

import co.edu.poli.examen2_Soto.dominio.modelo.Titular;

import java.util.List;

/**
 * Puerto de salida — contrato que define qué operaciones de persistencia
 * necesita la aplicación sobre Titular.
 */
public interface TitularRepository {

    /**
     * Retorna todos los titulares registrados.
     *
     * @return lista de titulares; nunca {@code null}
     * @throws Exception si ocurre un error de persistencia
     */
    List<Titular> listarTodos() throws Exception;
}
