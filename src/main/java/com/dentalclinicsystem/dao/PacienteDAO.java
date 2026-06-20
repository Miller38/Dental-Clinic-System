package com.dentalclinicsystem.dao;

import com.dentalclinicsystem.config.ConexionSQLite;
import com.dentalclinicsystem.model.Paciente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PacienteDAO {

    public boolean insertar(Paciente paciente) {
        String sql = "INSERT INTO pacientes (nombre, apellido, fecha_nacimiento, edad, genero, " +
                     "tipo_documento, numero_documento, telefono, telefono_alternativo, email, " +
                     "direccion, ocupacion, estado_civil, estado, alergias, enfermedades_sistema, " +
                     "medicamentos, contacto_emergencia_nombre, contacto_emergencia_telefono) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, paciente.getNombre());
            pstmt.setString(2, paciente.getApellido());
            pstmt.setString(3, paciente.getFechaNacimiento());
            pstmt.setInt(4, paciente.getEdad());
            pstmt.setString(5, paciente.getGenero());
            pstmt.setString(6, paciente.getTipoDocumento());
            pstmt.setString(7, paciente.getNumeroDocumento());
            pstmt.setString(8, paciente.getTelefono());
            pstmt.setString(9, paciente.getTelefonoAlternativo());
            pstmt.setString(10, paciente.getEmail());
            pstmt.setString(11, paciente.getDireccion());
            pstmt.setString(12, paciente.getOcupacion());
            pstmt.setString(13, paciente.getEstadoCivil());
            pstmt.setInt(14, paciente.getEstado());
            pstmt.setString(15, paciente.getAlergias());
            pstmt.setString(16, paciente.getEnfermedadesSistema());
            pstmt.setString(17, paciente.getMedicamentos());
            pstmt.setString(18, paciente.getContactoEmergenciaNombre());
            pstmt.setString(19, paciente.getContactoEmergenciaTelefono());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    paciente.setId(rs.getInt(1));
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error al insertar paciente: " + e.getMessage());
            return false;
        }
    }

    public Paciente obtenerPorId(int id) {
        String sql = "SELECT * FROM pacientes WHERE id = ?";

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapearPaciente(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener paciente: " + e.getMessage());
        }
        return null;
    }

    public Paciente obtenerPorDocumento(String documento) {
        String sql = "SELECT * FROM pacientes WHERE numero_documento = ?";

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, documento);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapearPaciente(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener paciente: " + e.getMessage());
        }
        return null;
    }

    public List<Paciente> obtenerTodos() {
        List<Paciente> pacientes = new ArrayList<>();
        String sql = "SELECT * FROM pacientes WHERE estado = 1 ORDER BY apellido, nombre";

        try (Connection conn = ConexionSQLite.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                pacientes.add(mapearPaciente(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener pacientes: " + e.getMessage());
        }
        return pacientes;
    }

    public List<Paciente> buscar(String texto) {
        List<Paciente> pacientes = new ArrayList<>();
        String sql = "SELECT * FROM pacientes WHERE estado = 1 AND " +
                     "(nombre LIKE ? OR apellido LIKE ? OR numero_documento LIKE ? OR telefono LIKE ?) " +
                     "ORDER BY apellido, nombre";

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String busqueda = "%" + texto + "%";
            pstmt.setString(1, busqueda);
            pstmt.setString(2, busqueda);
            pstmt.setString(3, busqueda);
            pstmt.setString(4, busqueda);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                pacientes.add(mapearPaciente(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar pacientes: " + e.getMessage());
        }
        return pacientes;
    }

    public boolean actualizar(Paciente paciente) {
        String sql = "UPDATE pacientes SET nombre = ?, apellido = ?, fecha_nacimiento = ?, edad = ?, " +
                     "genero = ?, tipo_documento = ?, numero_documento = ?, telefono = ?, " +
                     "telefono_alternativo = ?, email = ?, direccion = ?, ocupacion = ?, " +
                     "estado_civil = ?, estado = ?, alergias = ?, enfermedades_sistema = ?, " +
                     "medicamentos = ?, contacto_emergencia_nombre = ?, contacto_emergencia_telefono = ? " +
                     "WHERE id = ?";

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, paciente.getNombre());
            pstmt.setString(2, paciente.getApellido());
            pstmt.setString(3, paciente.getFechaNacimiento());
            pstmt.setInt(4, paciente.getEdad());
            pstmt.setString(5, paciente.getGenero());
            pstmt.setString(6, paciente.getTipoDocumento());
            pstmt.setString(7, paciente.getNumeroDocumento());
            pstmt.setString(8, paciente.getTelefono());
            pstmt.setString(9, paciente.getTelefonoAlternativo());
            pstmt.setString(10, paciente.getEmail());
            pstmt.setString(11, paciente.getDireccion());
            pstmt.setString(12, paciente.getOcupacion());
            pstmt.setString(13, paciente.getEstadoCivil());
            pstmt.setInt(14, paciente.getEstado());
            pstmt.setString(15, paciente.getAlergias());
            pstmt.setString(16, paciente.getEnfermedadesSistema());
            pstmt.setString(17, paciente.getMedicamentos());
            pstmt.setString(18, paciente.getContactoEmergenciaNombre());
            pstmt.setString(19, paciente.getContactoEmergenciaTelefono());
            pstmt.setInt(20, paciente.getId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar paciente: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sql = "UPDATE pacientes SET estado = 0 WHERE id = ?";

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar paciente: " + e.getMessage());
            return false;
        }
    }

    public int contarPacientes() {
        String sql = "SELECT COUNT(*) FROM pacientes WHERE estado = 1";

        try (Connection conn = ConexionSQLite.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error al contar pacientes: " + e.getMessage());
        }
        return 0;
    }

    private Paciente mapearPaciente(ResultSet rs) throws SQLException {
        Paciente paciente = new Paciente();
        paciente.setId(rs.getInt("id"));
        paciente.setNombre(rs.getString("nombre"));
        paciente.setApellido(rs.getString("apellido"));
        paciente.setFechaNacimiento(rs.getString("fecha_nacimiento"));
        paciente.setEdad(rs.getInt("edad"));
        paciente.setGenero(rs.getString("genero"));
        paciente.setTipoDocumento(rs.getString("tipo_documento"));
        paciente.setNumeroDocumento(rs.getString("numero_documento"));
        paciente.setTelefono(rs.getString("telefono"));
        paciente.setTelefonoAlternativo(rs.getString("telefono_alternativo"));
        paciente.setEmail(rs.getString("email"));
        paciente.setDireccion(rs.getString("direccion"));
        paciente.setOcupacion(rs.getString("ocupacion"));
        paciente.setEstadoCivil(rs.getString("estado_civil"));
        paciente.setEstado(rs.getInt("estado"));
        paciente.setFechaRegistro(rs.getString("fecha_registro"));
        paciente.setAlergias(rs.getString("alergias"));
        paciente.setEnfermedadesSistema(rs.getString("enfermedades_sistema"));
        paciente.setMedicamentos(rs.getString("medicamentos"));
        paciente.setContactoEmergenciaNombre(rs.getString("contacto_emergencia_nombre"));
        paciente.setContactoEmergenciaTelefono(rs.getString("contacto_emergencia_telefono"));
        return paciente;
    }
}