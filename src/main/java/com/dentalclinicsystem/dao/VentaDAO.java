package com.dentalclinicsystem.dao;

import com.dentalclinicsystem.config.ConexionSQLite;
import com.dentalclinicsystem.model.DetalleVenta;
import com.dentalclinicsystem.model.Venta;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VentaDAO {

    // ========== INSERT ==========
    public boolean insertar(Venta venta) {
    // 🔥 CORREGIDO: Usar fecha actual si no está establecida
    if (venta.getFecha() == null || venta.getFecha().isEmpty()) {
        venta.setFecha(LocalDate.now().toString());
        System.out.println("📅 Fecha no establecida, usando: " + venta.getFecha());
    }
    
    String sql = "INSERT INTO ventas (paciente_id, usuario, odontologo_id, tipo_comprobante, " +
                 "serie, numero_comprobante, subtotal, impuesto, descuento, total, " +
                 "metodo_pago, seguro_nombre, seguro_cobertura, estado, fecha, notas) " +  // 🔥 AÑADIR fecha
                 "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {
        conn = ConexionSQLite.conectar();
        conn.setAutoCommit(false);
        
        pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        pstmt.setInt(1, venta.getPacienteId());
        pstmt.setString(2, venta.getUsuario());
        
        if (venta.getOdontologoId() > 0) {
            pstmt.setInt(3, venta.getOdontologoId());
        } else {
            pstmt.setNull(3, java.sql.Types.INTEGER);
        }
        
        pstmt.setString(4, venta.getTipoComprobante());
        pstmt.setString(5, venta.getSerie());
        pstmt.setString(6, venta.getNumeroComprobante());
        pstmt.setDouble(7, venta.getSubtotal());
        pstmt.setDouble(8, venta.getImpuesto());
        pstmt.setDouble(9, venta.getDescuento());
        pstmt.setDouble(10, venta.getTotal());
        pstmt.setString(11, venta.getMetodoPago());
        pstmt.setString(12, venta.getSeguroNombre());
        pstmt.setDouble(13, venta.getSeguroCobertura());
        pstmt.setString(14, venta.getEstado());
        
        // 🔥 FECHA - Asegurar formato correcto
        String fecha = venta.getFecha();
        if (fecha == null || fecha.isEmpty()) {
            fecha = LocalDate.now().toString();
        }
        pstmt.setString(15, fecha);
        
        pstmt.setString(16, venta.getNotas());

        int affectedRows = pstmt.executeUpdate();
        
        if (affectedRows == 0) {
            conn.rollback();
            return false;
        }

        rs = pstmt.getGeneratedKeys();
        if (rs.next()) {
            venta.setId(rs.getInt(1));
        }

        // Insertar detalles
        for (DetalleVenta detalle : venta.getDetalles()) {
            if (!insertarDetalle(conn, detalle, venta.getId())) {
                conn.rollback();
                return false;
            }
        }

        conn.commit();
        System.out.println("✅ Venta insertada con ID: " + venta.getId() + " | Fecha: " + fecha);
        return true;

    } catch (SQLException e) {
        System.err.println("❌ Error al insertar venta: " + e.getMessage());
        try {
            if (conn != null) conn.rollback();
        } catch (SQLException ex) {
            System.err.println("❌ Error al hacer rollback: " + ex.getMessage());
        }
        return false;
    } finally {
        try {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al cerrar recursos: " + e.getMessage());
        }
    }
}

    private boolean insertarDetalle(Connection conn, DetalleVenta detalle, int ventaId) throws SQLException {
        String sql = "INSERT INTO detalle_ventas (venta_id, tipo_item, item_id, nombre, cantidad, precio_unitario, subtotal) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, ventaId);
            pstmt.setString(2, detalle.getTipoItem());
            pstmt.setInt(3, detalle.getItemId());
            pstmt.setString(4, detalle.getNombre());
            pstmt.setInt(5, detalle.getCantidad());
            pstmt.setDouble(6, detalle.getPrecioUnitario());
            pstmt.setDouble(7, detalle.getSubtotal());

            return pstmt.executeUpdate() > 0;
        }
    }

    // ========== SELECT ==========
    public Venta obtenerPorId(int id) {
        String sql = "SELECT v.*, p.nombre || ' ' || p.apellido as paciente_nombre, u.nombre as odontologo_nombre " +
                     "FROM ventas v " +
                     "LEFT JOIN pacientes p ON v.paciente_id = p.id " +
                     "LEFT JOIN usuarios u ON v.odontologo_id = u.id " +
                     "WHERE v.id = ?";

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Venta venta = mapearVenta(rs);
                venta.setDetalles(obtenerDetalles(conn, id));
                return venta;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener venta: " + e.getMessage());
        }
        return null;
    }

    // 🔥 MÉTODO PRINCIPAL - CORREGIDO DEFINITIVAMENTE
    public List<Venta> obtenerPorRango(String fechaInicio, String fechaFin) {
        List<Venta> ventas = new ArrayList<>();
        
        // 🔥 USAR LA MISMA CONSULTA QUE FUNCIONA EN SQL
        String sql = "SELECT v.*, " +
                     "p.nombre || ' ' || p.apellido as paciente_nombre " +
                     "FROM ventas v " +
                     "LEFT JOIN pacientes p ON v.paciente_id = p.id " +
                     "WHERE substr(v.fecha, 1, 10) BETWEEN ? AND ? " +
                     "ORDER BY v.fecha DESC";

        System.out.println("🔍 ===== CONSULTA DE VENTAS =====");
        System.out.println("🔍 Fecha Inicio: " + fechaInicio);
        System.out.println("🔍 Fecha Fin: " + fechaFin);

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, fechaInicio);
            pstmt.setString(2, fechaFin);
            ResultSet rs = pstmt.executeQuery();

            int contador = 0;
            while (rs.next()) {
                contador++;
                Venta venta = mapearVenta(rs);
                try {
                    venta.setDetalles(obtenerDetalles(conn, venta.getId()));
                } catch (SQLException e) {
                    // Si no hay detalles, continuar
                }
                ventas.add(venta);
                System.out.println("   ✅ Venta #" + contador + ": ID=" + venta.getId() + 
                                   " | Fecha=" + venta.getFecha());
            }

            System.out.println("📋 Total ventas encontradas: " + contador);

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener ventas: " + e.getMessage());
            e.printStackTrace();
        }
        
        return ventas;
    }

    public List<Venta> obtenerTodos() {
        List<Venta> ventas = new ArrayList<>();
        String sql = "SELECT v.*, p.nombre || ' ' || p.apellido as paciente_nombre, u.nombre as odontologo_nombre " +
                     "FROM ventas v " +
                     "LEFT JOIN pacientes p ON v.paciente_id = p.id " +
                     "LEFT JOIN usuarios u ON v.odontologo_id = u.id " +
                     "ORDER BY v.fecha DESC";

        try (Connection conn = ConexionSQLite.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Venta venta = mapearVenta(rs);
                venta.setDetalles(obtenerDetalles(conn, venta.getId()));
                ventas.add(venta);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener ventas: " + e.getMessage());
        }
        return ventas;
    }

    public List<Venta> obtenerPorPaciente(int pacienteId) {
        List<Venta> ventas = new ArrayList<>();
        String sql = "SELECT v.*, p.nombre || ' ' || p.apellido as paciente_nombre, u.nombre as odontologo_nombre " +
                     "FROM ventas v " +
                     "LEFT JOIN pacientes p ON v.paciente_id = p.id " +
                     "LEFT JOIN usuarios u ON v.odontologo_id = u.id " +
                     "WHERE v.paciente_id = ? ORDER BY v.fecha DESC";

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, pacienteId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Venta venta = mapearVenta(rs);
                venta.setDetalles(obtenerDetalles(conn, venta.getId()));
                ventas.add(venta);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener ventas: " + e.getMessage());
        }
        return ventas;
    }

    public List<DetalleVenta> obtenerDetalles(Connection conn, int ventaId) throws SQLException {
        List<DetalleVenta> detalles = new ArrayList<>();
        String sql = "SELECT * FROM detalle_ventas WHERE venta_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, ventaId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                DetalleVenta detalle = new DetalleVenta();
                detalle.setId(rs.getInt("id"));
                detalle.setVentaId(rs.getInt("venta_id"));
                detalle.setTipoItem(rs.getString("tipo_item"));
                detalle.setItemId(rs.getInt("item_id"));
                detalle.setNombre(rs.getString("nombre"));
                detalle.setCantidad(rs.getInt("cantidad"));
                detalle.setPrecioUnitario(rs.getDouble("precio_unitario"));
                detalle.setSubtotal(rs.getDouble("subtotal"));
                detalles.add(detalle);
            }
        }
        return detalles;
    }

    // ========== UPDATE ==========
    public boolean actualizarEstado(int id, String estado) {
        String sql = "UPDATE ventas SET estado = ? WHERE id = ?";

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, estado);
            pstmt.setInt(2, id);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar estado: " + e.getMessage());
            return false;
        }
    }

    // ========== DELETE ==========
    public boolean eliminar(int id) {
        String sql = "DELETE FROM ventas WHERE id = ?";

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar venta: " + e.getMessage());
            return false;
        }
    }

   // ========== REPORTES ==========
    public double obtenerTotalPorRango(String fechaInicio, String fechaFin) {
        // 🔥 CORREGIDO: Incluir todas las ventas no anuladas
        String sql = "SELECT COALESCE(SUM(total), 0) as total FROM ventas " +
                     "WHERE substr(fecha, 1, 10) BETWEEN ? AND ? AND estado != ?";

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, fechaInicio);
            pstmt.setString(2, fechaFin);
            pstmt.setString(3, Venta.ESTADO_ANULADA);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("total");
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener total: " + e.getMessage());
        }
        return 0.0;
    }

    public int contarVentasPorRango(String fechaInicio, String fechaFin) {
        // 🔥 CORREGIDO: Incluir todas las ventas no anuladas
        String sql = "SELECT COUNT(*) as total FROM ventas " +
                     "WHERE substr(fecha, 1, 10) BETWEEN ? AND ? AND estado != ?";

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, fechaInicio);
            pstmt.setString(2, fechaFin);
            pstmt.setString(3, Venta.ESTADO_ANULADA);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al contar ventas: " + e.getMessage());
        }
        return 0;
    }

    // ========== MAPEAR ==========
    private Venta mapearVenta(ResultSet rs) throws SQLException {
        Venta venta = new Venta();
        venta.setId(rs.getInt("id"));
        venta.setPacienteId(rs.getInt("paciente_id"));
        venta.setUsuario(rs.getString("usuario"));
        venta.setOdontologoId(rs.getInt("odontologo_id"));
        venta.setTipoComprobante(rs.getString("tipo_comprobante"));
        venta.setSerie(rs.getString("serie"));
        venta.setNumeroComprobante(rs.getString("numero_comprobante"));
        venta.setSubtotal(rs.getDouble("subtotal"));
        venta.setImpuesto(rs.getDouble("impuesto"));
        venta.setDescuento(rs.getDouble("descuento"));
        venta.setTotal(rs.getDouble("total"));
        venta.setMetodoPago(rs.getString("metodo_pago"));
        venta.setSeguroNombre(rs.getString("seguro_nombre"));
        venta.setSeguroCobertura(rs.getDouble("seguro_cobertura"));
        venta.setEstado(rs.getString("estado"));
        
        String fecha = rs.getString("fecha");
        if (fecha != null && fecha.length() >= 10) {
            venta.setFecha(fecha.substring(0, 10));
        } else {
            venta.setFecha(fecha);
        }
        
        venta.setNotas(rs.getString("notas"));
        
        try {
            venta.setPacienteNombre(rs.getString("paciente_nombre"));
            venta.setOdontologoNombre(rs.getString("odontologo_nombre"));
        } catch (SQLException e) {
            // Columnas no existen, ignorar
        }
        
        return venta;
    }
}