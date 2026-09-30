package co.edu.poli.examen2_Soto.aplicacion.puerto.entrada;

import co.edu.poli.examen2_Soto.dominio.modelo.Tarjeta;

/**
 * Puerto de entrada — caso de uso: buscar una tarjeta por número.
 * Define el contrato que el adaptador de entrada (UI) usa para
 * invocar la lógica de aplicación sin conocer su implementación.
 */
public interface BuscarTarjetaUseCase {

    /**
     * Busca y retorna una tarjeta dado su número.
     *
     * @param numero número de la tarjeta a buscar
     * @return tarjeta encontrada, o {@code null} si no existe
     * @throws Exception si ocurre un error durante la búsqueda
     */
    Tarjeta buscar(String numero) throws Exception;
}
