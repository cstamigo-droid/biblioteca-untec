package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas de las reglas de préstamo y devolución: son las que podrían
 * dejar el catálogo en un estado imposible si fallaran.
 */
class PrestamoDAOTest {

    private static int contador = 0;
    private final PrestamoDAO dao = new PrestamoDAO();
    private final LibroDAO libroDAO = new LibroDAO();

    @BeforeEach
    void baseLimpia() throws SQLException {
        ConexionBD.cerrar();
        System.setProperty("BIBLIOTECA_DB_URL",
                "jdbc:h2:mem:prestamos" + (++contador) + ";DB_CLOSE_DELAY=-1");
    }

    @AfterEach
    void cerrar() throws SQLException {
        ConexionBD.cerrar();
        System.clearProperty("BIBLIOTECA_DB_URL");
    }

    private boolean estaDisponible(int id) throws SQLException {
        return libroDAO.listar().stream()
                .filter(l -> l.getId() == id).findFirst().orElseThrow().isDisponible();
    }

    @Test
    @DisplayName("Prestar un libro lo deja NO disponible y registra el préstamo")
    void prestarLibroDisponible() throws SQLException {
        int id = libroDAO.listar().get(0).getId();

        assertTrue(dao.prestar(id, 1));
        assertFalse(estaDisponible(id), "el libro prestado no puede seguir disponible");
        assertEquals(1, dao.listar().size());
    }

    @Test
    @DisplayName("REGLA: un libro ya prestado NO se puede prestar de nuevo")
    void noSePrestaDosVecesElMismoLibro() throws SQLException {
        int id = libroDAO.listar().get(0).getId();
        assertTrue(dao.prestar(id, 1));

        assertFalse(dao.prestar(id, 2), "el segundo préstamo debe rechazarse");
        assertEquals(1, dao.listar().size(), "no debe existir un segundo préstamo");
    }

    @Test
    @DisplayName("Prestar un libro que no existe devuelve false")
    void prestarLibroInexistente() throws SQLException {
        assertFalse(dao.prestar(9999, 1));
        assertEquals(0, dao.listar().size());
    }

    @Test
    @DisplayName("Devolver libera el libro y marca el préstamo como devuelto")
    void devolverLiberaElLibro() throws SQLException {
        int id = libroDAO.listar().get(0).getId();
        dao.prestar(id, 1);
        int prestamoId = dao.listar().get(0).getId();

        assertTrue(dao.devolver(prestamoId));
        assertTrue(estaDisponible(id), "al devolver, el libro vuelve al catálogo");
        assertTrue(dao.listar().get(0).isDevuelto());
    }

    @Test
    @DisplayName("REGLA: el mismo préstamo NO se puede devolver dos veces")
    void noSeDevuelveDosVeces() throws SQLException {
        int id = libroDAO.listar().get(0).getId();
        dao.prestar(id, 1);
        int prestamoId = dao.listar().get(0).getId();
        assertTrue(dao.devolver(prestamoId));

        assertFalse(dao.devolver(prestamoId), "la segunda devolución debe rechazarse");
        assertEquals(1, dao.listar().size());
    }

    @Test
    @DisplayName("Devolver un préstamo que no existe devuelve false")
    void devolverPrestamoInexistente() throws SQLException {
        assertFalse(dao.devolver(9999));
    }

    @Test
    @DisplayName("Un libro devuelto se puede volver a prestar")
    void cicloCompletoPrestarDevolverPrestar() throws SQLException {
        int id = libroDAO.listar().get(0).getId();
        dao.prestar(id, 1);
        dao.devolver(dao.listar().get(0).getId());

        assertTrue(dao.prestar(id, 2), "tras la devolución el libro vuelve a prestarse");
        assertEquals(2, dao.listar().size());
    }
}
