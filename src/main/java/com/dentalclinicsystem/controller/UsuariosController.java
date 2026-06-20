package com.dentalclinicsystem.controller;

import com.dentalclinicsystem.dao.AuditoriaDAO;
import com.dentalclinicsystem.dao.Session;
import com.dentalclinicsystem.dao.UsuarioDAO;
import com.dentalclinicsystem.model.Usuario;
import com.dentalclinicsystem.util.PasswordUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class UsuariosController {
    private UsuarioDAO usuarioDAO;
    private AuditoriaDAO auditoriaDAO;

    public UsuariosController() {
        this.usuarioDAO = new UsuarioDAO();
        this.auditoriaDAO = new AuditoriaDAO();
    }

    // ========== VALIDACIONES ==========
    public String validarUsuario(Usuario usuario, boolean esEdicion) {
        if (usuario == null) {
            return "Datos del usuario inválidos";
        }

        if (usuario.getNombre() == null || usuario.getNombre().trim().isEmpty()) {
            return "El nombre es obligatorio";
        }

        if (usuario.getUsuario() == null || usuario.getUsuario().trim().isEmpty()) {
            return "El nombre de usuario es obligatorio";
        }

        if (usuario.getUsuario().length() < 3) {
            return "El nombre de usuario debe tener al menos 3 caracteres";
        }

        if (!esEdicion) {
            if (usuario.getPassword() == null || usuario.getPassword().isEmpty()) {
                return "La contraseña es obligatoria";
            }
            if (usuario.getPassword().length() < 4) {
                return "La contraseña debe tener al menos 4 caracteres";
            }
        }

        if (usuario.getRol() == null || usuario.getRol().isEmpty()) {
            return "Seleccione un rol";
        }

        if (usuario.getEmail() != null && !usuario.getEmail().isEmpty()) {
            if (!validarEmail(usuario.getEmail())) {
                return "El email no es válido";
            }
        }

        // Verificar si el usuario ya existe
        if (!esEdicion) {
            if (usuarioDAO.existeUsuario(usuario.getUsuario())) {
                return "El nombre de usuario ya está en uso";
            }
        } else {
            // En edición, verificar si el usuario existe y es diferente al actual
            Usuario existente = usuarioDAO.obtenerPorUsuario(usuario.getUsuario());
            if (existente != null && existente.getId() != usuario.getId()) {
                return "El nombre de usuario ya está en uso";
            }
        }

        return null;
    }

    private boolean validarEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    // ========== CRUD ==========
    public boolean guardarUsuario(Usuario usuario, boolean esEdicion) {
        if (usuario == null) {
            return false;
        }

        String error = validarUsuario(usuario, esEdicion);
        if (error != null) {
            JOptionPane.showMessageDialog(null, error, "Error de validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Capitalizar nombre
        usuario.setNombre(capitalizar(usuario.getNombre()));

        boolean exito;
        if (esEdicion) {
            // En edición, no modificar la contraseña si está vacía
            if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
                usuario.setPassword(PasswordUtil.encriptarPassword(usuario.getPassword()));
            } else {
                // Mantener contraseña actual
                Usuario actual = usuarioDAO.obtenerPorId(usuario.getId());
                if (actual != null) {
                    usuario.setPassword(actual.getPassword());
                }
            }

            exito = usuarioDAO.actualizar(usuario);
            if (exito) {
                auditoriaDAO.registrar("UPDATE", "usuarios", usuario.getId());
                JOptionPane.showMessageDialog(null, "✅ Usuario actualizado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            usuario.setPassword(PasswordUtil.encriptarPassword(usuario.getPassword()));
            exito = usuarioDAO.insertar(usuario);
            if (exito) {
                auditoriaDAO.registrar("INSERT", "usuarios", usuario.getId());
                JOptionPane.showMessageDialog(null, "✅ Usuario registrado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
        }

        return exito;
    }

    public boolean eliminarUsuario(int id) {
        // No permitir eliminar al usuario actual
        if (Session.getInstance().getUsuarioActual() != null &&
            Session.getInstance().getUsuarioActual().getId() == id) {
            JOptionPane.showMessageDialog(null, "No puede eliminar su propio usuario", "Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        int confirm = JOptionPane.showConfirmDialog(null, 
            "¿Está seguro de eliminar este usuario?", 
            "Confirmar eliminación", 
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean exito = usuarioDAO.eliminar(id);
            if (exito) {
                auditoriaDAO.registrar("DELETE", "usuarios", id);
                JOptionPane.showMessageDialog(null, "✅ Usuario eliminado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
            return exito;
        }
        return false;
    }

    public Usuario buscarPorId(int id) {
        return usuarioDAO.obtenerPorId(id);
    }

    public Usuario buscarPorUsuario(String usuario) {
        return usuarioDAO.obtenerPorUsuario(usuario);
    }

    public List<Usuario> listarTodos() {
        return usuarioDAO.obtenerTodos();
    }

    public List<Usuario> listarActivos() {
        return usuarioDAO.obtenerActivos();
    }

    public List<Usuario> listarPorRol(String rol) {
        return usuarioDAO.obtenerPorRol(rol);
    }

    public List<Usuario> buscar(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return listarTodos();
        }
        return usuarioDAO.buscar(texto);
    }

    public int contarUsuarios() {
        return usuarioDAO.contarUsuarios();
    }

    public int contarPorRol(String rol) {
        return usuarioDAO.contarPorRol(rol);
    }

    // ========== MÉTODOS ESPECIALES ==========
    public boolean cambiarPassword(int id, String nuevaPassword, String confirmarPassword) {
        if (nuevaPassword == null || nuevaPassword.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Ingrese una nueva contraseña", "Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (nuevaPassword.length() < 4) {
            JOptionPane.showMessageDialog(null, "La contraseña debe tener al menos 4 caracteres", "Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (!nuevaPassword.equals(confirmarPassword)) {
            JOptionPane.showMessageDialog(null, "Las contraseñas no coinciden", "Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        boolean exito = usuarioDAO.actualizarPassword(id, PasswordUtil.encriptarPassword(nuevaPassword));
        if (exito) {
            auditoriaDAO.registrar("PASSWORD_CHANGE", "usuarios", id);
            JOptionPane.showMessageDialog(null, "✅ Contraseña actualizada correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }
        return exito;
    }

    public boolean bloquearUsuario(int id) {
        // No permitir bloquear al usuario actual
        if (Session.getInstance().getUsuarioActual() != null &&
            Session.getInstance().getUsuarioActual().getId() == id) {
            JOptionPane.showMessageDialog(null, "No puede bloquear su propio usuario", "Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        Usuario usuario = usuarioDAO.obtenerPorId(id);
        if (usuario == null) {
            return false;
        }

        usuario.setBloqueado(1);
        boolean exito = usuarioDAO.actualizar(usuario);
        if (exito) {
            auditoriaDAO.registrar("BLOQUEAR", "usuarios", id);
            JOptionPane.showMessageDialog(null, "🔒 Usuario bloqueado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }
        return exito;
    }

    public boolean desbloquearUsuario(int id) {
        Usuario usuario = usuarioDAO.obtenerPorId(id);
        if (usuario == null) {
            return false;
        }

        usuario.setBloqueado(0);
        usuario.setIntentos(0);
        boolean exito = usuarioDAO.actualizar(usuario);
        if (exito) {
            auditoriaDAO.registrar("DESBLOQUEAR", "usuarios", id);
            JOptionPane.showMessageDialog(null, "🔓 Usuario desbloqueado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }
        return exito;
    }

    public boolean activarUsuario(int id) {
        Usuario usuario = usuarioDAO.obtenerPorId(id);
        if (usuario == null) {
            return false;
        }

        usuario.setEstado(1);
        boolean exito = usuarioDAO.actualizar(usuario);
        if (exito) {
            auditoriaDAO.registrar("ACTIVAR", "usuarios", id);
            JOptionPane.showMessageDialog(null, "✅ Usuario activado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }
        return exito;
    }

    public boolean inactivarUsuario(int id) {
        // No permitir inactivar al usuario actual
        if (Session.getInstance().getUsuarioActual() != null &&
            Session.getInstance().getUsuarioActual().getId() == id) {
            JOptionPane.showMessageDialog(null, "No puede inactivar su propio usuario", "Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        Usuario usuario = usuarioDAO.obtenerPorId(id);
        if (usuario == null) {
            return false;
        }

        usuario.setEstado(0);
        boolean exito = usuarioDAO.actualizar(usuario);
        if (exito) {
            auditoriaDAO.registrar("INACTIVAR", "usuarios", id);
            JOptionPane.showMessageDialog(null, "⛔ Usuario inactivado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }
        return exito;
    }

    // ========== MÉTODOS PARA ODONTÓLOGOS (para citas) ==========
    public List<Usuario> listarOdontologos() {
        return usuarioDAO.obtenerPorRol(Usuario.ROL_DENTISTA);
    }

    public List<Usuario> listarOdontologosActivos() {
        return usuarioDAO.obtenerPorRol(Usuario.ROL_DENTISTA);
    }

    // ========== MÉTODOS PARA TABLA ==========
    public void cargarTabla(DefaultTableModel model, List<Usuario> usuarios) {
        model.setRowCount(0);
        
        if (usuarios == null || usuarios.isEmpty()) {
            return;
        }

        for (Usuario u : usuarios) {
            String estado = u.getEstado() == 1 ? "Activo" : "Inactivo";
            String bloqueado = u.getBloqueado() == 1 ? "🔒 Sí" : "✅ No";
            
            model.addRow(new Object[]{
                u.getId(),
                u.getNombre(),
                u.getUsuario(),
                u.getRolTexto(),
                u.getEmail() != null ? u.getEmail() : "",
                u.getTelefono() != null ? u.getTelefono() : "",
                estado,
                bloqueado,
                u.getUltimoAcceso() != null ? u.getUltimoAcceso() : "Nunca"
            });
        }
    }

    public String[] getRoles() {
        return new String[]{
            Usuario.ROL_ADMIN,
            Usuario.ROL_DENTISTA,
            Usuario.ROL_ASISTENTE,
            Usuario.ROL_RECEPCIONISTA
        };
    }

    public String[] getRolesTexto() {
        return new String[]{
            "Administrador",
            "Odontólogo",
            "Asistente",
            "Recepcionista"
        };
    }

    // ========== UTILIDADES ==========
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