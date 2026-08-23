package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO de USUARIOS: valida las credenciales del login contra la base de datos.
 * Todo el SQL de la entidad vive aquí (patrón DAO).
 */
public class UsuarioDAO {

    /**
     * Busca un usuario por email y clave.
     * @return el Usuario si las credenciales son correctas, o null si no.
     */
    public Usuario validar(String email, String clave) throws SQLException {
        String sql = "SELECT id, nombre, email FROM USUARIOS WHERE email = ? AND clave = ?";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, clave);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(rs.getInt("id"), rs.getString("nombre"),
                            rs.getString("email"));
                }
                return null;
            }
        }
    }
}
