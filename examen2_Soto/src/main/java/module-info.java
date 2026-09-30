module co.edu.poli.examen2_Soto {

    // ── Dependencias de JavaFX ────────────────────────────────────────────
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    // ── Dependencias de infraestructura ──────────────────────────────────
    requires java.sql;
    requires io.github.cdimascio.dotenv.java;

    // ── Apertura al framework JavaFX ─────────────────────────────────────
    // vista: App.java necesita ser cargado por javafx.graphics
    opens co.edu.poli.examen2_Soto.vista to javafx.fxml, javafx.graphics;

    // ui: ControlFormCard necesita que javafx.fxml inyecte los campos @FXML
    opens co.edu.poli.examen2_Soto.infraestructura.ui to javafx.fxml;

    // dominio: Titular necesita ser accesible desde javafx.fxml (ComboBox)
    opens co.edu.poli.examen2_Soto.dominio.modelo to javafx.fxml;

    // ── Exportaciones ────────────────────────────────────────────────────
    exports co.edu.poli.examen2_Soto.vista;
}
