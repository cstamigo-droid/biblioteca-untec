package view;

/**
 * Rutas de las vistas JSP de la aplicación, en un solo lugar.
 * Las JSP viven en WEB-INF/views para que solo se llegue a ellas
 * a través de los controladores (patrón MVC).
 */
public final class Paginas {

    public static final String LOGIN = "/index.jsp";
    public static final String LIBROS = "/WEB-INF/views/libros.jsp";
    public static final String PRESTAMOS = "/WEB-INF/views/prestamos.jsp";

    private Paginas() { /* clase de constantes: no se instancia */ }
}
