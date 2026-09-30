package co.edu.poli.examen2_Soto.infraestructura.persistencia;

import java.sql.Connection;
import java.sql.DriverManager;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * Infraestructura — fábrica Singleton de conexión JDBC para PostgreSQL.
 *
 * Lee las credenciales desde el archivo .env usando las variables:
 *   PG_URL      → jdbc:postgresql://localhost:5432/examen2_soto
 *   PG_USER     → postgres
 *   PG_PASSWORD → tu_password
 */
public class ConexionPostgresBD {

    private static ConexionPostgresBD instancia;
    private Connection conexion;

    private ConexionPostgresBD() throws Exception {
        Dotenv dotenv = Dotenv.load();

        String url  = dotenv.get("PG_URL");
        String user = dotenv.get("PG_USER");
        String pass = dotenv.get("PG_PASSWORD");

        if (url == null || user == null || pass == null) {
            throw new RuntimeException(
                "Faltan variables de entorno PostgreSQL en el archivo .env " +
                "(PG_URL, PG_USER, PG_PASSWORD)");
        }

        Class.forName("org.postgresql.Driver");
        conexion = DriverManager.getConnection(url, user, pass);
    }

    public static ConexionPostgresBD getInstancia() throws Exception {
        if (instancia == null) {
            instancia = new ConexionPostgresBD();
        }
        return instancia;
    }

    public Connection getConexion() throws Exception {
        if (conexion == null || conexion.isClosed()) {
            instancia = new ConexionPostgresBD();
            return instancia.conexion;
        }
        return conexion;
    }
}
