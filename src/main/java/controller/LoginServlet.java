package controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.Usuario;
import model.UsuarioDAO;

/**
 * Controlador del inicio de sesión. Recibe por POST los datos del
 * formulario del index.jsp, los valida contra la base de datos y,
 * si son correctos, guarda al usuario en la sesión (HttpSession).
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Entrar por GET siempre lleva al formulario de login
        response.sendRedirect(request.getContextPath() + "/index.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String email = request.getParameter("email");
        String clave = request.getParameter("clave");

        // Validación en el servidor: campos obligatorios
        if (email == null || email.trim().isEmpty() || clave == null || clave.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/index.jsp?error=1");
            return;
        }

        try {
            Usuario usuario = usuarioDAO.validar(email.trim(), clave);
            if (usuario == null) {
                // Credenciales incorrectas: de vuelta al login con aviso
                response.sendRedirect(request.getContextPath() + "/index.jsp?error=1");
                return;
            }
            // Sesión iniciada: el usuario queda disponible para toda la app
            request.getSession().setAttribute("usuario", usuario);
            response.sendRedirect(request.getContextPath() + "/libros");
        } catch (java.sql.SQLException e) {
            throw new ServletException("Error validando el usuario", e);
        }
    }
}
