package com.dentalclinicsystem.view;

import com.dentalclinicsystem.controller.UsuariosController;
import com.dentalclinicsystem.model.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DialogUsuario extends JDialog {
    
    private UsuariosController controller;
    private Usuario usuario;
    private boolean guardado = false;
    private boolean esEdicion = false;
    
    private JTextField txtNombre, txtUsuario, txtEmail, txtTelefono;
    private JTextField txtEspecialidad, txtLicencia;
    private JPasswordField pfPassword, pfConfirm;
    private JComboBox<String> cbRol;
    private JCheckBox chkActivo;
    private JButton btnGuardar, btnCancelar;
    
    private Color darkBg = new Color(30, 30, 35);
    private Color darkCard = new Color(40, 40, 45);
    private Color textLight = new Color(220, 220, 230);
    private Color textGray = new Color(150, 150, 165);
    private Color accentGreen = new Color(60, 180, 110);
    private Color accentRed = new Color(210, 80, 80);
    private Color fieldBg = new Color(50, 50, 55);
    private Color fieldBorder = new Color(60, 60, 65);
    
    public DialogUsuario(JFrame parent, boolean edicion) {
        super(parent, edicion ? "Editar Usuario" : " Nuevo Usuario", true);
        this.controller = new UsuariosController();
        this.esEdicion = edicion;
        initComponents();
        setSize(500, 600);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setModal(true);
    }
    
    private void initComponents() {
        setBackground(darkBg);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(darkBg);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(darkBg);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        // Título
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel(esEdicion ? "️ Editar Usuario" : " Nuevo Usuario");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(textLight);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        formPanel.add(titleLabel, gbc);
        row++;
        
        gbc.gridwidth = 1;
        
        // Nombre
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Nombre *:"), gbc);
        gbc.gridx = 1;
        txtNombre = createTextField(25);
        formPanel.add(txtNombre, gbc);
        row++;
        
        // Usuario
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Usuario *:"), gbc);
        gbc.gridx = 1;
        txtUsuario = createTextField(20);
        formPanel.add(txtUsuario, gbc);
        row++;
        
        // Contraseña
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel(esEdicion ? "Nueva Contraseña:" : "Contraseña *:"), gbc);
        gbc.gridx = 1;
        pfPassword = new JPasswordField(20);
        pfPassword.setBackground(fieldBg);
        pfPassword.setForeground(textLight);
        pfPassword.setCaretColor(textLight);
        pfPassword.setBorder(BorderFactory.createLineBorder(fieldBorder));
        pfPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(pfPassword, gbc);
        row++;
        
        // Confirmar contraseña
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Confirmar:"), gbc);
        gbc.gridx = 1;
        pfConfirm = new JPasswordField(20);
        pfConfirm.setBackground(fieldBg);
        pfConfirm.setForeground(textLight);
        pfConfirm.setCaretColor(textLight);
        pfConfirm.setBorder(BorderFactory.createLineBorder(fieldBorder));
        pfConfirm.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(pfConfirm, gbc);
        row++;
        
        // Rol
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Rol *:"), gbc);
        gbc.gridx = 1;
        cbRol = new JComboBox<>(controller.getRolesTexto());
        cbRol.setBackground(fieldBg);
        cbRol.setForeground(textLight);
        cbRol.setBorder(BorderFactory.createLineBorder(fieldBorder));
        cbRol.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(cbRol, gbc);
        row++;
        
        // Especialidad (para odontólogos)
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Especialidad:"), gbc);
        gbc.gridx = 1;
        txtEspecialidad = createTextField(25);
        formPanel.add(txtEspecialidad, gbc);
        row++;
        
        // Número de licencia
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("N° Licencia:"), gbc);
        gbc.gridx = 1;
        txtLicencia = createTextField(15);
        formPanel.add(txtLicencia, gbc);
        row++;
        
        // Email
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Email:"), gbc);
        gbc.gridx = 1;
        txtEmail = createTextField(25);
        formPanel.add(txtEmail, gbc);
        row++;
        
        // Teléfono
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Teléfono:"), gbc);
        gbc.gridx = 1;
        txtTelefono = createTextField(15);
        formPanel.add(txtTelefono, gbc);
        row++;
        
        // Activo
        if (esEdicion) {
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(createLabel("Activo:"), gbc);
            gbc.gridx = 1;
            chkActivo = new JCheckBox("Activo");
            chkActivo.setBackground(fieldBg);
            chkActivo.setForeground(textLight);
            chkActivo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            chkActivo.setFocusPainted(false);
            chkActivo.setSelected(true);
            formPanel.add(chkActivo, gbc);
            row++;
        }
        
        JScrollPane scrollForm = new JScrollPane(formPanel);
        scrollForm.setBackground(darkBg);
        scrollForm.setBorder(BorderFactory.createEmptyBorder());
        scrollForm.getViewport().setBackground(darkBg);
        scrollForm.setPreferredSize(new Dimension(450, 500));
        
        // Botones
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnPanel.setBackground(darkBg);
        
        btnGuardar = createDialogButton(" Guardar", accentGreen);
        btnGuardar.addActionListener(e -> guardarUsuario());
        btnPanel.add(btnGuardar);
        
        btnCancelar = createDialogButton(" Cancelar", accentRed);
        btnCancelar.addActionListener(e -> dispose());
        btnPanel.add(btnCancelar);
        
        mainPanel.add(scrollForm, BorderLayout.CENTER);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(textGray);
        return label;
    }
    
    private JTextField createTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setBackground(fieldBg);
        field.setForeground(textLight);
        field.setCaretColor(textLight);
        field.setBorder(BorderFactory.createLineBorder(fieldBorder));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return field;
    }
    
    private JButton createDialogButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(120, 40));
        return btn;
    }
    
    private String getRolFromTexto(String texto) {
        switch (texto) {
            case "Administrador": return Usuario.ROL_ADMIN;
            case "Odontólogo": return Usuario.ROL_DENTISTA;
            case "Asistente": return Usuario.ROL_ASISTENTE;
            case "Recepcionista": return Usuario.ROL_RECEPCIONISTA;
            default: return Usuario.ROL_ADMIN;
        }
    }
    
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        if (usuario != null) {
            txtNombre.setText(usuario.getNombre());
            txtUsuario.setText(usuario.getUsuario());
            txtEmail.setText(usuario.getEmail() != null ? usuario.getEmail() : "");
            txtTelefono.setText(usuario.getTelefono() != null ? usuario.getTelefono() : "");
            txtEspecialidad.setText(usuario.getEspecialidad() != null ? usuario.getEspecialidad() : "");
            txtLicencia.setText(usuario.getNumeroLicencia() != null ? usuario.getNumeroLicencia() : "");
            
            // Seleccionar rol
            String rolTexto = usuario.getRolTexto();
            for (int i = 0; i < cbRol.getItemCount(); i++) {
                if (cbRol.getItemAt(i).equals(rolTexto)) {
                    cbRol.setSelectedIndex(i);
                    break;
                }
            }
            
            if (esEdicion && chkActivo != null) {
                chkActivo.setSelected(usuario.getEstado() == 1);
            }
            
            // En edición, la contraseña no es obligatoria
            pfPassword.setToolTipText("Dejar en blanco para mantener la contraseña actual");
            pfConfirm.setToolTipText("Dejar en blanco para mantener la contraseña actual");
        }
    }
    
    private void guardarUsuario() {
        try {
            String nombre = txtNombre.getText().trim();
            String usuarioStr = txtUsuario.getText().trim();
            String password = new String(pfPassword.getPassword());
            String confirm = new String(pfConfirm.getPassword());
            String rolTexto = (String) cbRol.getSelectedItem();
            String rol = getRolFromTexto(rolTexto);
            String email = txtEmail.getText().trim();
            String telefono = txtTelefono.getText().trim();
            String especialidad = txtEspecialidad.getText().trim();
            String licencia = txtLicencia.getText().trim();
            
            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El nombre es obligatorio", "Error", JOptionPane.WARNING_MESSAGE);
                txtNombre.requestFocus();
                return;
            }
            
            if (usuarioStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El nombre de usuario es obligatorio", "Error", JOptionPane.WARNING_MESSAGE);
                txtUsuario.requestFocus();
                return;
            }
            
            if (!esEdicion) {
                if (password.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "La contraseña es obligatoria", "Error", JOptionPane.WARNING_MESSAGE);
                    pfPassword.requestFocus();
                    return;
                }
                if (!password.equals(confirm)) {
                    JOptionPane.showMessageDialog(this, "Las contraseñas no coinciden", "Error", JOptionPane.WARNING_MESSAGE);
                    pfPassword.requestFocus();
                    return;
                }
            } else {
                // En edición, si se ingresó contraseña, debe coincidir
                if (!password.isEmpty() && !password.equals(confirm)) {
                    JOptionPane.showMessageDialog(this, "Las contraseñas no coinciden", "Error", JOptionPane.WARNING_MESSAGE);
                    pfPassword.requestFocus();
                    return;
                }
            }
            
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setId(usuario != null ? usuario.getId() : 0);
            nuevoUsuario.setNombre(nombre);
            nuevoUsuario.setUsuario(usuarioStr);
            nuevoUsuario.setPassword(password);
            nuevoUsuario.setRol(rol);
            nuevoUsuario.setEmail(email.isEmpty() ? null : email);
            nuevoUsuario.setTelefono(telefono.isEmpty() ? null : telefono);
            nuevoUsuario.setEspecialidad(especialidad.isEmpty() ? null : especialidad);
            nuevoUsuario.setNumeroLicencia(licencia.isEmpty() ? null : licencia);
            
            if (esEdicion && chkActivo != null) {
                nuevoUsuario.setEstado(chkActivo.isSelected() ? 1 : 0);
            } else {
                nuevoUsuario.setEstado(1);
            }
            
            nuevoUsuario.setBloqueado(0);
            nuevoUsuario.setIntentos(0);
            
            if (controller.guardarUsuario(nuevoUsuario, esEdicion)) {
                guardado = true;
                dispose();
            }
            
        } catch (Exception e) {
            System.err.println("Error al guardar usuario: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error al guardar: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean isGuardado() {
        return guardado;
    }
}