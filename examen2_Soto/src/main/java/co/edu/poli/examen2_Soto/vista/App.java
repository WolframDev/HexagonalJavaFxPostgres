package co.edu.poli.examen2_Soto.vista;

import co.edu.poli.examen2_Soto.aplicacion.puerto.entrada.BuscarTarjetaUseCase;
import co.edu.poli.examen2_Soto.aplicacion.puerto.entrada.CrearTarjetaUseCase;
import co.edu.poli.examen2_Soto.aplicacion.puerto.entrada.ListarTitularesUseCase;
import co.edu.poli.examen2_Soto.aplicacion.servicio.BuscarTarjetaServicio;
import co.edu.poli.examen2_Soto.aplicacion.servicio.CrearTarjetaServicio;
import co.edu.poli.examen2_Soto.aplicacion.servicio.ListarTitularesServicio;
import co.edu.poli.examen2_Soto.infraestructura.persistencia.PostgresTarjetaRepository;
import co.edu.poli.examen2_Soto.infraestructura.persistencia.PostgresTitularRepository;
import co.edu.poli.examen2_Soto.infraestructura.ui.ControlFormCard;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

/**
 * Punto de entrada de la aplicación JavaFX.
 *
 * Responsabilidad adicional en la arquitectura hexagonal:
 * es el único lugar del sistema que conoce todas las capas y
 * realiza la composición (wiring) manual de dependencias:
 *
 *   Adaptadores de salida  →  Servicios de aplicación  →  Adaptador de entrada (UI)
 *   MySqlTarjetaRepository → BuscarTarjetaServicio     → ControlFormCard
 *   MySqlTitularRepository → CrearTarjetaServicio
 *                          → ListarTitularesServicio
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        // 1. Crear adaptadores de salida (infraestructura de persistencia)
        PostgresTarjetaRepository tarjetaRepo = new PostgresTarjetaRepository();
        PostgresTitularRepository titularRepo = new PostgresTitularRepository();

        // 2. Crear servicios de aplicación inyectando los repositorios
        BuscarTarjetaUseCase   buscarUC  = new BuscarTarjetaServicio(tarjetaRepo);
        CrearTarjetaUseCase    crearUC   = new CrearTarjetaServicio(tarjetaRepo);
        ListarTitularesUseCase listarUC  = new ListarTitularesServicio(titularRepo);

        // 3. Cargar la vista FXML y obtener el controlador
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/co/edu/poli/examen2_Soto/formCard.fxml"));
        TabPane root = loader.load();

        // 4. Inyectar use cases en el adaptador de entrada (UI)
        ControlFormCard controlador = loader.getController();
        controlador.setUseCases(buscarUC, crearUC, listarUC);
        controlador.cargarTitulares();

        // 5. Mostrar la ventana
        stage.setTitle("Tarjetas");
        stage.setResizable(false);
        stage.setScene(new Scene(root));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
