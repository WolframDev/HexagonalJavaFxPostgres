package co.edu.poli.examen2_Soto.aplicacion.servicio;

import co.edu.poli.examen2_Soto.aplicacion.puerto.entrada.ListarTitularesUseCase;
import co.edu.poli.examen2_Soto.aplicacion.puerto.salida.TitularRepository;
import co.edu.poli.examen2_Soto.dominio.modelo.Titular;

import java.util.List;

/**
 * Servicio de aplicación — implementa el caso de uso ListarTitularesUseCase.
 * Delega directamente al repositorio ya que no hay lógica adicional.
 */
public class ListarTitularesServicio implements ListarTitularesUseCase {

    private final TitularRepository titularRepository;

    public ListarTitularesServicio(TitularRepository titularRepository) {
        this.titularRepository = titularRepository;
    }

    @Override
    public List<Titular> listar() throws Exception {
        return titularRepository.listarTodos();
    }
}
