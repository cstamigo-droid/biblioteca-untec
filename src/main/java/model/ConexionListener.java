package model;

import java.sql.SQLException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Cierra la conexión a la base de datos cuando Tomcat detiene la aplicación.
 *
 * <p>Sin esto, un <em>Reload</em> o <em>Redeploy</em> desde el Tomcat Manager
 * dejaría la conexión anterior abierta contra el archivo de H2 y el nuevo
 * despliegue no podría abrirlo. Es el ciclo de vida de la aplicación web
 * aplicado a un recurso externo.</p>
 */
@WebListener
public class ConexionListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent evento) {
        evento.getServletContext().log("Biblioteca UNTEC: aplicacion iniciada.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent evento) {
        try {
            ConexionBD.cerrar();
            evento.getServletContext().log("Biblioteca UNTEC: conexion a la BD cerrada.");
        } catch (SQLException e) {
            evento.getServletContext().log("Error cerrando la conexion a la BD", e);
        }
    }
}
