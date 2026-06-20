package com.dentalclinicsystem.dao;

import com.dentalclinicsystem.config.ConexionSQLite;
import com.dentalclinicsystem.model.Usuario;
import com.dentalclinicsystem.util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    // ========== INSERT ==========
    public boolean insertar(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nombre, usuario, password, rol, especialidad, " +
                     "numero_licencia, email, telefono, foto, estado, intentos, bloqueado) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, usuario.getNombre());
            pstmt.setString(2, usuario.getUsuario());
            pstmt.setString(3, PasswordUtil.encriptarPassword(usuario.getPassword()));
            pstmt.setString(4, usuario.getRol());
            pstmt.setString(5, usuario.getEspecialidad());
            pstmt.setString(6, usuario.getNumeroLicencia());
            pstmt.setString(7, usuario.getEmail());
            pstmt.setString(8, usuario.getTelefono());
            pstmt.setString(9, usuario.getFoto());
            pstmt.setInt(10, usuario.getEstado());
            pstmt.setInt(11, usuario.getIntentos());
            pstmt.setInt(12, usuario.getBloqueado());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    usuario.setId(rs.getInt(1));
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("❌ Error al insertar usuario: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ========== SELECT ==========
    public Usuario obtenerPorId(int id) {
        String sql = "SELECT * FROM usuarios WHERE id = ?";

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapearUsuario(rs);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener usuario: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public Usuario obtenerPorUsuario(String usuario) {
        String sql = "SELECT * FROM usuarios WHERE usuario = ?";

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, usuario);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapearUsuario(rs);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener usuario: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<Usuario> obtenerTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios ORDER BY nombre";

        try (Connection conn = ConexionSQLite.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener usuarios: " + e.getMessage());
            e.printStackTrace();
        }
        return usuarios;
    }

    public List<Usuario> obtenerActivos() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios WHERE estado = 1 ORDER BY nombre";

        try (Connection conn = ConexionSQLite.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener usuarios activos: " + e.getMessage());
            e.printStackTrace();
        }
        return usuarios;
    }

    public List<Usuario> obtenerPorRol(String rol) {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios WHERE rol = ? AND estado = 1 ORDER BY nombre";

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, rol);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener usuarios por rol: " + e.getMessage());
            e.printStackTrace();
        }
        return usuarios;
    }

    public List<Usuario> buscar(String texto) {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios WHERE nombre LIKE ? OR usuario LIKE ? OR email LIKE ? " +
                     "ORDER BY nombre";

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String busqueda = "%" + texto + "%";
            pstmt.setString(1, busqueda);
            pstmt.setString(2, busqueda);
            pstmt.setString(3, busqueda);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al buscar usuarios: " + e.getMessage());
            e.printStackTrace();
        }
        return usuarios;
    }

    public boolean existeUsuario(String usuario) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE usuario = ?";

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, usuario);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al verificar usuario: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // ========== UPDATE ==========
    public boolean actualizar(Usuario usuario) {
        String sql = "UPDATE usuarios SET nombre = ?, rol = ?, especialidad = ?, " +
                     "numero_licencia = ?, email = ?, telefono = ?, foto = ?, " +
                     "estado = ?, intentos = ?, bloqueado = ?, bloqueado_hasta = ? " +
                     "WHERE id = ?";

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, usuario.getNombre());
            pstmt.setString(2, usuario.getRol());
            pstmt.setString(3, usuario.getEspecialidad());
            pstmt.setString(4, usuario.getNumeroLicencia());
            pstmt.setString(5, usuario.getEmail());
            pstmt.setString(6, usuario.getTelefono());
            pstmt.setString(7, usuario.getFoto());
            pstmt.setInt(8, usuario.getEstado());
            pstmt.setInt(9, usuario.getIntentos());
            pstmt.setInt(10, usuario.getBloqueado());
            pstmt.setString(11, usuario.getBloqueadoHasta());
            pstmt.setInt(12, usuario.getId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar usuario: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizarPassword(int id, String passwordEncriptada) {
        String sql = "UPDATE usuarios SET password = ? WHERE id = ?";

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, passwordEncriptada);
            pstmt.setInt(2, id);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar password: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizarUltimoAcceso(int id) {
        String sql = "UPDATE usuarios SET ultimo_acceso = datetime('now') WHERE id = ?";

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar último acceso: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean incrementarIntentos(int id) {
        String sql = "UPDATE usuarios SET intentos = intentos + 1 WHERE id = ?";

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error al incrementar intentos: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean resetearIntentos(int id) {
        String sql = "UPDATE usuarios SET intentos = 0 WHERE id = ?";

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error al resetear intentos: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ========== DELETE ==========
    public boolean eliminar(int id) {
        String sql = "DELETE FROM usuarios WHERE id = ?";

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar usuario: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ========== CONTAR ==========
    public int contarUsuarios() {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE estado = 1";

        try (Connection conn = ConexionSQLite.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al contar usuarios: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    public int contarPorRol(String rol) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE rol = ? AND estado = 1";

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, rol);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al contar usuarios por rol: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    // ========== LOGIN ==========
    public Usuario login(String usuario, String password) {
        String passwordEncriptada = PasswordUtil.encriptarPassword(password);
        String sql = "SELECT * FROM usuarios WHERE usuario = ? AND password = ?";

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, usuario);
            pstmt.setString(2, passwordEncriptada);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Usuario user = mapearUsuario(rs);
                // Actualizar último acceso
                actualizarUltimoAcceso(user.getId());
                resetearIntentos(user.getId());
                return user;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error en login: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // ========== MAPEAR ==========
    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getInt("id"));
        usuario.setNombre(rs.getString("nombre"));
        usuario.setUsuario(rs.getString("usuario"));
        usuario.setPassword(rs.getString("password"));
        usuario.setRol(rs.getString("rol"));
        usuario.setEspecialidad(rs.getString("especialidad"));
        usuario.setNumeroLicencia(rs.getString("numero_licencia"));
        usuario.setEmail(rs.getString("email"));
        usuario.setTelefono(rs.getString("telefono"));
        usuario.setFoto(rs.getString("foto"));
        usuario.setEstado(rs.getInt("estado"));
        usuario.setIntentos(rs.getInt("intentos"));
        usuario.setBloqueado(rs.getInt("bloqueado"));
        usuario.setBloqueadoHasta(rs.getString("bloqueado_hasta"));
        usuario.setUltimoAcceso(rs.getString("ultimo_acceso"));
        usuario.setFechaCreacion(rs.getString("fecha_creacion"));
        return usuario;
    }
}