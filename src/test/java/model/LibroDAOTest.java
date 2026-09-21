package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Pruebas del CRUD del catálogo y de la regla de eliminación. */
class LibroDAOTest {

    private static int contador = 0;
    private final LibroDAO dao = new LibroDAO();

    @BeforeEach
    void baseLimpia() throws SQLException {
        ConexionBD.cerrar();
        System.setProperty("BIBLIOTECA_DB_URL",
                "jdbc:h2:mem:libros" + (++contador) + ";DB_CLOSE_DELAY=-1");
    }

    @AfterEach
    void cerrar() throws SQLException {
        ConexionBD.cerrar();
        System.clearProperty("BIBLIOTECA_DB_URL");
    }

    @Test
    @DisplayName("READ: el catálogo parte con los libros sembrados")
    void listarDevuelveElCatalogo() throws SQLException {
        assertEquals(4, dao.listar().size());
    }

    @Test
    @DisplayName("CREATE: un libro agregado aparece en el catálogo y queda disponible")
    void agregarLibro() throws SQLException {
        dao.agregar("Rayuela", "Julio Cortázar");
        List<Libro> libros = dao.listar();
        assertEquals(5, libros.size());
        Libro rayuela = libros.stream()
                .filter(l -> "Rayuela".equals(l.getTitulo()))
                .findFirst().orElseThrow();
        assertTrue(rayuela.isDisponible(), "un libro nuevo debe nacer disponible");
    }

    @Test
    @DisplayName("UPDATE: cambiar la disponibilidad se refleja en el catálogo")
    void cambiarDisponibilidad() throws SQLException {
        int id = dao.listar().get(0).getId();
        dao.cambiarDisponibilidad(id, false);
        Libro libro = dao.listar().stream()
                .filter(l -> l.getId() == id).findFirst().orElseThrow();
        assertFalse(libro.isDisponible());
    }

    @Test
    @DisplayName("DELETE: un libro sin historial sí se elimina")
    void eliminarLibroSinHistorial() throws SQLException {
        int id = dao.listar().get(0).getId();
        assertTrue(dao.eliminar(id));
        assertEquals(3, dao.listar().size());
    }

    @Test
    @DisplayName("REGLA: un libro CON historial de préstamos NO se elimina")
    void noSeEliminaLibroConHistorial() throws SQLException {
        int id = dao.listar().get(0).getId();
        new PrestamoDAO().prestar(id, 1);

        assertFalse(dao.eliminar(id), "borrarlo destruiría el historial de préstamos");
        assertEquals(4, dao.listar().size(), "el libro debe seguir en el catálogo");
    }

    @Test
    @DisplayName("DELETE: un id que no existe devuelve false y no rompe")
    void eliminarIdInexistente() throws SQLException {
        assertFalse(dao.eliminar(9999));
    }
}
