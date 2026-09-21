package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de PRESTAMOS: crear un préstamo, registrar la devolución y listar
 * el historial. Al prestar o devolver también se actualiza la
 * disponibilidad del libro (las dos operaciones van juntas).
 */
public class PrestamoDAO {

    private final LibroDAO libroDAO = new LibroDAO();

    /**
     * CREATE: presta un libro al usuario indicado y lo deja no disponible.
     *
     * <p>Regla de negocio: solo se presta un libro que esta DISPONIBLE. La
     * comprobacion vive en el servidor y no en la vista: ocultar el boton
     * "Prestar" no impide que llegue un POST directo con el id de un libro
     * ya prestado, lo que crearia un segundo prestamo del mismo ejemplar.</p>
     *
     * @return true si el prestamo se registro; false si el libro no estaba
     *         disponible o no existe.
     */
    public boolean prestar(int libroId, int usuarioId) throws SQLException {
        String verificar = "SELECT disponible FROM LIBROS WHERE id = ?";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(verificar)) {
            ps.setInt(1, libroId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next() || !rs.getBoolean(1)) {
                    return false;   // no existe o ya esta prestado
                }
            }
        }
        String sql = "INSERT INTO PRESTAMOS (libro_id, usuario_id, fecha_prestamo) "
                + "VALUES (?, ?, CURRENT_DATE)";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            ps.setInt(1, libroId);
            ps.setInt(2, usuarioId);
            ps.executeUpdate();
        }
        libroDAO.cambiarDisponibilidad(libroId, false);
        return true;
    }

    /**
     * UPDATE: registra la devolucion y libera el libro.
     * Regla: un mismo prestamo solo puede devolverse una vez.
     * @return true si se registro la devolucion; false si el prestamo no
     *         existe o ya habia sido devuelto.
     */
    public boolean devolver(int prestamoId) throws SQLException {
        int libroId = -1;
        String buscar = "SELECT libro_id FROM PRESTAMOS WHERE id = ? AND devuelto = FALSE";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(buscar)) {
            ps.setInt(1, prestamoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) libroId = rs.getInt(1);
            }
        }
        if (libroId == -1) return false;   // no existe o ya estaba devuelto

        String marcar = "UPDATE PRESTAMOS SET devuelto = TRUE WHERE id = ?";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(marcar)) {
            ps.setInt(1, prestamoId);
            ps.executeUpdate();
        }
        libroDAO.cambiarDisponibilidad(libroId, true);
        return true;
    }

    /** READ: historial de préstamos con título y usuario (JOIN de 3 tablas). */
    public List<Prestamo> listar() throws SQLException {
        List<Prestamo> prestamos = new ArrayList<>();
        String sql = "SELECT p.id, l.titulo, u.nombre, p.fecha_prestamo, p.devuelto "
                + "FROM PRESTAMOS p "
                + "JOIN LIBROS l ON l.id = p.libro_id "
                + "JOIN USUARIOS u ON u.id = p.usuario_id "
                + "ORDER BY p.devuelto, p.fecha_prestamo DESC";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                prestamos.add(new Prestamo(rs.getInt(1), rs.getString(2), rs.getString(3),
                        String.valueOf(rs.getDate(4)), rs.getBoolean(5)));
            }
        }
        return prestamos;
    }
}
