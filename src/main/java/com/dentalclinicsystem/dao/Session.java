package com.dentalclinicsystem.dao;

import com.dentalclinicsystem.config.ConexionSQLite;
import com.dentalclinicsystem.model.Usuario;
import com.dentalclinicsystem.service.BackupService;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Session {
    
    private static Session instancia;
    private Usuario usuarioActual;
    private String loginTime;
    private String ultimaActividad;
    private int sesionId;
    
    private Session() {
        this.sesionId = -1;
    }
    
    public static Session getInstance() {
        if (instancia == null) {
            instancia = new Session();
        }
        return instancia;
    }
    
    public void iniciarSesion(Usuario usuario) {
        this.usuarioActual = usuario;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.loginTime = LocalDateTime.now().format(formatter);
        this.ultimaActividad = loginTime;
        this.sesionId = registrarInicioSesion(usuario.getUsuario(), loginTime);
    }
    
    private int registrarInicioSesion(String usuario, String login) {
        String sql = "INSERT INTO sesiones (usuario, login) VALUES (?, ?)";
        
        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, usuario);
            pstmt.setString(2, login);
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al registrar inicio de sesión: " + e.getMessage());
        }
        return -1;
    }
    
    public void cerrarSesion() {
        if (usuarioActual != null && sesionId != -1) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String logoutTime = LocalDateTime.now().format(formatter);
            
            registrarCierreSesion(usuarioActual.getUsuario(), logoutTime, sesionId);
            
            // Crear backup automático al cerrar sesión
            try {
                System.out.println("💾 Creando backup automático antes de cerrar sesión...");
                BackupService backupService = new BackupService();
                backupService.crearBackupAutomatico();
            } catch (Exception e) {
                System.err.println("❌ Error al crear backup automático: " + e.getMessage());
                e.printStackTrace();
            }
            
            this.usuarioActual = null;
            this.loginTime = null;
            this.ultimaActividad = null;
            this.sesionId = -1;
        }
    }
    
    private void registrarCierreSesion(String usuario, String logout, int sesionId) {
        String sql = "UPDATE sesiones SET logout = ?, duracion_minutos = (strftime('%s', ?) - strftime('%s', login)) / 60 WHERE id = ? AND usuario = ?";
        
        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, logout);
            pstmt.setString(2, logout);
            pstmt.setInt(3, sesionId);
            pstmt.setString(4, usuario);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Error al registrar cierre de sesión: " + e.getMessage());
        }
    }
    
    public void actualizarActividad() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.ultimaActividad = LocalDateTime.now().format(formatter);
        actualizarUltimaActividadBD();
    }
    
    private void actualizarUltimaActividadBD() {
        if (sesionId != -1) {
            String sql = "UPDATE sesiones SET ultima_actividad = ? WHERE id = ?";
            
            try (Connection conn = ConexionSQLite.conectar();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
                pstmt.setString(1, ultimaActividad);
                pstmt.setInt(2, sesionId);
                pstmt.executeUpdate();
                
            } catch (SQLException e) {
                System.err.println("Error al actualizar actividad: " + e.getMessage());
            }
        }
    }
    
    public boolean isSesionActiva() {
        return usuarioActual != null;
    }
    
    public boolean hasRole(String rol) {
        return usuarioActual != null && usuarioActual.getRol().equals(rol);
    }
    
    public boolean hasAnyRole(String... roles) {
        if (usuarioActual == null) return false;
        for (String rol : roles) {
            if (usuarioActual.getRol().equals(rol)) {
                return true;
            }
        }
        return false;
    }
    
    public static void setUsuarioActual(Usuario usuario) {
        getInstance().iniciarSesion(usuario);
    }
    
    public static Usuario getUsuarioActual() {
        return getInstance().usuarioActual;
    }
    
    public static void cerrarSession() {
        getInstance().cerrarSesion();
    }
    
    public static boolean estaActiva() {
        return getInstance().isSesionActiva();
    }
    
    public String getLoginTime() { return loginTime; }
    public String getUltimaActividad() { return ultimaActividad; }
    public int getSesionId() { return sesionId; }
}