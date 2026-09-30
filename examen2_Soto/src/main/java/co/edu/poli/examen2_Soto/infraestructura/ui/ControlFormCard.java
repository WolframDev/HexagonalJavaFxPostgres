package co.edu.poli.examen2_Soto.infraestructura.ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import co.edu.poli.examen2_Soto.aplicacion.puerto.entrada.BuscarTarjetaUseCase;
import co.edu.poli.examen2_Soto.aplicacion.puerto.entrada.CrearTarjetaUseCase;
import co.edu.poli.examen2_Soto.aplicacion.puerto.entrada.ListarTitularesUseCase;
import co.edu.poli.examen2_Soto.dominio.modelo.Credito;
import co.edu.poli.examen2_Soto.dominio.modelo.Debito;
import co.edu.poli.examen2_Soto.dominio.modelo.Tarjeta;
import co.edu.poli.examen2_Soto.dominio.modelo.Titular;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

/**
 * Adaptador de entrada (driving adapter) — JavaFX UI.
 * Solo conoce los puertos de entrada (use cases); nunca accede
 * directamente a repositorios ni a infraestructura de persistencia.
 *
 * Las dependencias se inyectan desde App.java (composición manual).
 */
public class ControlFormCard {

    // ── Campos FXML ────────────────────────────────────────────────────────
    @FXML private Button   bttConsulta;
    @FXML private TextField txtTarjeta1;
    @FXML private TextArea  txtAreaResultado;
    @FXML private Button   bttCreacion;
    @FXML private TextField txtTarjeta2;
    @FXML private DatePicker datepk1;
    @FXML private ComboBox<Titular> cmbTitular;
    @FXML private RadioButton radio1; // Débito
    @FXML private RadioButton radio2; // Crédito
    @FXML private ToggleGroup tipo;

    // ── Puertos de entrada ─────────────────────────────────────────────────
    private BuscarTarjetaUseCase  buscarTarjetaUseCase;
    private CrearTarjetaUseCase   crearTarjetaUseCase;
    private ListarTitularesUseCase listarTitularesUseCase;

    // ── Inyección de dependencias (llamada desde App.java) ─────────────────

    public void setUseCases(BuscarTarjetaUseCase buscar,
                            CrearTarjetaUseCase  crear,
                            ListarTitularesUseCase listar) {
        this.buscarTarjetaUseCase   = buscar;
        this.crearTarjetaUseCase    = crear;
        this.listarTitularesUseCase = listar;
    }

    // ── Inicialización ─────────────────────────────────────────────────────

    @FXML
    private void initialize() {
        datepk1.setValue(LocalDate.now());

        // Listeners de validación numérica al perder el foco
        txtTarjeta1.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) validarSoloNumeros(txtTarjeta1);
        });
        txtTarjeta2.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) validarSoloNumeros(txtTarjeta2);
        });
    }

    /**
     * Carga los titulares en el ComboBox.
     * Se llama desde App.java después de inyectar los use cases,
     * porque initialize() se ejecuta antes de que estén disponibles.
     */
    public void cargarTitulares() {
        try {
            List<Titular> lista = listarTitularesUseCase.listar();
            cmbTitular.getItems().setAll(lista);
        } catch (Exception e) {
            mostrarAlerta("Error al cargar titulares: " + e.getMessage());
        }
    }

    // ── Acciones de botones ────────────────────────────────────────────────

    @FXML
    private void pressConsulta(ActionEvent event) {
        txtAreaResultado.setText("");
        String numero = txtTarjeta1.getText().trim();
        if (numero.isBlank()) {
            mostrarAlerta("Ingrese el número de tarjeta.");
            return;
        }
        try {
            Tarjeta t = buscarTarjetaUseCase.buscar(numero);
            if (t != null) {
                txtAreaResultado.setText(t.toString());
            } else {
                mostrarAlerta("No existe el número de tarjeta.");
            }
        } catch (Exception e) {
            mostrarAlerta(e.getMessage());
        }
    }

    @FXML
    private void pressCreacion(ActionEvent event) {
        String numero = txtTarjeta2.getText().trim();
        if (numero.isEmpty()) {
            mostrarAlerta("⚠ Ingrese el número de tarjeta.");
            return;
        }

        if (datepk1.getValue() == null) {
            mostrarAlerta("⚠ Seleccione la fecha de expedición.");
            return;
        }

        Titular titular = cmbTitular.getValue();
        if (titular == null) {
            mostrarAlerta("⚠ Seleccione un titular.");
            return;
        }

        String fechaExp = datepk1.getValue()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        Tarjeta nueva = radio1.isSelected()
                ? new Debito(numero, fechaExp, true, titular, 0.0)
                : new Credito(numero, fechaExp, true, titular, 1_000_000);

        try {
            String resultado = crearTarjetaUseCase.crear(nueva);
            mostrarAlerta(resultado);
            if (resultado.startsWith("✔")) {
                limpiarFormCrear();
            }
        } catch (Exception e) {
            mostrarAlerta("Error al crear tarjeta: " + e.getMessage());
        }
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Resultado");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void limpiarFormCrear() {
        txtTarjeta2.clear();
        datepk1.setValue(null);
        cmbTitular.setValue(null);
        radio1.setSelected(true);
    }

    private void validarSoloNumeros(TextField txt) {
        String texto = txt.getText().trim();
        if (!texto.isBlank() && !texto.matches("\\d+")) {
            txt.setStyle("-fx-border-color: red;");
            mostrarAlerta("Solo se permiten números.");
            txt.setText("");
            Platform.runLater(txt::requestFocus);
        } else {
            txt.setStyle("");
        }
    }
}
