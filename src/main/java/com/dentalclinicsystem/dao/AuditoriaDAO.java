package com.dentalclinicsystem.dao;

import com.dentalclinicsystem.config.ConexionSQLite;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * DAO para registrar auditoría del sistema
 */
public class AuditoriaDAO {
    
    /**
     * Registra una acción en la auditoría
     * @param accion Descripción de la acción (INSERT, UPDATE, DELETE, LOGIN, etc.)
     * @param tabla Nombre de la tabla afectada
     * @param registroId ID del registro afectado
     * @param detalleAnterior JSON o texto con el estado anterior
     * @param detalleNuevo JSON o texto con el estado nuevo
     */
    public void registrar(String accion, String tabla, int registroId, 
                         String detalleAnterior, String detalleNuevo) {
        String sql = "INSERT INTO auditoria (usuario, accion, tabla, registro_id, "
                   + "detalle_anterior, detalle_nuevo, fecha) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String usuario = "SISTEMA";
            if (Session.getInstance().isSesionActiva() && Session.getUsuarioActual() != null) {
                usuario = Session.getUsuarioActual().getUsuario();
            }
            
            pstmt.setString(1, usuario);
            pstmt.setString(2, accion);
            pstmt.setString(3, tabla);
            
            if (registroId > 0) {
                pstmt.setInt(4, registroId);
            } else {
                pstmt.setNull(4, java.sql.Types.INTEGER);
            }
            
            pstmt.setString(5, detalleAnterior);
            pstmt.setString(6, detalleNuevo);
            pstmt.setString(7, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Error al registrar auditoría: " + e.getMessage());
        }
    }
    
    /**
     * Registro simplificado (sin detalles)
     */
    public void registrar(String accion, String tabla, int registroId) {
        registrar(accion, tabla, registroId, null, null);
    }
    
    /**
     * Registro solo con acción
     */
    public void registrar(String accion) {
        registrar(accion, null, 0, null, null);
    }
    
    /**
     * Registro para eventos de login con usuario específico
     * @param usuario Nombre del usuario
     * @param accion Acción realizada
     */
    public void registrar(String usuario, String accion) {
        String sql = "INSERT INTO auditoria (usuario, accion, fecha) VALUES (?, ?, ?)";
        
        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, usuario);
            pstmt.setString(2, accion);
            pstmt.setString(3, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Error al registrar auditoría: " + e.getMessage());
        }
    }
}