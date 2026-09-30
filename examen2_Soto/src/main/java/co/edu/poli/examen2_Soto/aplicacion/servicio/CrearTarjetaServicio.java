package co.edu.poli.examen2_Soto.aplicacion.servicio;

import co.edu.poli.examen2_Soto.aplicacion.puerto.entrada.CrearTarjetaUseCase;
import co.edu.poli.examen2_Soto.aplicacion.puerto.salida.TarjetaRepository;
import co.edu.poli.examen2_Soto.dominio.modelo.Tarjeta;

/**
 * Servicio de aplicación — implementa el caso de uso CrearTarjetaUseCase.
 * Valida las reglas de negocio antes de delegar la persistencia al repositorio.
 */
public class CrearTarjetaServicio implements CrearTarjetaUseCase {

    private final TarjetaRepository tarjetaRepository;

    public CrearTarjetaServicio(TarjetaRepository tarjetaRepository) {
        this.tarjetaRepository = tarjetaRepository;
    }

    @Override
    public String crear(Tarjeta tarjeta) throws Exception {
        if (tarjeta == null) {
            throw new IllegalArgumentException("La tarjeta no puede ser nula.");
        }
        if (tarjeta.getNumero() == null || tarjeta.getNumero().isBlank()) {
            throw new IllegalArgumentException("El número de tarjeta no puede estar vacío.");
        }
        if (tarjeta.getTitular() == null) {
            throw new IllegalArgumentException("La tarjeta debe tener un titular asignado.");
        }
        if (tarjeta.getFechaExp() == null || tarjeta.getFechaExp().isBlank()) {
            throw new IllegalArgumentException("La fecha de expedición no puede estar vacía.");
        }
        return tarjetaRepository.guardar(tarjeta);
    }
}
