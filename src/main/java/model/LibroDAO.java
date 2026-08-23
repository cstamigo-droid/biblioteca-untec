package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de LIBROS con las operaciones CRUD del catálogo:
 * Crear (agregar), Leer (listar), Actualizar (disponibilidad) y Eliminar.
 */
public class LibroDAO {

    /** READ: catálogo completo, ordenado por título. */
    public List<Libro> listar() throws SQLException {
        List<Libro> libros = new ArrayList<>();
        String sql = "SELECT id, titulo, autor, disponible FROM LIBROS ORDER BY titulo";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                libros.add(new Libro(rs.getInt("id"), rs.getString("titulo"),
                        rs.getString("autor"), rs.getBoolean("disponible")));
            }
        }
        return libros;
    }

    /** CREATE: agrega un libro nuevo al catálogo (queda disponible). */
    public void agregar(String titulo, String autor) throws SQLException {
        String sql = "INSERT INTO LIBROS (titulo, autor) VALUES (?, ?)";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            ps.setString(1, titulo);
            ps.setString(2, autor);
            ps.executeUpdate();
        }
    }

    /** UPDATE: marca un libro como disponible o prestado. */
    public void cambiarDisponibilidad(int id, boolean disponible) throws SQLException {
        String sql = "UPDATE LIBROS SET disponible = ? WHERE id = ?";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            ps.setBoolean(1, disponible);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    /**
     * DELETE: elimina un libro del catálogo. Solo se permite si está
     * disponible y no tiene historial de préstamos (integridad referencial:
     * el historial es un registro que no debe perderse).
     * @return true si se eliminó; false si estaba prestado o tiene historial.
     */
    public boolean eliminar(int id) throws SQLException {
        String conteo = "SELECT COUNT(*) FROM PRESTAMOS WHERE libro_id = ?";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(conteo)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                if (rs.getInt(1) > 0) return false;   // tiene historial: no se borra
            }
        }
        String sql = "DELETE FROM LIBROS WHERE id = ? AND disponible = TRUE";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}
