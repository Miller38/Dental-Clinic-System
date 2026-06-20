package com.dentalclinicsystem.dao;

import com.dentalclinicsystem.config.ConexionSQLite;
import com.dentalclinicsystem.model.Configuracion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConfiguracionDAO {
    
    public boolean guardar(String clave, String valor) {
        String sql = "INSERT OR REPLACE INTO configuracion (clave, valor) VALUES (?, ?)";
        
        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, clave);
            pstmt.setString(2, valor);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al guardar configuración: " + e.getMessage());
            return false;
        }
    }
    
    public boolean guardar(Configuracion config) {
        String sql = "INSERT OR REPLACE INTO configuracion (clave, valor, descripcion, categoria) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, config.getClave());
            pstmt.setString(2, config.getValor());
            pstmt.setString(3, config.getDescripcion());
            pstmt.setString(4, config.getCategoria());
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al guardar configuración: " + e.getMessage());
            return false;
        }
    }
    
    public String obtener(String clave) {
        String sql = "SELECT valor FROM configuracion WHERE clave = ?";
        
        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, clave);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("valor");
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener configuración: " + e.getMessage());
        }
        return null;
    }
    
    public Configuracion obtenerConfiguracion(String clave) {
        String sql = "SELECT * FROM configuracion WHERE clave = ?";
        
        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, clave);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapearConfiguracion(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener configuración: " + e.getMessage());
        }
        return null;
    }
    
    public List<Configuracion> obtenerPorCategoria(String categoria) {
        List<Configuracion> configs = new ArrayList<>();
        String sql = "SELECT * FROM configuracion WHERE categoria = ? ORDER BY clave";
        
        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, categoria);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                configs.add(mapearConfiguracion(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener configuraciones: " + e.getMessage());
        }
        return configs;
    }
    
    public List<Configuracion> obtenerTodas() {
        List<Configuracion> configs = new ArrayList<>();
        String sql = "SELECT * FROM configuracion ORDER BY categoria, clave";
        
        try (Connection conn = ConexionSQLite.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                configs.add(mapearConfiguracion(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener configuraciones: " + e.getMessage());
        }
        return configs;
    }
    
    public boolean eliminar(String clave) {
        String sql = "DELETE FROM configuracion WHERE clave = ?";
        
        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, clave);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar configuración: " + e.getMessage());
            return false;
        }
    }
    
    private Configuracion mapearConfiguracion(ResultSet rs) throws SQLException {
        Configuracion config = new Configuracion();
        config.setId(rs.getInt("id"));
        config.setClave(rs.getString("clave"));
        config.setValor(rs.getString("valor"));
        config.setDescripcion(rs.getString("descripcion"));
        config.setCategoria(rs.getString("categoria"));
        return config;
    }
}