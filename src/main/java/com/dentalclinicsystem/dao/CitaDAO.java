package com.dentalclinicsystem.dao;

import com.dentalclinicsystem.config.ConexionSQLite;
import com.dentalclinicsystem.model.Cita;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CitaDAO {

    public boolean insertar(Cita cita) {
        String sql = "INSERT INTO citas (paciente_id, odontologo_id, servicio_id, tratamiento_id, " +
                     "fecha, hora, duracion, estado, nota, recordatorio_enviado, modificada_por) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, cita.getPacienteId());
            pstmt.setInt(2, cita.getOdontologoId());
            pstmt.setInt(3, cita.getServicioId());
            
            if (cita.getTratamientoId() > 0) {
                pstmt.setInt(4, cita.getTratamientoId());
            } else {
                pstmt.setNull(4, java.sql.Types.INTEGER);
            }
            
            pstmt.setString(5, cita.getFecha());
            pstmt.setString(6, cita.getHora());
            pstmt.setInt(7, cita.getDuracion());
            pstmt.setString(8, cita.getEstado());
            pstmt.setString(9, cita.getNota());
            pstmt.setInt(10, cita.getRecordatorioEnviado());
            pstmt.setString(11, cita.getModificadaPor());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    cita.setId(rs.getInt(1));
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("❌ Error al insertar cita: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public Cita obtenerPorId(int id) {
        String sql = "SELECT c.*, " +
                     "p.nombre || ' ' || p.apellido as paciente_nombre, " +
                     "u.nombre as odontologo_nombre, " +
                     "s.nombre as servicio_nombre " +
                     "FROM citas c " +
                     "LEFT JOIN pacientes p ON c.paciente_id = p.id " +
                     "LEFT JOIN usuarios u ON c.odontologo_id = u.id " +
                     "LEFT JOIN servicios s ON c.servicio_id = s.id " +
                     "WHERE c.id = ?";

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapearCita(rs);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener cita: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<Cita> obtenerPorPaciente(int pacienteId) {
        List<Cita> citas = new ArrayList<>();
        String sql = "SELECT c.*, " +
                     "p.nombre || ' ' || p.apellido as paciente_nombre, " +
                     "u.nombre as odontologo_nombre, " +
                     "s.nombre as servicio_nombre " +
                     "FROM citas c " +
                     "LEFT JOIN pacientes p ON c.paciente_id = p.id " +
                     "LEFT JOIN usuarios u ON c.odontologo_id = u.id " +
                     "LEFT JOIN servicios s ON c.servicio_id = s.id " +
                     "WHERE c.paciente_id = ? ORDER BY c.fecha DESC, c.hora DESC";

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, pacienteId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                citas.add(mapearCita(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener citas por paciente: " + e.getMessage());
            e.printStackTrace();
        }
        return citas;
    }

    public List<Cita> obtenerPorOdontologo(int odontologoId, String fecha) {
        List<Cita> citas = new ArrayList<>();
        String sql = "SELECT c.*, " +
                     "p.nombre || ' ' || p.apellido as paciente_nombre, " +
                     "u.nombre as odontologo_nombre, " +
                     "s.nombre as servicio_nombre " +
                     "FROM citas c " +
                     "LEFT JOIN pacientes p ON c.paciente_id = p.id " +
                     "LEFT JOIN usuarios u ON c.odontologo_id = u.id " +
                     "LEFT JOIN servicios s ON c.servicio_id = s.id " +
                     "WHERE c.odontologo_id = ? AND c.fecha = ? " +
                     "AND c.estado NOT IN ('CANCELADA', 'COMPLETADA') " +
                     "ORDER BY c.hora";

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, odontologoId);
            pstmt.setString(2, fecha);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                citas.add(mapearCita(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener citas por odontólogo: " + e.getMessage());
            e.printStackTrace();
        }
        return citas;
    }

    public List<Cita> obtenerPorFecha(String fecha) {
        List<Cita> citas = new ArrayList<>();
        String sql = "SELECT c.*, " +
                     "p.nombre || ' ' || p.apellido as paciente_nombre, " +
                     "u.nombre as odontologo_nombre, " +
                     "s.nombre as servicio_nombre " +
                     "FROM citas c " +
                     "LEFT JOIN pacientes p ON c.paciente_id = p.id " +
                     "LEFT JOIN usuarios u ON c.odontologo_id = u.id " +
                     "LEFT JOIN servicios s ON c.servicio_id = s.id " +
                     "WHERE c.fecha = ? ORDER BY c.hora";

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, fecha);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                citas.add(mapearCita(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener citas por fecha: " + e.getMessage());
            e.printStackTrace();
        }
        return citas;
    }

    public List<Cita> obtenerPorRango(String fechaInicio, String fechaFin) {
        List<Cita> citas = new ArrayList<>();
        String sql = "SELECT c.*, " +
                     "p.nombre || ' ' || p.apellido as paciente_nombre, " +
                     "u.nombre as odontologo_nombre, " +
                     "s.nombre as servicio_nombre " +
                     "FROM citas c " +
                     "LEFT JOIN pacientes p ON c.paciente_id = p.id " +
                     "LEFT JOIN usuarios u ON c.odontologo_id = u.id " +
                     "LEFT JOIN servicios s ON c.servicio_id = s.id " +
                     "WHERE c.fecha BETWEEN ? AND ? " +
                     "AND c.estado NOT IN ('CANCELADA', 'COMPLETADA') " +
                     "ORDER BY c.fecha, c.hora";

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, fechaInicio);
            pstmt.setString(2, fechaFin);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                citas.add(mapearCita(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener citas por rango: " + e.getMessage());
            e.printStackTrace();
        }
        return citas;
    }

    public List<Cita> obtenerProximas(int limite) {
        List<Cita> citas = new ArrayList<>();
        String sql = "SELECT c.*, " +
                     "p.nombre || ' ' || p.apellido as paciente_nombre, " +
                     "u.nombre as odontologo_nombre, " +
                     "s.nombre as servicio_nombre " +
                     "FROM citas c " +
                     "LEFT JOIN pacientes p ON c.paciente_id = p.id " +
                     "LEFT JOIN usuarios u ON c.odontologo_id = u.id " +
                     "LEFT JOIN servicios s ON c.servicio_id = s.id " +
                     "WHERE c.fecha >= date('now') AND c.estado IN ('PROGRAMADA', 'CONFIRMADA') " +
                     "ORDER BY c.fecha, c.hora LIMIT ?";

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, limite);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                citas.add(mapearCita(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener próximas citas: " + e.getMessage());
            e.printStackTrace();
        }
        return citas;
    }

    public int contarCitasHoy() {
        String sql = "SELECT COUNT(*) FROM citas WHERE fecha = date('now') AND estado NOT IN ('CANCELADA', 'COMPLETADA')";

        try (Connection conn = ConexionSQLite.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al contar citas de hoy: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    public int contarCitasPorEstado(String estado) {
        String sql = "SELECT COUNT(*) FROM citas WHERE estado = ?";

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, estado);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al contar citas por estado: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    public int contarCitasPorOdontologoYFecha(int odontologoId, String fecha) {
        String sql = "SELECT COUNT(*) FROM citas WHERE odontologo_id = ? AND fecha = ? " +
                     "AND estado NOT IN ('CANCELADA', 'COMPLETADA')";

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, odontologoId);
            pstmt.setString(2, fecha);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al contar citas por odontólogo: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    public boolean actualizar(Cita cita) {
        String sql = "UPDATE citas SET paciente_id = ?, odontologo_id = ?, servicio_id = ?, " +
                     "tratamiento_id = ?, fecha = ?, hora = ?, duracion = ?, estado = ?, " +
                     "nota = ?, recordatorio_enviado = ?, modificada_por = ? WHERE id = ?";

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, cita.getPacienteId());
            pstmt.setInt(2, cita.getOdontologoId());
            pstmt.setInt(3, cita.getServicioId());
            
            if (cita.getTratamientoId() > 0) {
                pstmt.setInt(4, cita.getTratamientoId());
            } else {
                pstmt.setNull(4, java.sql.Types.INTEGER);
            }
            
            pstmt.setString(5, cita.getFecha());
            pstmt.setString(6, cita.getHora());
            pstmt.setInt(7, cita.getDuracion());
            pstmt.setString(8, cita.getEstado());
            pstmt.setString(9, cita.getNota());
            pstmt.setInt(10, cita.getRecordatorioEnviado());
            pstmt.setString(11, cita.getModificadaPor());
            pstmt.setInt(12, cita.getId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar cita: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizarEstado(int id, String estado) {
        String sql = "UPDATE citas SET estado = ? WHERE id = ?";

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, estado);
            pstmt.setInt(2, id);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar estado: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean marcarRecordatorioEnviado(int id) {
        String sql = "UPDATE citas SET recordatorio_enviado = 1 WHERE id = ?";

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error al marcar recordatorio: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM citas WHERE id = ?";

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar cita: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

   /**
 * Verifica si existe conflicto de horario para un odontólogo
 * @param odontologoId ID del odontólogo
 * @param fecha Fecha de la cita
 * @param hora Hora de la cita
 * @param duracion Duración en minutos
 * @param idExcluir ID de la cita a excluir (0 si es nueva)
 * @return true si hay conflicto, false si está disponible
 */
public boolean existeConflicto(int odontologoId, String fecha, String hora, int duracion, int idExcluir) {
    String sql = "SELECT COUNT(*) FROM citas WHERE odontologo_id = ? AND fecha = ? " +
                 "AND estado NOT IN ('CANCELADA', 'COMPLETADA') " +
                 "AND id != ? " +  // 🔥 EXCLUIR LA CITA ACTUAL
                 "AND ((hora <= ? AND datetime(fecha || ' ' || hora, '+' || duracion || ' minutes') > datetime(?)) " +
                 "OR (hora >= ? AND hora < datetime(?, '+' || ? || ' minutes')))";

    try (Connection conn = ConexionSQLite.conectar();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setInt(1, odontologoId);
        pstmt.setString(2, fecha);
        pstmt.setInt(3, idExcluir);  // 🔥 Excluir la cita actual
        pstmt.setString(4, hora);
        pstmt.setString(5, hora);
        pstmt.setString(6, hora);
        pstmt.setString(7, hora);
        pstmt.setInt(8, duracion);

        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) > 0;
        }

    } catch (SQLException e) {
        System.err.println("❌ Error al verificar conflicto: " + e.getMessage());
        e.printStackTrace();
    }
    return false;
}

    public List<String> getHorasOcupadas(int odontologoId, String fecha) {
        List<String> horasOcupadas = new ArrayList<>();
        String sql = "SELECT hora, duracion FROM citas WHERE odontologo_id = ? AND fecha = ? " +
                     "AND estado NOT IN ('CANCELADA', 'COMPLETADA')";

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, odontologoId);
            pstmt.setString(2, fecha);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String hora = rs.getString("hora");
                horasOcupadas.add(hora);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener horas ocupadas: " + e.getMessage());
            e.printStackTrace();
        }
        return horasOcupadas;
    }

    /**
     * Mapea un ResultSet a un objeto Cita
     * CORREGIDO: Ahora verifica correctamente si las columnas existen
     */
    private Cita mapearCita(ResultSet rs) throws SQLException {
        Cita cita = new Cita();
        cita.setId(rs.getInt("id"));
        cita.setPacienteId(rs.getInt("paciente_id"));
        cita.setOdontologoId(rs.getInt("odontologo_id"));
        cita.setServicioId(rs.getInt("servicio_id"));
        cita.setTratamientoId(rs.getInt("tratamiento_id"));
        cita.setFecha(rs.getString("fecha"));
        cita.setHora(rs.getString("hora"));
        cita.setDuracion(rs.getInt("duracion"));
        cita.setEstado(rs.getString("estado"));
        cita.setNota(rs.getString("nota"));
        cita.setRecordatorioEnviado(rs.getInt("recordatorio_enviado"));
        cita.setFechaCreacion(rs.getString("fecha_creacion"));
        cita.setModificadaPor(rs.getString("modificada_por"));
        
        // Obtener nombres de las columnas disponibles
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        java.util.HashSet<String> columnas = new java.util.HashSet<>();
        for (int i = 1; i <= columnCount; i++) {
            columnas.add(metaData.getColumnName(i));
        }
        
        // Asignar nombres solo si las columnas existen
        if (columnas.contains("paciente_nombre")) {
            cita.setPacienteNombre(rs.getString("paciente_nombre"));
        }
        
        if (columnas.contains("odontologo_nombre")) {
            cita.setOdontologoNombre(rs.getString("odontologo_nombre"));
        }
        
        if (columnas.contains("servicio_nombre")) {
            cita.setServicioNombre(rs.getString("servicio_nombre"));
        }
        
        return cita;
    }
    
    /**
 * Marca el recordatorio de 24h como enviado
 */
public boolean marcarRecordatorio24hEnviado(int id) {
    String sql = "UPDATE citas SET recordatorio_24h_enviado = 1 WHERE id = ?";
    try (Connection conn = ConexionSQLite.conectar();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setInt(1, id);
        return pstmt.executeUpdate() > 0;
    } catch (SQLException e) {
        System.err.println("❌ Error marcando recordatorio 24h: " + e.getMessage());
        return false;
    }
}

/**
 * Marca el recordatorio de 2h como enviado
 */
public boolean marcarRecordatorio2hEnviado(int id) {
    String sql = "UPDATE citas SET recordatorio_2h_enviado = 1 WHERE id = ?";
    try (Connection conn = ConexionSQLite.conectar();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setInt(1, id);
        return pstmt.executeUpdate() > 0;
    } catch (SQLException e) {
        System.err.println("❌ Error marcando recordatorio 2h: " + e.getMessage());
        return false;
    }
}

/**
 * Obtiene citas que necesitan recordatorio de 24h
 */
public List<Cita> obtenerCitasParaRecordatorio24h(String fecha) {
    List<Cita> citas = new ArrayList<>();
    String sql = "SELECT c.*, " +
                 "p.nombre || ' ' || p.apellido as paciente_nombre, " +
                 "u.nombre as odontologo_nombre, " +
                 "s.nombre as servicio_nombre " +
                 "FROM citas c " +
                 "LEFT JOIN pacientes p ON c.paciente_id = p.id " +
                 "LEFT JOIN usuarios u ON c.odontologo_id = u.id " +
                 "LEFT JOIN servicios s ON c.servicio_id = s.id " +
                 "WHERE c.fecha = ? " +
                 "AND c.estado IN ('PROGRAMADA', 'CONFIRMADA') " +
                 "AND c.recordatorio_24h_enviado = 0 " +
                 "ORDER BY c.hora";

    try (Connection conn = ConexionSQLite.conectar();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, fecha);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            citas.add(mapearCita(rs));
        }
    } catch (SQLException e) {
        System.err.println("❌ Error obteniendo citas para recordatorio 24h: " + e.getMessage());
    }
    return citas;
}
}