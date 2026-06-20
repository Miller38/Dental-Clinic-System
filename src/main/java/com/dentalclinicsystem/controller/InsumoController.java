package com.dentalclinicsystem.controller;

import com.dentalclinicsystem.dao.AuditoriaDAO;
import com.dentalclinicsystem.dao.InsumoDAO;
import com.dentalclinicsystem.dao.Session;
import com.dentalclinicsystem.model.Insumo;
import com.dentalclinicsystem.model.Movimiento;
import com.dentalclinicsystem.model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class InsumoController {
    private InsumoDAO insumoDAO;
    private AuditoriaDAO auditoriaDAO;

    public InsumoController() {
        this.insumoDAO = new InsumoDAO();
        this.auditoriaDAO = new AuditoriaDAO();
    }

    // ========== VALIDACIONES ==========
    public String validarInsumo(Insumo insumo) {
        if (insumo == null) {
            return "Datos del insumo inválidos";
        }

        if (insumo.getNombre() == null || insumo.getNombre().trim().isEmpty()) {
            return "El nombre es obligatorio";
        }

        if (insumo.getCodigo() == null || insumo.getCodigo().trim().isEmpty()) {
            return "El código es obligatorio";
        }

        if (insumo.getCategoria() == null || insumo.getCategoria().trim().isEmpty()) {
            return "La categoría es obligatoria";
        }

        if (insumo.getStock() < 0) {
            return "El stock no puede ser negativo";
        }

        if (insumo.getStockMinimo() < 0) {
            return "El stock mínimo no puede ser negativo";
        }

        if (insumo.getPrecioVenta() < 0) {
            return "El precio de venta no puede ser negativo";
        }

        // Verificar código duplicado
        Insumo existente = insumoDAO.obtenerPorCodigo(insumo.getCodigo());
        if (existente != null && existente.getId() != insumo.getId()) {
            return "Ya existe un insumo con este código";
        }

        return null;
    }

    // ========== CRUD ==========
    public boolean guardarInsumo(Insumo insumo) {
        if (insumo == null) {
            return false;
        }

        String error = validarInsumo(insumo);
        if (error != null) {
            JOptionPane.showMessageDialog(null, error, "Error de validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        boolean exito;
        if (insumo.getId() > 0) {
            exito = insumoDAO.actualizar(insumo);
            if (exito) {
                auditoriaDAO.registrar("UPDATE", "insumos", insumo.getId());
                JOptionPane.showMessageDialog(null, "✅ Insumo actualizado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            exito = insumoDAO.insertar(insumo);
            if (exito) {
                auditoriaDAO.registrar("INSERT", "insumos", insumo.getId());
                JOptionPane.showMessageDialog(null, "✅ Insumo registrado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
        }

        return exito;
    }

    public boolean eliminarInsumo(int id) {
        int confirm = JOptionPane.showConfirmDialog(null, 
            "¿Está seguro de eliminar este insumo?", 
            "Confirmar eliminación", 
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean exito = insumoDAO.eliminar(id);
            if (exito) {
                auditoriaDAO.registrar("DELETE", "insumos", id);
                JOptionPane.showMessageDialog(null, "✅ Insumo eliminado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
            return exito;
        }
        return false;
    }

    public Insumo buscarPorId(int id) {
        return insumoDAO.obtenerPorId(id);
    }

    public Insumo buscarPorCodigo(String codigo) {
        return insumoDAO.obtenerPorCodigo(codigo);
    }

    public List<Insumo> listarTodos() {
        return insumoDAO.obtenerTodos();
    }

    public List<Insumo> listarActivos() {
        return insumoDAO.obtenerActivos();
    }

    public List<Insumo> listarStockBajo() {
        return insumoDAO.obtenerStockBajo();
    }

    public List<Insumo> buscar(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return listarTodos();
        }
        return insumoDAO.buscar(texto);
    }

    public int contarInsumos() {
        return insumoDAO.contarInsumos();
    }

    public int contarStockBajo() {
        return insumoDAO.contarStockBajo();
    }

    // ========== MOVIMIENTOS ==========
    public boolean registrarMovimiento(int insumoId, String tipo, int cantidad, String motivo) {
        Insumo insumo = insumoDAO.obtenerPorId(insumoId);
        if (insumo == null) {
            JOptionPane.showMessageDialog(null, "Insumo no encontrado", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Calcular nuevo stock
        int nuevoStock = insumo.getStock();
        if (tipo.equals(Movimiento.TIPO_ENTRADA)) {
            nuevoStock += cantidad;
        } else if (tipo.equals(Movimiento.TIPO_SALIDA) || tipo.equals(Movimiento.TIPO_VENCIDO)) {
            if (insumo.getStock() < cantidad) {
                JOptionPane.showMessageDialog(null, "Stock insuficiente. Disponible: " + insumo.getStock(), 
                    "Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            nuevoStock -= cantidad;
        }

        // Obtener usuario actual
        String usuarioActual = "SISTEMA";
        if (Session.getInstance().isSesionActiva()) {
            Usuario usuario = Session.getUsuarioActual();
            if (usuario != null) {
                usuarioActual = usuario.getUsuario();
            }
        }

        // Crear movimiento
        Movimiento movimiento = new Movimiento(insumoId, tipo, cantidad, motivo);
        movimiento.setUsuario(usuarioActual);

        // Guardar movimiento y actualizar stock
        boolean exito = insumoDAO.insertarMovimiento(movimiento);
        if (exito) {
            insumoDAO.actualizarStock(insumoId, nuevoStock);
            auditoriaDAO.registrar("MOVIMIENTO", "movimientos", movimiento.getId());
            JOptionPane.showMessageDialog(null, 
                "✅ Movimiento registrado correctamente\nNuevo stock: " + nuevoStock, 
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }

        return exito;
    }

    public List<Movimiento> listarMovimientosPorInsumo(int insumoId) {
        return insumoDAO.obtenerMovimientosPorInsumo(insumoId);
    }

    // ========== TABLA ==========
    public void cargarTabla(DefaultTableModel model, List<Insumo> insumos) {
        model.setRowCount(0);
        
        if (insumos == null || insumos.isEmpty()) {
            return;
        }

        for (Insumo i : insumos) {
            String estadoStock = i.isSinStock() ? "🔴 Sin stock" : 
                                 i.isStockBajo() ? "🟡 Stock bajo" : "🟢 Normal";
            
            model.addRow(new Object[]{
                i.getId(),
                i.getCodigo(),
                i.getNombre(),
                i.getCategoria(),
                i.getStock(),
                i.getStockMinimo(),
                estadoStock,
                i.getPrecioVentaFormateado(),
                i.getEstadoTexto()
            });
        }
    }

    public void cargarMovimientos(DefaultTableModel model, List<Movimiento> movimientos) {
        model.setRowCount(0);
        
        if (movimientos == null || movimientos.isEmpty()) {
            return;
        }

        for (Movimiento m : movimientos) {
            model.addRow(new Object[]{
                m.getId(),
                m.getTipoTexto(),
                m.getCantidad(),
                m.getMotivo() != null ? m.getMotivo() : "",
                m.getUsuario() != null ? m.getUsuario() : "",
                m.getFecha()
            });
        }
    }

    public String[] getCategorias() {
        return new String[]{
            "Instrumental",
            "Materiales Dentales",
            "Medicamentos",
            "PPE",
            "Otros"
        };
    }

    public String[] getTiposInsumo() {
        return new String[]{
            "CONSUMIBLE",
            "NO_CONSUMIBLE",
            "CADUCO",
            "MEDICAMENTO"
        };
    }

    public String[] getTiposMovimiento() {
        return new String[]{
            Movimiento.TIPO_ENTRADA,
            Movimiento.TIPO_SALIDA,
            Movimiento.TIPO_AJUSTE,
            Movimiento.TIPO_VENCIDO,
            Movimiento.TIPO_DEVOLUCION
        };
    }
}