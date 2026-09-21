package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.sql.SQLException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Pruebas del login: qué ocurre cuando las credenciales no son correctas. */
class UsuarioDAOTest {

    private static int contador = 0;
    private final UsuarioDAO dao = new UsuarioDAO();

    @BeforeEach
    void baseLimpia() throws SQLException {
        ConexionBD.cerrar();
        System.setProperty("BIBLIOTECA_DB_URL",
                "jdbc:h2:mem:usuarios" + (++contador) + ";DB_CLOSE_DELAY=-1");
    }

    @AfterEach
    void cerrar() throws SQLException {
        ConexionBD.cerrar();
        System.clearProperty("BIBLIOTECA_DB_URL");
    }

    @Test
    @DisplayName("Credenciales correctas devuelven el usuario")
    void loginCorrecto() throws SQLException {
        Usuario u = dao.validar("biblioteca@untec.cl", "untec2026");
        assertNotNull(u, "un usuario sembrado debe poder entrar");
        assertEquals("Bibliotecaria UNTEC", u.getNombre());
    }

    @Test
    @DisplayName("La clave incorrecta NO deja entrar")
    void claveIncorrecta() throws SQLException {
        assertNull(dao.validar("biblioteca@untec.cl", "clave-equivocada"));
    }

    @Test
    @DisplayName("Un email que no existe NO deja entrar")
    void emailInexistente() throws SQLException {
        assertNull(dao.validar("nadie@untec.cl", "untec2026"));
    }

    @Test
    @DisplayName("Una comilla en el email no rompe la consulta (PreparedStatement)")
    void inyeccionSqlNoFunciona() throws SQLException {
        assertNull(dao.validar("' OR '1'='1", "' OR '1'='1"));
    }
}
