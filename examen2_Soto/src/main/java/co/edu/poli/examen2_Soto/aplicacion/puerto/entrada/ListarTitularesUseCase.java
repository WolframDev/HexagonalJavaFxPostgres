package co.edu.poli.examen2_Soto.aplicacion.puerto.entrada;

import co.edu.poli.examen2_Soto.dominio.modelo.Titular;

import java.util.List;

/**
 * Puerto de entrada — caso de uso: listar todos los titulares.
 * Define el contrato que el adaptador de entrada (UI) usa para
 * poblar el ComboBox de titulares sin conocer la implementación.
 */
public interface ListarTitularesUseCase {

    /**
     * Retorna todos los titulares registrados en el sistema.
     *
     * @return lista de titulares; nunca {@code null}
     * @throws Exception si ocurre un error durante la consulta
     */
    List<Titular> listar() throws Exception;
}
