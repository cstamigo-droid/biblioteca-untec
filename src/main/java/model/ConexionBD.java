package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Conexión a la base de datos H2 con patrón SINGLETON: una única instancia
 * de la conexión, compartida por todos los DAO de la aplicación.
 * Al iniciarse por primera vez crea las tablas y carga datos de ejemplo.
 */
public class ConexionBD {

    /* H2 en modo archivo (uso educativo, permitido por la consigna).
       Para MySQL bastaría cambiar esta URL y las credenciales. */
    /* AUTO_SERVER permite que la BD sobreviva redespliegues de la aplicación
       sin quedar bloqueada por una conexión anterior. */
    private static final String URL = "jdbc:h2:file:C:/dev/biblioteca_untec;AUTO_SERVER=TRUE";
    private static final String USUARIO = "sa";
    private static final String CLAVE = "";

    private static Connection conexion;

    private ConexionBD() { /* nadie instancia esta clase: acceso solo estático */ }

    public static synchronized Connection getConexion() throws SQLException {
        if (conexion == null || conexion.isClosed()) {
            try {
                // Cargar el driver explícitamente (necesario dentro de Tomcat)
                Class.forName("org.h2.Driver");
            } catch (ClassNotFoundException e) {
                throw new SQLException("Driver H2 no encontrado en WEB-INF/lib", e);
            }
            conexion = DriverManager.getConnection(URL, USUARIO, CLAVE);
            inicializar();
        }
        return conexion;
    }

    /** Crea las tablas si no existen y siembra datos de ejemplo la primera vez. */
    private static void inicializar() throws SQLException {
        try (Statement st = conexion.createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS USUARIOS ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "nombre VARCHAR(100) NOT NULL, "
                    + "email VARCHAR(120) NOT NULL UNIQUE, "
                    + "clave VARCHAR(60) NOT NULL)");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS LIBROS ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "titulo VARCHAR(200) NOT NULL, "
                    + "autor VARCHAR(120) NOT NULL, "
                    + "disponible BOOLEAN NOT NULL DEFAULT TRUE)");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS PRESTAMOS ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "libro_id INT NOT NULL, "
                    + "usuario_id INT NOT NULL, "
                    + "fecha_prestamo DATE NOT NULL, "
                    + "devuelto BOOLEAN NOT NULL DEFAULT FALSE, "
                    + "FOREIGN KEY (libro_id) REFERENCES LIBROS(id), "
                    + "FOREIGN KEY (usuario_id) REFERENCES USUARIOS(id))");

            try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM USUARIOS")) {
                rs.next();
                if (rs.getInt(1) == 0) {
                    // Datos de ejemplo. Credenciales de DEMO, documentadas a proposito
                    // en el README: esta base es un archivo H2 local, no un entorno real.
                    st.executeUpdate("INSERT INTO USUARIOS (nombre, email, clave) VALUES "
                            + "('Bibliotecaria UNTEC', 'biblioteca@untec.cl', 'demo1234'), "
                            + "('Administrador', 'admin@untec.cl', 'demo1234')");
                    st.executeUpdate("INSERT INTO LIBROS (titulo, autor) VALUES "
                            + "('Cien años de soledad', 'Gabriel García Márquez'), "
                            + "('El principito', 'Antoine de Saint-Exupéry'), "
                            + "('Papelucho', 'Marcela Paz'), "
                            + "('La casa de los espíritus', 'Isabel Allende')");
                }
            }
        }
    }
}
