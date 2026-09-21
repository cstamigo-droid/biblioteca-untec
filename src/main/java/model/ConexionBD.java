package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Conexión a la base de datos con patrón SINGLETON: una única instancia
 * de la conexión, compartida por todos los DAO de la aplicación.
 * Al iniciarse por primera vez crea las tablas y carga datos de ejemplo.
 *
 * <p>La configuración es externa: no hay credenciales escritas en el código
 * que no puedan cambiarse sin recompilar. El orden de búsqueda es:</p>
 * <ol>
 *   <li>propiedad de sistema  (-DBIBLIOTECA_DB_URL=...)</li>
 *   <li>variable de entorno   (BIBLIOTECA_DB_URL)</li>
 *   <li>valor por defecto     (H2 en archivo, uso educativo)</li>
 * </ol>
 */
public class ConexionBD {

    /** H2 en modo archivo (uso educativo, permitido por la consigna).
        No se usa AUTO_SERVER: ese modo levanta un servidor TCP de H2 DENTRO de
        la aplicación, y al redesplegar el WAR ese hilo sobrevive apuntando a un
        classloader ya detenido, de modo que toda conexión posterior falla. La
        conexión se libera en su lugar con ConexionListener, al parar la webapp.

        La ruta usa "~", que H2 resuelve como la carpeta personal del usuario:
        así el proyecto arranca igual en Windows, Linux o macOS sin crear
        carpetas a mano ni depender de una ruta que sólo existe en un equipo. */
    private static final String URL_POR_DEFECTO =
            "jdbc:h2:~/biblioteca_untec";
    private static final String USUARIO_POR_DEFECTO = "sa";
    private static final String CLAVE_POR_DEFECTO = "";

    private static Connection conexion;

    private ConexionBD() { /* nadie instancia esta clase: acceso solo estático */ }

    /**
     * Lee un parámetro de configuración desde fuera del código.
     * @param nombre        clave a buscar (ej. BIBLIOTECA_DB_URL)
     * @param porDefecto    valor a usar si no está configurada
     */
    static String configuracion(String nombre, String porDefecto) {
        String valor = System.getProperty(nombre);
        if (valor == null || valor.trim().isEmpty()) {
            valor = System.getenv(nombre);
        }
        return (valor == null || valor.trim().isEmpty()) ? porDefecto : valor.trim();
    }

    /** URL JDBC efectiva (configurable con BIBLIOTECA_DB_URL). */
    public static String getUrl() {
        return configuracion("BIBLIOTECA_DB_URL", URL_POR_DEFECTO);
    }

    /**
     * Detecta la clase del driver JDBC a partir de la URL, para poder
     * cargarlo explícitamente dentro de Tomcat y dar un error entendible
     * en vez de un "No suitable driver" sin contexto.
     */
    static String driverPara(String url) {
        if (url == null) return null;
        if (url.startsWith("jdbc:h2:"))         return "org.h2.Driver";
        if (url.startsWith("jdbc:mariadb:"))    return "org.mariadb.jdbc.Driver";
        if (url.startsWith("jdbc:mysql:"))      return "com.mysql.cj.jdbc.Driver";
        if (url.startsWith("jdbc:postgresql:")) return "org.postgresql.Driver";
        return null;
    }

    public static synchronized Connection getConexion() throws SQLException {
        if (conexion == null || conexion.isClosed()) {
            String url = getUrl();
            String driver = driverPara(url);
            if (driver != null) {
                try {
                    // Cargar el driver explícitamente (necesario dentro de Tomcat:
                    // DriverManager recorre los servicios una sola vez por JVM).
                    Class.forName(driver);
                } catch (ClassNotFoundException e) {
                    throw new SQLException("Driver JDBC [" + driver + "] no disponible "
                            + "para la URL [" + url + "]. Revisar las dependencias del pom.xml.", e);
                }
            }
            conexion = DriverManager.getConnection(url,
                    configuracion("BIBLIOTECA_DB_USER", USUARIO_POR_DEFECTO),
                    configuracion("BIBLIOTECA_DB_PASSWORD", CLAVE_POR_DEFECTO));
            inicializar();
        }
        return conexion;
    }

    /** Cierra la conexión actual. La siguiente llamada volverá a abrirla. */
    public static synchronized void cerrar() throws SQLException {
        if (conexion != null && !conexion.isClosed()) {
            conexion.close();
        }
        conexion = null;
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
                    st.executeUpdate("INSERT INTO USUARIOS (nombre, email, clave) VALUES "
                            + "('Bibliotecaria UNTEC', 'biblioteca@untec.cl', 'untec2026'), "
                            + "('Administrador UNTEC', 'admin@untec.cl', 'admin123')");
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
