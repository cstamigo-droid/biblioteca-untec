package controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.PrestamoDAO;
import model.Usuario;
import view.Paginas;

/**
 * Controlador de préstamos.
 * GET: historial de préstamos (vista prestamos.jsp).
 * POST: registrar una devolución. Exige sesión iniciada.
 */
@WebServlet("/prestamos")
public class PrestamoServlet extends HttpServlet {

    private final PrestamoDAO prestamoDAO = new PrestamoDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Usuario u = (Usuario) request.getSession().getAttribute("usuario");
        if (u == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }
        try {
            request.setAttribute("prestamos", prestamoDAO.listar());
        } catch (java.sql.SQLException e) {
            throw new ServletException("Error listando los préstamos", e);
        }
        request.getRequestDispatcher(Paginas.PRESTAMOS).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (request.getSession().getAttribute("usuario") == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }
        try {
            if ("devolver".equals(request.getParameter("accion"))) {
                prestamoDAO.devolver(Integer.parseInt(request.getParameter("id")));
            }
        } catch (NumberFormatException e) {
            // id inválido: se ignora
        } catch (java.sql.SQLException e) {
            throw new ServletException("Error registrando la devolución", e);
        }
        response.sendRedirect(request.getContextPath() + "/prestamos");
    }
}
