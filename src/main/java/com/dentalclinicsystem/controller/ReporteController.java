package com.dentalclinicsystem.controller;

import com.dentalclinicsystem.config.ConexionSQLite;
import com.dentalclinicsystem.model.Cita;
import com.dentalclinicsystem.model.Paciente;
import com.dentalclinicsystem.model.Servicio;
import com.dentalclinicsystem.model.Venta;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ReporteController {

    // ================================================================
    // ============== REPORTE DE PACIENTES =============================
    // ================================================================
    
    /**
     * Obtiene todos los pacientes para reporte
     */
    public List<Paciente> getReportePacientes() {
        List<Paciente> pacientes = new ArrayList<>();
        String sql = "SELECT * FROM pacientes WHERE estado = 1 ORDER BY apellido, nombre";
        
        try (Connection conn = ConexionSQLite.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Paciente p = new Paciente();
                p.setId(rs.getInt("id"));
                p.setNombre(rs.getString("nombre"));
                p.setApellido(rs.getString("apellido"));
                p.setNumeroDocumento(rs.getString("numero_documento"));
                p.setTelefono(rs.getString("telefono"));
                p.setEmail(rs.getString("email"));
                p.setGenero(rs.getString("genero"));
                p.setEdad(rs.getInt("edad"));
                p.setFechaRegistro(rs.getString("fecha_registro"));
                p.setEstado(rs.getInt("estado"));
                pacientes.add(p);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener pacientes: " + e.getMessage());
        }
        return pacientes;
    }
    
    /**
     * Obtiene pacientes por rango de fechas
     */
    public List<Paciente> getReportePacientesPorFechas(String fechaInicio, String fechaFin) {
        List<Paciente> pacientes = new ArrayList<>();
        String sql = "SELECT * FROM pacientes WHERE estado = 1 AND fecha_registro BETWEEN ? AND ? ORDER BY apellido, nombre";
        
        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, fechaInicio);
            pstmt.setString(2, fechaFin + " 23:59:59");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Paciente p = new Paciente();
                p.setId(rs.getInt("id"));
                p.setNombre(rs.getString("nombre"));
                p.setApellido(rs.getString("apellido"));
                p.setNumeroDocumento(rs.getString("numero_documento"));
                p.setTelefono(rs.getString("telefono"));
                p.setEmail(rs.getString("email"));
                p.setGenero(rs.getString("genero"));
                p.setEdad(rs.getInt("edad"));
                p.setFechaRegistro(rs.getString("fecha_registro"));
                p.setEstado(rs.getInt("estado"));
                pacientes.add(p);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener pacientes: " + e.getMessage());
        }
        return pacientes;
    }
    
    /**
     * Obtiene estadísticas de pacientes (hombres/mujeres)
     */
    public int[] getEstadisticasPacientes() {
        int[] stats = new int[2]; // [hombres, mujeres]
        String sql = "SELECT genero, COUNT(*) as total FROM pacientes WHERE estado = 1 GROUP BY genero";
        
        try (Connection conn = ConexionSQLite.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String genero = rs.getString("genero");
                int total = rs.getInt("total");
                if ("M".equals(genero)) {
                    stats[0] = total;
                } else if ("F".equals(genero)) {
                    stats[1] = total;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener estadísticas: " + e.getMessage());
        }
        return stats;
    }
    
    /**
     * Obtiene pacientes nuevos por mes
     */
    public int[] getPacientesPorMes() {
        int[] meses = new int[12];
        String sql = "SELECT strftime('%m', fecha_registro) as mes, COUNT(*) as total FROM pacientes " +
                     "WHERE estado = 1 AND strftime('%Y', fecha_registro) = strftime('%Y', 'now') " +
                     "GROUP BY mes ORDER BY mes";
        
        try (Connection conn = ConexionSQLite.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                int mes = Integer.parseInt(rs.getString("mes")) - 1;
                meses[mes] = rs.getInt("total");
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener pacientes por mes: " + e.getMessage());
        }
        return meses;
    }

    // ================================================================
    // ============== REPORTE DE CITAS ================================
    // ================================================================
    
    /**
     * Obtiene todas las citas para reporte
     */
    public List<Cita> getReporteCitas(String fechaInicio, String fechaFin) {
        List<Cita> citas = new ArrayList<>();
        String sql = "SELECT c.*, p.nombre || ' ' || p.apellido as paciente_nombre, " +
                     "u.nombre as odontologo_nombre, s.nombre as servicio_nombre " +
                     "FROM citas c " +
                     "LEFT JOIN pacientes p ON c.paciente_id = p.id " +
                     "LEFT JOIN usuarios u ON c.odontologo_id = u.id " +
                     "LEFT JOIN servicios s ON c.servicio_id = s.id " +
                     "WHERE c.fecha BETWEEN ? AND ? ORDER BY c.fecha, c.hora";
        
        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, fechaInicio);
            pstmt.setString(2, fechaFin);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Cita c = new Cita();
                c.setId(rs.getInt("id"));
                c.setPacienteId(rs.getInt("paciente_id"));
                c.setOdontologoId(rs.getInt("odontologo_id"));
                c.setServicioId(rs.getInt("servicio_id"));
                c.setFecha(rs.getString("fecha"));
                c.setHora(rs.getString("hora"));
                c.setEstado(rs.getString("estado"));
                c.setDuracion(rs.getInt("duracion"));
                c.setNota(rs.getString("nota"));
                c.setPacienteNombre(rs.getString("paciente_nombre"));
                c.setOdontologoNombre(rs.getString("odontologo_nombre"));
                c.setServicioNombre(rs.getString("servicio_nombre"));
                citas.add(c);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener citas: " + e.getMessage());
        }
        return citas;
    }
    
    /**
     * Obtiene estadísticas de citas por estado
     */
    public int[] getEstadisticasCitas() {
        int[] stats = new int[6]; // programadas, confirmadas, en_proceso, completadas, canceladas, no_asistio
        String sql = "SELECT estado, COUNT(*) as total FROM citas GROUP BY estado";
        
        try (Connection conn = ConexionSQLite.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String estado = rs.getString("estado");
                int total = rs.getInt("total");
                switch (estado) {
                    case "PROGRAMADA": stats[0] = total; break;
                    case "CONFIRMADA": stats[1] = total; break;
                    case "EN_PROCESO": stats[2] = total; break;
                    case "COMPLETADA": stats[3] = total; break;
                    case "CANCELADA": stats[4] = total; break;
                    case "NO_ASISTIO": stats[5] = total; break;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener estadísticas de citas: " + e.getMessage());
        }
        return stats;
    }
    
    /**
     * Obtiene citas por odontólogo
     */
    public List<Object[]> getCitasPorOdontologo(String fechaInicio, String fechaFin) {
        List<Object[]> resultados = new ArrayList<>();
        String sql = "SELECT u.nombre as odontologo, COUNT(*) as total " +
                     "FROM citas c JOIN usuarios u ON c.odontologo_id = u.id " +
                     "WHERE c.fecha BETWEEN ? AND ? " +
                     "GROUP BY u.nombre ORDER BY total DESC";
        
        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, fechaInicio);
            pstmt.setString(2, fechaFin);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                resultados.add(new Object[]{rs.getString("odontologo"), rs.getInt("total")});
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener citas por odontólogo: " + e.getMessage());
        }
        return resultados;
    }

    // ================================================================
    // ============== REPORTE DE VENTAS ===============================
    // ================================================================
    
    /**
     * Obtiene ventas para reporte
     */
    public List<Venta> getReporteVentas(String fechaInicio, String fechaFin) {
        List<Venta> ventas = new ArrayList<>();
        String sql = "SELECT v.*, p.nombre || ' ' || p.apellido as paciente_nombre " +
                     "FROM ventas v " +
                     "LEFT JOIN pacientes p ON v.paciente_id = p.id " +
                     "WHERE v.fecha BETWEEN ? AND ? ORDER BY v.fecha DESC";
        
        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, fechaInicio);
            pstmt.setString(2, fechaFin + " 23:59:59");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Venta v = new Venta();
                v.setId(rs.getInt("id"));
                v.setPacienteId(rs.getInt("paciente_id"));
                v.setPacienteNombre(rs.getString("paciente_nombre"));
                v.setTipoComprobante(rs.getString("tipo_comprobante"));
                v.setMetodoPago(rs.getString("metodo_pago"));
                v.setSubtotal(rs.getDouble("subtotal"));
                v.setImpuesto(rs.getDouble("impuesto"));
                v.setTotal(rs.getDouble("total"));
                v.setEstado(rs.getString("estado"));
                v.setFecha(rs.getString("fecha"));
                ventas.add(v);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener ventas: " + e.getMessage());
        }
        return ventas;
    }
    
    /**
     * Obtiene ingresos por día para gráfica
     */
    public List<Object[]> getIngresosPorDia(String fechaInicio, String fechaFin) {
        List<Object[]> resultados = new ArrayList<>();
        String sql = "SELECT fecha, SUM(total) as total FROM ventas " +
                     "WHERE fecha BETWEEN ? AND ? AND estado IN ('PAGADA', 'ACTIVA') " +
                     "GROUP BY fecha ORDER BY fecha";
        
        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, fechaInicio);
            pstmt.setString(2, fechaFin + " 23:59:59");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String fecha = rs.getString("fecha");
                if (fecha != null && fecha.length() > 10) {
                    fecha = fecha.substring(0, 10);
                }
                resultados.add(new Object[]{fecha, rs.getDouble("total")});
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener ingresos por día: " + e.getMessage());
        }
        return resultados;
    }
    
    /**
     * Obtiene ingresos por método de pago
     */
    public List<Object[]> getIngresosPorMetodoPago(String fechaInicio, String fechaFin) {
        List<Object[]> resultados = new ArrayList<>();
        String sql = "SELECT metodo_pago, SUM(total) as total FROM ventas " +
                     "WHERE fecha BETWEEN ? AND ? AND estado IN ('PAGADA', 'ACTIVA') " +
                     "GROUP BY metodo_pago ORDER BY total DESC";
        
        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, fechaInicio);
            pstmt.setString(2, fechaFin + " 23:59:59");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                resultados.add(new Object[]{rs.getString("metodo_pago"), rs.getDouble("total")});
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener ingresos por método de pago: " + e.getMessage());
        }
        return resultados;
    }

    // ================================================================
    // ============== REPORTE DE SERVICIOS ============================
    // ================================================================
    
    /**
     * Obtiene servicios más solicitados
     */
    public List<Object[]> getServiciosMasSolicitados(String fechaInicio, String fechaFin) {
        List<Object[]> resultados = new ArrayList<>();
        String sql = "SELECT s.nombre, COUNT(*) as total FROM citas c " +
                     "JOIN servicios s ON c.servicio_id = s.id " +
                     "WHERE c.fecha BETWEEN ? AND ? " +
                     "GROUP BY s.nombre ORDER BY total DESC LIMIT 10";
        
        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, fechaInicio);
            pstmt.setString(2, fechaFin);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                resultados.add(new Object[]{rs.getString("nombre"), rs.getInt("total")});
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener servicios más solicitados: " + e.getMessage());
        }
        return resultados;
    }

    // ================================================================
    // ============== REPORTE DE INVENTARIO ===========================
    // ================================================================
    
    /**
     * Obtiene insumos con stock bajo
     */
    public List<Object[]> getInsumosStockBajo() {
        List<Object[]> resultados = new ArrayList<>();
        String sql = "SELECT nombre, codigo, stock, stock_minimo FROM insumos " +
                     "WHERE stock <= stock_minimo AND estado = 1 ORDER BY stock";
        
        try (Connection conn = ConexionSQLite.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                resultados.add(new Object[]{
                    rs.getString("nombre"),
                    rs.getString("codigo"),
                    rs.getInt("stock"),
                    rs.getInt("stock_minimo")
                });
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener insumos con stock bajo: " + e.getMessage());
        }
        return resultados;
    }
    
    /**
     * Obtiene resumen general del sistema
     */
    public String[] getResumenGeneral() {
        String[] resumen = new String[6];
        
        try (Connection conn = ConexionSQLite.conectar()) {
            // Total pacientes
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM pacientes WHERE estado = 1")) {
                resumen[0] = rs.next() ? String.valueOf(rs.getInt(1)) : "0";
            }
            
            // Total citas hoy
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM citas WHERE fecha = date('now')")) {
                resumen[1] = rs.next() ? String.valueOf(rs.getInt(1)) : "0";
            }
            
            // Total ventas hoy
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COALESCE(SUM(total), 0) FROM ventas WHERE fecha = date('now')")) {
                resumen[2] = rs.next() ? String.format("%.2f", rs.getDouble(1)) : "0.00";
            }
            
            // Total servicios
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM servicios WHERE estado = 1")) {
                resumen[3] = rs.next() ? String.valueOf(rs.getInt(1)) : "0";
            }
            
            // Total insumos
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM insumos WHERE estado = 1")) {
                resumen[4] = rs.next() ? String.valueOf(rs.getInt(1)) : "0";
            }
            
            // Insumos con stock bajo
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM insumos WHERE stock <= stock_minimo AND estado = 1")) {
                resumen[5] = rs.next() ? String.valueOf(rs.getInt(1)) : "0";
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener resumen general: " + e.getMessage());
        }
        
        return resumen;
    }
}