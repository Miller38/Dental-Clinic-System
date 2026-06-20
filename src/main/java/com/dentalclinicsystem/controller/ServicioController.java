package com.dentalclinicsystem.controller;

import com.dentalclinicsystem.dao.AuditoriaDAO;
import com.dentalclinicsystem.dao.ServicioDAO;
import com.dentalclinicsystem.model.Servicio;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class ServicioController {
    private ServicioDAO servicioDAO;
    private AuditoriaDAO auditoriaDAO;

    public ServicioController() {
        this.servicioDAO = new ServicioDAO();
        this.auditoriaDAO = new AuditoriaDAO();
    }

    // ========== VALIDACIONES ==========
    public String validarServicio(Servicio servicio) {
        if (servicio == null) {
            return "Datos del servicio inválidos";
        }

        if (servicio.getNombre() == null || servicio.getNombre().trim().isEmpty()) {
            return "El nombre es obligatorio";
        }

        if (servicio.getCategoria() == null || servicio.getCategoria().trim().isEmpty()) {
            return "La categoría es obligatoria";
        }

        if (servicio.getDuracionMinutos() < 5 || servicio.getDuracionMinutos() > 480) {
            return "La duración debe estar entre 5 y 480 minutos";
        }

        if (servicio.getPrecio() < 0) {
            return "El precio no puede ser negativo";
        }

        return null;
    }

    // ========== CRUD ==========
    public boolean guardarServicio(Servicio servicio) {
        if (servicio == null) {
            return false;
        }

        String error = validarServicio(servicio);
        if (error != null) {
            JOptionPane.showMessageDialog(null, error, "Error de validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        boolean exito;
        if (servicio.getId() > 0) {
            exito = servicioDAO.actualizar(servicio);
            if (exito) {
                auditoriaDAO.registrar("UPDATE", "servicios", servicio.getId());
                JOptionPane.showMessageDialog(null, "✅ Servicio actualizado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            exito = servicioDAO.insertar(servicio);
            if (exito) {
                auditoriaDAO.registrar("INSERT", "servicios", servicio.getId());
                JOptionPane.showMessageDialog(null, "✅ Servicio registrado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
        }

        return exito;
    }

    public boolean eliminarServicio(int id) {
        int confirm = JOptionPane.showConfirmDialog(null, 
            "¿Está seguro de eliminar este servicio?", 
            "Confirmar eliminación", 
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean exito = servicioDAO.eliminar(id);
            if (exito) {
                auditoriaDAO.registrar("DELETE", "servicios", id);
                JOptionPane.showMessageDialog(null, "✅ Servicio eliminado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
            return exito;
        }
        return false;
    }

    public Servicio buscarPorId(int id) {
        return servicioDAO.obtenerPorId(id);
    }

    /**
     * Busca un servicio por su nombre
     * @param nombre Nombre del servicio a buscar
     * @return Servicio encontrado o null si no existe
     */
    public Servicio buscarPorNombre(String nombre) {
        if (nombre == null || nombre.isEmpty()) {
            return null;
        }
        List<Servicio> servicios = servicioDAO.obtenerActivos();
        if (servicios != null) {
            for (Servicio s : servicios) {
                if (s.getNombre().equals(nombre)) {
                    return s;
                }
            }
        }
        return null;
    }

    public List<Servicio> listarTodos() {
        return servicioDAO.obtenerTodos();
    }

    public List<Servicio> listarActivos() {
        return servicioDAO.obtenerActivos();
    }

    public List<Servicio> buscar(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return listarTodos();
        }
        return servicioDAO.buscar(texto);
    }

    public List<Servicio> listarPorCategoria(String categoria) {
        return servicioDAO.obtenerPorCategoria(categoria);
    }

    public int contarServicios() {
        return servicioDAO.contarServicios();
    }

    public void cargarTabla(DefaultTableModel model, List<Servicio> servicios) {
        model.setRowCount(0);
        
        if (servicios == null || servicios.isEmpty()) {
            return;
        }

        for (Servicio s : servicios) {
            model.addRow(new Object[]{
                s.getId(),
                s.getNombre(),
                s.getCategoria(),
                s.getSubcategoria() != null ? s.getSubcategoria() : "",
                s.getDuracionTexto(),
                s.getPrecioFormateado(),
                s.getEstadoTexto(),
                s.getCodigoProcedimiento() != null ? s.getCodigoProcedimiento() : ""
            });
        }
    }

    public String[] getCategorias() {
        return new String[]{
            "Consulta",
            "Diagnóstico",
            "Tratamiento",
            "Cirugía",
            "Estética",
            "Prótesis",
            "Endodoncia",
            "Periodoncia",
            "Odontopediatría",
            "Urgencia"
        };
    }
}