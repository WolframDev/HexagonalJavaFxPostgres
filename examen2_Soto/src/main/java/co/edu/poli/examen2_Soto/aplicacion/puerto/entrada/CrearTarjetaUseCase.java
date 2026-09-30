package co.edu.poli.examen2_Soto.aplicacion.puerto.entrada;

import co.edu.poli.examen2_Soto.dominio.modelo.Tarjeta;

/**
 * Puerto de entrada — caso de uso: crear (registrar) una nueva tarjeta.
 * Define el contrato que el adaptador de entrada (UI) usa para
 * invocar la lógica de aplicación sin conocer su implementación.
 */
public interface CrearTarjetaUseCase {

    /**
     * Persiste una nueva tarjeta (débito o crédito).
     *
     * @param tarjeta entidad a registrar
     * @return mensaje de resultado de la operación
     * @throws Exception si ocurre un error durante la creación
     */
    String crear(Tarjeta tarjeta) throws Exception;
}
