package co.edu.poli.examen2_Soto.aplicacion.puerto.salida;

import co.edu.poli.examen2_Soto.dominio.modelo.Tarjeta;

/**
 * Puerto de salida — contrato que define qué operaciones de persistencia
 * necesita la aplicación sobre Tarjeta.
 * La capa de aplicación depende de esta interfaz, nunca de la implementación concreta.
 */
public interface TarjetaRepository {

    /**
     * Persiste una nueva tarjeta (débito o crédito).
     *
     * @param tarjeta entidad a guardar
     * @return mensaje de resultado de la operación
     * @throws Exception si ocurre un error de persistencia
     */
    String guardar(Tarjeta tarjeta) throws Exception;

    /**
     * Busca una tarjeta por su número.
     *
     * @param numero número de tarjeta
     * @return la tarjeta encontrada, o {@code null} si no existe
     * @throws Exception si ocurre un error de persistencia
     */
    Tarjeta buscarPorNumero(String numero) throws Exception;
}
