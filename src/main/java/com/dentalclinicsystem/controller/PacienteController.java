package com.dentalclinicsystem.controller;

import com.dentalclinicsystem.dao.AuditoriaDAO;
import com.dentalclinicsystem.dao.PacienteDAO;
import com.dentalclinicsystem.model.Paciente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class PacienteController {
    private PacienteDAO pacienteDAO;
    private AuditoriaDAO auditoriaDAO;

    public PacienteController() {
        this.pacienteDAO = new PacienteDAO();
        this.auditoriaDAO = new AuditoriaDAO();
    }

    public String validarPaciente(Paciente paciente) {
        if (paciente == null) {
            return "Datos del paciente inválidos";
        }
        
        if (paciente.getNombre() == null || paciente.getNombre().trim().isEmpty()) {
            return "El nombre es obligatorio";
        }
        if (paciente.getApellido() == null || paciente.getApellido().trim().isEmpty()) {
            return "El apellido es obligatorio";
        }
        if (paciente.getNumeroDocumento() == null || paciente.getNumeroDocumento().trim().isEmpty()) {
            return "El número de documento es obligatorio";
        }
        if (paciente.getTelefono() == null || paciente.getTelefono().trim().isEmpty()) {
            return "El teléfono es obligatorio";
        }
        
        String documento = paciente.getNumeroDocumento().trim();
        if (!documento.matches("\\d+")) {
            return "El documento solo debe contener números";
        }
        if (documento.length() < 7 || documento.length() > 15) {
            return "El documento debe tener entre 7 y 15 dígitos";
        }
        
        String telefono = paciente.getTelefono().trim();
        if (!telefono.matches("\\d+")) {
            return "El teléfono solo debe contener números";
        }
        if (telefono.length() < 7 || telefono.length() > 10) {
            return "El teléfono debe tener entre 7 y 10 dígitos";
        }
        
        if (paciente.getEmail() != null && !paciente.getEmail().trim().isEmpty()) {
            if (!validarEmail(paciente.getEmail())) {
                return "El email no es válido";
            }
        }
        
        if (paciente.getEdad() < 0 || paciente.getEdad() > 150) {
            return "La edad debe estar entre 0 y 150 años";
        }
        
        Paciente existente = pacienteDAO.obtenerPorDocumento(documento);
        if (existente != null && existente.getId() != paciente.getId()) {
            return "Ya existe un paciente con este número de documento";
        }
        
        return null;
    }

    public boolean guardarPaciente(Paciente paciente) {
        if (paciente == null) {
            return false;
        }

        String error = validarPaciente(paciente);
        if (error != null) {
            JOptionPane.showMessageDialog(null, error, "Error de validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        paciente.setNombre(capitalizar(paciente.getNombre()));
        paciente.setApellido(capitalizar(paciente.getApellido()));

        boolean exito;
        if (paciente.getId() > 0) {
            exito = pacienteDAO.actualizar(paciente);
            if (exito) {
                auditoriaDAO.registrar("UPDATE", "pacientes", paciente.getId());
                JOptionPane.showMessageDialog(null, "Paciente actualizado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            exito = pacienteDAO.insertar(paciente);
            if (exito) {
                auditoriaDAO.registrar("INSERT", "pacientes", paciente.getId());
                JOptionPane.showMessageDialog(null, "Paciente registrado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
        }

        return exito;
    }

    public boolean eliminarPaciente(int id) {
        int confirm = JOptionPane.showConfirmDialog(null, 
            "¿Está seguro de eliminar este paciente?", 
            "Confirmar eliminación", 
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean exito = pacienteDAO.eliminar(id);
            if (exito) {
                auditoriaDAO.registrar("DELETE", "pacientes", id);
                JOptionPane.showMessageDialog(null, "Paciente eliminado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
            return exito;
        }
        return false;
    }

    public Paciente buscarPorId(int id) {
        return pacienteDAO.obtenerPorId(id);
    }

    public Paciente buscarPorDocumento(String documento) {
        return pacienteDAO.obtenerPorDocumento(documento);
    }

    public List<Paciente> listarTodos() {
        return pacienteDAO.obtenerTodos();
    }

    public List<Paciente> buscar(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return listarTodos();
        }
        return pacienteDAO.buscar(texto);
    }

    public int contarPacientes() {
        return pacienteDAO.contarPacientes();
    }

    public void cargarTabla(DefaultTableModel model, List<Paciente> pacientes) {
        model.setRowCount(0);
        
        if (pacientes == null || pacientes.isEmpty()) {
            return;
        }

        for (Paciente p : pacientes) {
            model.addRow(new Object[]{
                p.getId(),
                p.getNombreCompleto(),
                p.getNumeroDocumento(),
                p.getTelefono(),
                p.getEmail() != null ? p.getEmail() : "",
                p.getGenero() != null ? p.getGenero() : "",
                p.getEdad() > 0 ? p.getEdad() : ""
            });
        }
    }

    private boolean validarEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    private String capitalizar(String texto) {
        if (texto == null || texto.isEmpty()) return texto;
        texto = texto.toLowerCase().trim();
        String[] palabras = texto.split(" ");
        StringBuilder result = new StringBuilder();
        for (String palabra : palabras) {
            if (!palabra.isEmpty()) {
                result.append(Character.toUpperCase(palabra.charAt(0)))
                      .append(palabra.substring(1))
                      .append(" ");
            }
        }
        return result.toString().trim();
    }
}