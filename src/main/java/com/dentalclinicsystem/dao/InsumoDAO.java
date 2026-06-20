package com.dentalclinicsystem.dao;

import com.dentalclinicsystem.config.ConexionSQLite;
import com.dentalclinicsystem.model.Insumo;
import com.dentalclinicsystem.model.Movimiento;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InsumoDAO {

    // ========== INSERT ==========
    public boolean insertar(Insumo insumo) {
        String sql = "INSERT INTO insumos (nombre, codigo, categoria, tipo_insumo, presentacion, " +
                     "stock, stock_minimo, stock_maximo, ubicacion, precio_compra, precio_venta, " +
                     "fecha_compra, fecha_vencimiento, lote, proveedor, estado) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, insumo.getNombre());
            pstmt.setString(2, insumo.getCodigo());
            pstmt.setString(3, insumo.getCategoria());
            pstmt.setString(4, insumo.getTipoInsumo());
            pstmt.setString(5, insumo.getPresentacion());
            pstmt.setInt(6, insumo.getStock());
            pstmt.setInt(7, insumo.getStockMinimo());
            pstmt.setInt(8, insumo.getStockMaximo());
            pstmt.setString(9, insumo.getUbicacion());
            pstmt.setDouble(10, insumo.getPrecioCompra());
            pstmt.setDouble(11, insumo.getPrecioVenta());
            pstmt.setString(12, insumo.getFechaCompra());
            pstmt.setString(13, insumo.getFechaVencimiento());
            pstmt.setString(14, insumo.getLote());
            pstmt.setString(15, insumo.getProveedor());
            pstmt.setInt(16, insumo.getEstado());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    insumo.setId(rs.getInt(1));
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("❌ Error al insertar insumo: " + e.getMessage());
            return false;
        }
    }

    // ========== SELECT ==========
    public Insumo obtenerPorId(int id) {
        String sql = "SELECT * FROM insumos WHERE id = ?";

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapearInsumo(rs);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener insumo: " + e.getMessage());
        }
        return null;
    }

    public Insumo obtenerPorCodigo(String codigo) {
        String sql = "SELECT * FROM insumos WHERE codigo = ?";

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, codigo);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapearInsumo(rs);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener insumo: " + e.getMessage());
        }
        return null;
    }

    public List<Insumo> obtenerTodos() {
        List<Insumo> insumos = new ArrayList<>();
        String sql = "SELECT * FROM insumos ORDER BY nombre";

        try (Connection conn = ConexionSQLite.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                insumos.add(mapearInsumo(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener insumos: " + e.getMessage());
        }
        return insumos;
    }

    public List<Insumo> obtenerActivos() {
        List<Insumo> insumos = new ArrayList<>();
        String sql = "SELECT * FROM insumos WHERE estado = 1 ORDER BY nombre";

        try (Connection conn = ConexionSQLite.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                insumos.add(mapearInsumo(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener insumos activos: " + e.getMessage());
        }
        return insumos;
    }

    public List<Insumo> obtenerStockBajo() {
        List<Insumo> insumos = new ArrayList<>();
        String sql = "SELECT * FROM insumos WHERE stock <= stock_minimo AND estado = 1 ORDER BY stock";

        try (Connection conn = ConexionSQLite.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                insumos.add(mapearInsumo(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener insumos con stock bajo: " + e.getMessage());
        }
        return insumos;
    }

    public List<Insumo> buscar(String texto) {
        List<Insumo> insumos = new ArrayList<>();
        String sql = "SELECT * FROM insumos WHERE nombre LIKE ? OR codigo LIKE ? OR categoria LIKE ? " +
                     "ORDER BY nombre";

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String busqueda = "%" + texto + "%";
            pstmt.setString(1, busqueda);
            pstmt.setString(2, busqueda);
            pstmt.setString(3, busqueda);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                insumos.add(mapearInsumo(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al buscar insumos: " + e.getMessage());
        }
        return insumos;
    }

    // ========== UPDATE ==========
    public boolean actualizar(Insumo insumo) {
        String sql = "UPDATE insumos SET nombre = ?, codigo = ?, categoria = ?, tipo_insumo = ?, " +
                     "presentacion = ?, stock = ?, stock_minimo = ?, stock_maximo = ?, ubicacion = ?, " +
                     "precio_compra = ?, precio_venta = ?, fecha_compra = ?, fecha_vencimiento = ?, " +
                     "lote = ?, proveedor = ?, estado = ? WHERE id = ?";

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, insumo.getNombre());
            pstmt.setString(2, insumo.getCodigo());
            pstmt.setString(3, insumo.getCategoria());
            pstmt.setString(4, insumo.getTipoInsumo());
            pstmt.setString(5, insumo.getPresentacion());
            pstmt.setInt(6, insumo.getStock());
            pstmt.setInt(7, insumo.getStockMinimo());
            pstmt.setInt(8, insumo.getStockMaximo());
            pstmt.setString(9, insumo.getUbicacion());
            pstmt.setDouble(10, insumo.getPrecioCompra());
            pstmt.setDouble(11, insumo.getPrecioVenta());
            pstmt.setString(12, insumo.getFechaCompra());
            pstmt.setString(13, insumo.getFechaVencimiento());
            pstmt.setString(14, insumo.getLote());
            pstmt.setString(15, insumo.getProveedor());
            pstmt.setInt(16, insumo.getEstado());
            pstmt.setInt(17, insumo.getId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar insumo: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarStock(int id, int nuevoStock) {
        String sql = "UPDATE insumos SET stock = ? WHERE id = ?";

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, nuevoStock);
            pstmt.setInt(2, id);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar stock: " + e.getMessage());
            return false;
        }
    }

    // ========== DELETE ==========
    public boolean eliminar(int id) {
        String sql = "DELETE FROM insumos WHERE id = ?";

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar insumo: " + e.getMessage());
            return false;
        }
    }

    // ========== CONTAR ==========
    public int contarInsumos() {
        String sql = "SELECT COUNT(*) FROM insumos WHERE estado = 1";

        try (Connection conn = ConexionSQLite.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al contar insumos: " + e.getMessage());
        }
        return 0;
    }

    public int contarStockBajo() {
        String sql = "SELECT COUNT(*) FROM insumos WHERE stock <= stock_minimo AND estado = 1";

        try (Connection conn = ConexionSQLite.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al contar insumos con stock bajo: " + e.getMessage());
        }
        return 0;
    }

    // ========== MOVIMIENTOS ==========
    public boolean insertarMovimiento(Movimiento movimiento) {
        String sql = "INSERT INTO movimientos (insumo_id, tipo, cantidad, motivo, usuario) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, movimiento.getInsumoId());
            pstmt.setString(2, movimiento.getTipo());
            pstmt.setInt(3, movimiento.getCantidad());
            pstmt.setString(4, movimiento.getMotivo());
            pstmt.setString(5, movimiento.getUsuario());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    movimiento.setId(rs.getInt(1));
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("❌ Error al insertar movimiento: " + e.getMessage());
            return false;
        }
    }

    public List<Movimiento> obtenerMovimientosPorInsumo(int insumoId) {
        List<Movimiento> movimientos = new ArrayList<>();
        String sql = "SELECT * FROM movimientos WHERE insumo_id = ? ORDER BY fecha DESC";

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, insumoId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Movimiento m = new Movimiento();
                m.setId(rs.getInt("id"));
                m.setInsumoId(rs.getInt("insumo_id"));
                m.setTipo(rs.getString("tipo"));
                m.setCantidad(rs.getInt("cantidad"));
                m.setMotivo(rs.getString("motivo"));
                m.setUsuario(rs.getString("usuario"));
                m.setFecha(rs.getString("fecha"));
                movimientos.add(m);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener movimientos: " + e.getMessage());
        }
        return movimientos;
    }

    // ========== MAPEAR ==========
    private Insumo mapearInsumo(ResultSet rs) throws SQLException {
        Insumo insumo = new Insumo();
        insumo.setId(rs.getInt("id"));
        insumo.setNombre(rs.getString("nombre"));
        insumo.setCodigo(rs.getString("codigo"));
        insumo.setCategoria(rs.getString("categoria"));
        insumo.setTipoInsumo(rs.getString("tipo_insumo"));
        insumo.setPresentacion(rs.getString("presentacion"));
        insumo.setStock(rs.getInt("stock"));
        insumo.setStockMinimo(rs.getInt("stock_minimo"));
        insumo.setStockMaximo(rs.getInt("stock_maximo"));
        insumo.setUbicacion(rs.getString("ubicacion"));
        insumo.setPrecioCompra(rs.getDouble("precio_compra"));
        insumo.setPrecioVenta(rs.getDouble("precio_venta"));
        insumo.setFechaCompra(rs.getString("fecha_compra"));
        insumo.setFechaVencimiento(rs.getString("fecha_vencimiento"));
        insumo.setLote(rs.getString("lote"));
        insumo.setProveedor(rs.getString("proveedor"));
        insumo.setEstado(rs.getInt("estado"));
        return insumo;
    }
}