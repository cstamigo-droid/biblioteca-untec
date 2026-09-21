package controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.LibroDAO;
import model.PrestamoDAO;
import model.Usuario;
import view.Paginas;

/**
 * Controlador del catálogo de libros.
 * GET: lista los libros disponibles y prestados (vista libros.jsp).
 * POST: agrega un libro, lo elimina, o lo presta al usuario en sesión.
 * Todas las operaciones exigen sesión iniciada.
 */
@WebServlet("/libros")
public class LibroServlet extends HttpServlet {

    private final LibroDAO libroDAO = new LibroDAO();
    private final PrestamoDAO prestamoDAO = new PrestamoDAO();

    /** Devuelve el usuario en sesión, o null (y redirige al login) si no hay. */
    private Usuario exigirSesion(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        Usuario u = (Usuario) req.getSession().getAttribute("usuario");
        if (u == null) {
            resp.sendRedirect(req.getContextPath() + "/index.jsp");
        }
        return u;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (exigirSesion(request, response) == null) return;
        try {
            request.setAttribute("libros", libroDAO.listar());
        } catch (java.sql.SQLException e) {
            throw new ServletException("Error listando los libros", e);
        }
        request.getRequestDispatcher(Paginas.LIBROS).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Usuario usuario = exigirSesion(request, response);
        if (usuario == null) return;

        request.setCharacterEncoding("UTF-8");
        String accion = request.getParameter("accion");

        try {
            if ("agregar".equals(accion)) {
                String titulo = request.getParameter("titulo");
                String autor = request.getParameter("autor");
                if (titulo == null || titulo.trim().isEmpty()
                        || autor == null || autor.trim().isEmpty()) {
                    response.sendRedirect(request.getContextPath() + "/libros?error=campos");
                    return;
                }
                libroDAO.agregar(titulo.trim(), autor.trim());

            } else if ("eliminar".equals(accion)) {
                boolean eliminado = libroDAO.eliminar(Integer.parseInt(request.getParameter("id")));
                if (!eliminado) {
                    response.sendRedirect(request.getContextPath() + "/libros?error=historial");
                    return;
                }

            } else if ("prestar".equals(accion)) {
                int libroId = Integer.parseInt(request.getParameter("id"));
                // Validacion en el servidor: no basta con ocultar el boton en la vista.
                boolean prestado = prestamoDAO.prestar(libroId, usuario.getId());
                if (!prestado) {
                    response.sendRedirect(request.getContextPath() + "/libros?error=noDisponible");
                    return;
                }
                response.sendRedirect(request.getContextPath() + "/prestamos");
                return;
            }
        } catch (NumberFormatException e) {
            // id inválido: se ignora y se recarga el catálogo
        } catch (java.sql.SQLException e) {
            throw new ServletException("Error actualizando el catálogo", e);
        }
        response.sendRedirect(request.getContextPath() + "/libros");
    }
}
