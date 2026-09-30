package co.edu.poli.examen2_Soto.aplicacion.servicio;

import co.edu.poli.examen2_Soto.aplicacion.puerto.entrada.BuscarTarjetaUseCase;
import co.edu.poli.examen2_Soto.aplicacion.puerto.salida.TarjetaRepository;
import co.edu.poli.examen2_Soto.dominio.modelo.Tarjeta;

/**
 * Servicio de aplicación — implementa el caso de uso BuscarTarjetaUseCase.
 * Depende únicamente del puerto de salida TarjetaRepository (interfaz),
 * nunca de la implementación concreta de persistencia.
 */
public class BuscarTarjetaServicio implements BuscarTarjetaUseCase {

    private final TarjetaRepository tarjetaRepository;

    public BuscarTarjetaServicio(TarjetaRepository tarjetaRepository) {
        this.tarjetaRepository = tarjetaRepository;
    }

    @Override
    public Tarjeta buscar(String numero) throws Exception {
        if (numero == null || numero.isBlank()) {
            throw new IllegalArgumentException("El número de tarjeta no puede estar vacío.");
        }
        return tarjetaRepository.buscarPorNumero(numero.trim());
    }
}
