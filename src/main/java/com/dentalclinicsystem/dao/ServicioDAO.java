package com.dentalclinicsystem.dao;

import com.dentalclinicsystem.model.Servicio;
import com.dentalclinicsystem.config.ConexionSQLite;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServicioDAO {

    public boolean insertar(Servicio servicio) {
        String sql = "INSERT INTO servicios (nombre, categoria, subcategoria, duracion_minutos, " +
                     "precio, precio_particular, precio_aseguradora, codigo_procedimiento, " +
                     "requiere_consulta_previa, materiales_necesarios, estado) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, servicio.getNombre());
            pstmt.setString(2, servicio.getCategoria());
            pstmt.setString(3, servicio.getSubcategoria());
            pstmt.setInt(4, servicio.getDuracionMinutos());
            pstmt.setDouble(5, servicio.getPrecio());
            pstmt.setDouble(6, servicio.getPrecioParticular());
            pstmt.setDouble(7, servicio.getPrecioAseguradora());
            pstmt.setString(8, servicio.getCodigoProcedimiento());
            pstmt.setInt(9, servicio.getRequiereConsultaPrevia());
            pstmt.setString(10, servicio.getMaterialesNecesarios());
            pstmt.setInt(11, servicio.getEstado());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    servicio.setId(rs.getInt(1));
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("❌ Error al insertar servicio: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public Servicio obtenerPorId(int id) {
        String sql = "SELECT * FROM servicios WHERE id = ?";

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapearServicio(rs);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener servicio: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<Servicio> obtenerTodos() {
        List<Servicio> servicios = new ArrayList<>();
        String sql = "SELECT * FROM servicios ORDER BY nombre";

        try (Connection conn = ConexionSQLite.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                servicios.add(mapearServicio(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener servicios: " + e.getMessage());
            e.printStackTrace();
        }
        return servicios;
    }

    public List<Servicio> obtenerActivos() {
        List<Servicio> servicios = new ArrayList<>();
        String sql = "SELECT * FROM servicios WHERE estado = 1 ORDER BY nombre";

        try (Connection conn = ConexionSQLite.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                servicios.add(mapearServicio(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener servicios activos: " + e.getMessage());
            e.printStackTrace();
        }
        return servicios;
    }

    public List<Servicio> buscar(String texto) {
        List<Servicio> servicios = new ArrayList<>();
        String sql = "SELECT * FROM servicios WHERE nombre LIKE ? OR categoria LIKE ? OR subcategoria LIKE ? " +
                     "ORDER BY nombre";

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String busqueda = "%" + texto + "%";
            pstmt.setString(1, busqueda);
            pstmt.setString(2, busqueda);
            pstmt.setString(3, busqueda);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                servicios.add(mapearServicio(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al buscar servicios: " + e.getMessage());
            e.printStackTrace();
        }
        return servicios;
    }

    public List<Servicio> obtenerPorCategoria(String categoria) {
        List<Servicio> servicios = new ArrayList<>();
        String sql = "SELECT * FROM servicios WHERE categoria = ? AND estado = 1 ORDER BY nombre";

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, categoria);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                servicios.add(mapearServicio(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener servicios por categoría: " + e.getMessage());
            e.printStackTrace();
        }
        return servicios;
    }

    public boolean actualizar(Servicio servicio) {
        String sql = "UPDATE servicios SET nombre = ?, categoria = ?, subcategoria = ?, " +
                     "duracion_minutos = ?, precio = ?, precio_particular = ?, precio_aseguradora = ?, " +
                     "codigo_procedimiento = ?, requiere_consulta_previa = ?, materiales_necesarios = ?, " +
                     "estado = ? WHERE id = ?";

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, servicio.getNombre());
            pstmt.setString(2, servicio.getCategoria());
            pstmt.setString(3, servicio.getSubcategoria());
            pstmt.setInt(4, servicio.getDuracionMinutos());
            pstmt.setDouble(5, servicio.getPrecio());
            pstmt.setDouble(6, servicio.getPrecioParticular());
            pstmt.setDouble(7, servicio.getPrecioAseguradora());
            pstmt.setString(8, servicio.getCodigoProcedimiento());
            pstmt.setInt(9, servicio.getRequiereConsultaPrevia());
            pstmt.setString(10, servicio.getMaterialesNecesarios());
            pstmt.setInt(11, servicio.getEstado());
            pstmt.setInt(12, servicio.getId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar servicio: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM servicios WHERE id = ?";

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar servicio: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public int contarServicios() {
        String sql = "SELECT COUNT(*) FROM servicios WHERE estado = 1";

        try (Connection conn = ConexionSQLite.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al contar servicios: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    private Servicio mapearServicio(ResultSet rs) throws SQLException {
        Servicio servicio = new Servicio();
        servicio.setId(rs.getInt("id"));
        servicio.setNombre(rs.getString("nombre"));
        servicio.setCategoria(rs.getString("categoria"));
        servicio.setSubcategoria(rs.getString("subcategoria"));
        servicio.setDuracionMinutos(rs.getInt("duracion_minutos"));
        servicio.setPrecio(rs.getDouble("precio"));
        servicio.setPrecioParticular(rs.getDouble("precio_particular"));
        servicio.setPrecioAseguradora(rs.getDouble("precio_aseguradora"));
        servicio.setCodigoProcedimiento(rs.getString("codigo_procedimiento"));
        servicio.setRequiereConsultaPrevia(rs.getInt("requiere_consulta_previa"));
        servicio.setMaterialesNecesarios(rs.getString("materiales_necesarios"));
        servicio.setEstado(rs.getInt("estado"));
        return servicio;
    }
}