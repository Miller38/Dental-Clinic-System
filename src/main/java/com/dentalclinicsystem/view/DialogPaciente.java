package com.dentalclinicsystem.view;

import com.dentalclinicsystem.controller.PacienteController;
import com.dentalclinicsystem.model.Paciente;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class DialogPaciente extends JDialog {
    
    private PacienteController controller;
    private Paciente paciente;
    private boolean guardado = false;
    private boolean esEdicion = false;
    
    // Componentes del formulario
    private JTextField txtNombre, txtApellido, txtDocumento, txtTelefono;
    private JTextField txtTelefonoAlt, txtEmail, txtDireccion, txtOcupacion;
    private JTextField txtEdad, txtContactoEmergencia, txtTelefonoEmergencia;
    private JComboBox<String> cbGenero, cbEstadoCivil;
    private JTextArea txtAlergias, txtEnfermedades, txtMedicamentos;
    private JButton btnGuardar, btnCancelar;
    private JLabel lblDocumentoValido, lblTelefonoValido;
    
    private Color darkBg = new Color(30, 30, 35);
    private Color darkCard = new Color(40, 40, 45);
    private Color textLight = new Color(220, 220, 230);
    private Color textGray = new Color(150, 150, 165);
    private Color accentGreen = new Color(60, 180, 110);
    private Color accentRed = new Color(210, 80, 80);
    private Color fieldBg = new Color(50, 50, 55);
    private Color fieldBorder = new Color(60, 60, 65);
    
    public DialogPaciente(JFrame parent, boolean edicion) {
        super(parent, edicion ? "Editar Paciente" : " Nuevo Paciente", true);
        this.controller = new PacienteController();
        this.esEdicion = edicion;
        initComponents();
        setSize(650, 750);
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
        
        // -------------------------------------------------Título-------------------------------------//
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel(esEdicion ? "Editar Paciente" : " Nuevo Paciente");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(textLight);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        formPanel.add(titleLabel, gbc);
        row++;
        
        gbc.gridwidth = 1;
        
        // -------------------------------------NOMBRE ----------------------------------------//
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Nombre *:"), gbc);
        gbc.gridx = 1;
        txtNombre = createTextField(25);
        // ----------------------VALIDACIÓN: Solo letras y espacios------------------------//
        txtNombre.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isLetter(c) && c != KeyEvent.VK_SPACE && c != KeyEvent.VK_BACK_SPACE) {
                    e.consume();
                }
            }
        });
        formPanel.add(txtNombre, gbc);
        row++;
        
        // -------------------------------------- APELLIDO----------------------------------//
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Apellido *:"), gbc);
        gbc.gridx = 1;
        txtApellido = createTextField(25);
        // 🔥 VALIDACIÓN: Solo letras y espacios
        txtApellido.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isLetter(c) && c != KeyEvent.VK_SPACE && c != KeyEvent.VK_BACK_SPACE) {
                    e.consume();
                }
            }
        });
        formPanel.add(txtApellido, gbc);
        row++;
        
        //------------------------------------DOCUMENTO-----------------------------------//
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Documento *:"), gbc);
        gbc.gridx = 1;
        txtDocumento = createTextField(15);
        // 🔥 VALIDACIÓN: Solo números
        aplicarFiltroNumerico(txtDocumento, 15);
        txtDocumento.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { validarDocumento(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { validarDocumento(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { validarDocumento(); }
        });
        formPanel.add(txtDocumento, gbc);
        row++;
        
        // ------------------Indicador de validación de documento----------------------------//
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        lblDocumentoValido = new JLabel("Ingrese el número de documento (7-15 dígitos)");
        lblDocumentoValido.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblDocumentoValido.setForeground(textGray);
        formPanel.add(lblDocumentoValido, gbc);
        row++;
        gbc.gridwidth = 1;
        
        //---------------------------------TELÉFONO ------------------------------------------//
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Teléfono *:"), gbc);
        gbc.gridx = 1;
        txtTelefono = createTextField(10);
        // 🔥 VALIDACIÓN: Solo números
        aplicarFiltroNumerico(txtTelefono, 10);
        txtTelefono.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { validarTelefono(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { validarTelefono(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { validarTelefono(); }
        });
        formPanel.add(txtTelefono, gbc);
        row++;
        
        //----------------------- Indicador de validación de teléfono-------------------------//
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        lblTelefonoValido = new JLabel("Ingrese el número de teléfono (7-10 dígitos)");
        lblTelefonoValido.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTelefonoValido.setForeground(textGray);
        formPanel.add(lblTelefonoValido, gbc);
        row++;
        gbc.gridwidth = 1;
        
        // TELÉFONO ALTERNATIVO
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Teléfono Alt:"), gbc);
        gbc.gridx = 1;
        txtTelefonoAlt = createTextField(10);
        aplicarFiltroNumerico(txtTelefonoAlt, 10);
        formPanel.add(txtTelefonoAlt, gbc);
        row++;
        
        // ========== EMAIL ==========
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Email:"), gbc);
        gbc.gridx = 1;
        txtEmail = createTextField(25);
        formPanel.add(txtEmail, gbc);
        row++;
        
        // ========== EDAD ==========
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Edad:"), gbc);
        gbc.gridx = 1;
        txtEdad = createTextField(3);
        aplicarFiltroNumerico(txtEdad, 3);
        formPanel.add(txtEdad, gbc);
        row++;
        
        // ========== GÉNERO ==========
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Género:"), gbc);
        gbc.gridx = 1;
        cbGenero = new JComboBox<>(new String[]{"", "M", "F", "OTRO"});
        cbGenero.setBackground(fieldBg);
        cbGenero.setForeground(textLight);
        cbGenero.setBorder(BorderFactory.createLineBorder(fieldBorder));
        cbGenero.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(cbGenero, gbc);
        row++;
        
        // ========== ESTADO CIVIL ==========
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Estado Civil:"), gbc);
        gbc.gridx = 1;
        cbEstadoCivil = new JComboBox<>(new String[]{"", "Soltero/a", "Casado/a", "Divorciado/a", "Viudo/a", "Unión Libre"});
        cbEstadoCivil.setBackground(fieldBg);
        cbEstadoCivil.setForeground(textLight);
        cbEstadoCivil.setBorder(BorderFactory.createLineBorder(fieldBorder));
        cbEstadoCivil.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(cbEstadoCivil, gbc);
        row++;
        
        // ========== OCUPACIÓN ==========
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Ocupación:"), gbc);
        gbc.gridx = 1;
        txtOcupacion = createTextField(25);
        formPanel.add(txtOcupacion, gbc);
        row++;
        
        // ========== DIRECCIÓN ==========
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Dirección:"), gbc);
        gbc.gridx = 1;
        txtDireccion = createTextField(30);
        formPanel.add(txtDireccion, gbc);
        row++;
        
        // ========== CONTACTO EMERGENCIA ==========
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Contacto Emergencia:"), gbc);
        gbc.gridx = 1;
        txtContactoEmergencia = createTextField(25);
        formPanel.add(txtContactoEmergencia, gbc);
        row++;
        
        // ========== TELÉFONO EMERGENCIA ==========
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Teléfono Emergencia:"), gbc);
        gbc.gridx = 1;
        txtTelefonoEmergencia = createTextField(10);
        aplicarFiltroNumerico(txtTelefonoEmergencia, 10);
        formPanel.add(txtTelefonoEmergencia, gbc);
        row++;
        
        // ========== ALERGIAS ==========
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Alergias:"), gbc);
        gbc.gridx = 1;
        txtAlergias = createTextArea(2, 20);
        JScrollPane scrollAlergias = new JScrollPane(txtAlergias);
        scrollAlergias.setBackground(fieldBg);
        scrollAlergias.setBorder(BorderFactory.createLineBorder(fieldBorder));
        formPanel.add(scrollAlergias, gbc);
        row++;
        
        // ========== ENFERMEDADES ==========
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Enfermedades:"), gbc);
        gbc.gridx = 1;
        txtEnfermedades = createTextArea(2, 20);
        JScrollPane scrollEnfermedades = new JScrollPane(txtEnfermedades);
        scrollEnfermedades.setBackground(fieldBg);
        scrollEnfermedades.setBorder(BorderFactory.createLineBorder(fieldBorder));
        formPanel.add(scrollEnfermedades, gbc);
        row++;
        
        // ========== MEDICAMENTOS ==========
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Medicamentos:"), gbc);
        gbc.gridx = 1;
        txtMedicamentos = createTextArea(2, 20);
        JScrollPane scrollMedicamentos = new JScrollPane(txtMedicamentos);
        scrollMedicamentos.setBackground(fieldBg);
        scrollMedicamentos.setBorder(BorderFactory.createLineBorder(fieldBorder));
        formPanel.add(scrollMedicamentos, gbc);
        row++;
        
        JScrollPane scrollForm = new JScrollPane(formPanel);
        scrollForm.setBackground(darkBg);
        scrollForm.setBorder(BorderFactory.createEmptyBorder());
        scrollForm.getViewport().setBackground(darkBg);
        scrollForm.setPreferredSize(new Dimension(600, 500));
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnPanel.setBackground(darkBg);
        
        btnGuardar = createDialogButton("💾 Guardar", accentGreen);
        btnGuardar.addActionListener(e -> guardarPaciente());
        btnPanel.add(btnGuardar);
        
        btnCancelar = createDialogButton("❌ Cancelar", accentRed);
        btnCancelar.addActionListener(e -> dispose());
        btnPanel.add(btnCancelar);
        
        mainPanel.add(scrollForm, BorderLayout.CENTER);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    // ================================================================
    // ========== VALIDACIONES EN TIEMPO REAL ==========
    // ================================================================
    
    private void validarDocumento() {
        String doc = txtDocumento.getText().trim();
        if (doc.isEmpty()) {
            lblDocumentoValido.setText("📌 Ingrese el número de documento (7-15 dígitos)");
            lblDocumentoValido.setForeground(textGray);
            txtDocumento.setBorder(BorderFactory.createLineBorder(fieldBorder));
            return;
        }
        
        if (!doc.matches("\\d+")) {
            lblDocumentoValido.setText("❌ Solo números");
            lblDocumentoValido.setForeground(accentRed);
            txtDocumento.setBorder(BorderFactory.createLineBorder(accentRed, 2));
        } else if (doc.length() < 7) {
            lblDocumentoValido.setText("⚠️ Mínimo 7 dígitos");
            lblDocumentoValido.setForeground(accentRed);
            txtDocumento.setBorder(BorderFactory.createLineBorder(accentRed, 2));
        } else if (doc.length() > 15) {
            lblDocumentoValido.setText("⚠️ Máximo 15 dígitos");
            lblDocumentoValido.setForeground(accentRed);
            txtDocumento.setBorder(BorderFactory.createLineBorder(accentRed, 2));
        } else {
            lblDocumentoValido.setText("✅ Documento válido");
            lblDocumentoValido.setForeground(accentGreen);
            txtDocumento.setBorder(BorderFactory.createLineBorder(fieldBorder));
        }
    }
    
    private void validarTelefono() {
        String tel = txtTelefono.getText().trim();
        if (tel.isEmpty()) {
            lblTelefonoValido.setText("📌 Ingrese el número de teléfono (7-10 dígitos)");
            lblTelefonoValido.setForeground(textGray);
            txtTelefono.setBorder(BorderFactory.createLineBorder(fieldBorder));
            return;
        }
        
        if (!tel.matches("\\d+")) {
            lblTelefonoValido.setText("❌ Solo números");
            lblTelefonoValido.setForeground(accentRed);
            txtTelefono.setBorder(BorderFactory.createLineBorder(accentRed, 2));
        } else if (tel.length() < 7) {
            lblTelefonoValido.setText("⚠️ Mínimo 7 dígitos");
            lblTelefonoValido.setForeground(accentRed);
            txtTelefono.setBorder(BorderFactory.createLineBorder(accentRed, 2));
        } else if (tel.length() > 10) {
            lblTelefonoValido.setText("⚠️ Máximo 10 dígitos");
            lblTelefonoValido.setForeground(accentRed);
            txtTelefono.setBorder(BorderFactory.createLineBorder(accentRed, 2));
        } else {
            lblTelefonoValido.setText("✅ Teléfono válido");
            lblTelefonoValido.setForeground(accentGreen);
            txtTelefono.setBorder(BorderFactory.createLineBorder(fieldBorder));
        }
    }
    
    // ================================================================
    // ========== FILTRO PARA SOLO NÚMEROS ==========
    // ================================================================
    
    private void aplicarFiltroNumerico(JTextField field, int maxLength) {
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) 
                    throws BadLocationException {
                if (string == null) return;
                if (string.matches("\\d*")) {
                    String newStr = fb.getDocument().getText(0, fb.getDocument().getLength()) + string;
                    if (newStr.length() <= maxLength) {
                        super.insertString(fb, offset, string, attr);
                    }
                }
            }
            
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) 
                    throws BadLocationException {
                if (text == null) return;
                if (text.matches("\\d*")) {
                    String newStr = fb.getDocument().getText(0, fb.getDocument().getLength()) + text;
                    if (newStr.length() <= maxLength) {
                        super.replace(fb, offset, length, text, attrs);
                    }
                }
            }
        });
        
        // Validación adicional en tiempo real
        field.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                    e.consume();
                }
            }
        });
    }
    
    // ================================================================
    // ========== MÉTODOS AUXILIARES ==========
    // ================================================================
    
    private JTextField createTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setBackground(fieldBg);
        field.setForeground(textLight);
        field.setCaretColor(textLight);
        field.setBorder(BorderFactory.createLineBorder(fieldBorder));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return field;
    }
    
    private JComboBox<String> createComboBox(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setBackground(fieldBg);
        combo.setForeground(textLight);
        combo.setBorder(BorderFactory.createLineBorder(fieldBorder));
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return combo;
    }
    
    private JTextArea createTextArea(int rows, int columns) {
        JTextArea area = new JTextArea(rows, columns);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBackground(fieldBg);
        area.setForeground(textLight);
        area.setCaretColor(textLight);
        area.setBorder(BorderFactory.createLineBorder(fieldBorder));
        area.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return area;
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(textGray);
        return label;
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
    
    // ================================================================
    // ========== SET PACIENTE Y GUARDAR ==========
    // ================================================================
    
    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
        if (paciente != null) {
            txtNombre.setText(paciente.getNombre() != null ? paciente.getNombre() : "");
            txtApellido.setText(paciente.getApellido() != null ? paciente.getApellido() : "");
            txtDocumento.setText(paciente.getNumeroDocumento() != null ? paciente.getNumeroDocumento() : "");
            txtTelefono.setText(paciente.getTelefono() != null ? paciente.getTelefono() : "");
            txtTelefonoAlt.setText(paciente.getTelefonoAlternativo() != null ? paciente.getTelefonoAlternativo() : "");
            txtEmail.setText(paciente.getEmail() != null ? paciente.getEmail() : "");
            txtDireccion.setText(paciente.getDireccion() != null ? paciente.getDireccion() : "");
            txtOcupacion.setText(paciente.getOcupacion() != null ? paciente.getOcupacion() : "");
            txtEdad.setText(paciente.getEdad() > 0 ? String.valueOf(paciente.getEdad()) : "");
            txtContactoEmergencia.setText(paciente.getContactoEmergenciaNombre() != null ? paciente.getContactoEmergenciaNombre() : "");
            txtTelefonoEmergencia.setText(paciente.getContactoEmergenciaTelefono() != null ? paciente.getContactoEmergenciaTelefono() : "");
            
            if (paciente.getGenero() != null) {
                cbGenero.setSelectedItem(paciente.getGenero());
            }
            if (paciente.getEstadoCivil() != null) {
                cbEstadoCivil.setSelectedItem(paciente.getEstadoCivil());
            }
            
            txtAlergias.setText(paciente.getAlergias() != null ? paciente.getAlergias() : "");
            txtEnfermedades.setText(paciente.getEnfermedadesSistema() != null ? paciente.getEnfermedadesSistema() : "");
            txtMedicamentos.setText(paciente.getMedicamentos() != null ? paciente.getMedicamentos() : "");
            
            // Validar en tiempo real al cargar
            validarDocumento();
            validarTelefono();
        }
    }
    
    private void guardarPaciente() {
    try {
        // ===== 1. VALIDAR CAMPOS OBLIGATORIOS =====
        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String documento = txtDocumento.getText().trim();
        String telefono = txtTelefono.getText().trim();
        
        if (nombre.isEmpty()) {
            mostrarError("El nombre es obligatorio");
            txtNombre.requestFocus();
            return;
        }
        
        if (apellido.isEmpty()) {
            mostrarError("El apellido es obligatorio");
            txtApellido.requestFocus();
            return;
        }
        
        if (documento.isEmpty()) {
            mostrarError("El documento es obligatorio");
            txtDocumento.requestFocus();
            return;
        }
        
        if (telefono.isEmpty()) {
            mostrarError("El teléfono es obligatorio");
            txtTelefono.requestFocus();
            return;
        }
        
        // ===== 2. VALIDAR NOMBRE (solo letras y espacios) =====
        if (!nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
            mostrarError("El nombre solo debe contener letras y espacios");
            txtNombre.requestFocus();
            return;
        }
        
        if (!apellido.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
            mostrarError("El apellido solo debe contener letras y espacios");
            txtApellido.requestFocus();
            return;
        }
        
        // ===== 3. VALIDAR DOCUMENTO (solo números) =====
        if (!documento.matches("\\d+")) {
            mostrarError("El documento solo debe contener números");
            txtDocumento.requestFocus();
            return;
        }
        
        if (documento.length() < 7) {
            mostrarError("El documento debe tener al menos 7 dígitos");
            txtDocumento.requestFocus();
            return;
        }
        
        if (documento.length() > 15) {
            mostrarError("El documento no puede tener más de 15 dígitos");
            txtDocumento.requestFocus();
            return;
        }
        
        // ===== 4. VALIDAR TELÉFONO (solo números) =====
        if (!telefono.matches("\\d+")) {
            mostrarError("El teléfono solo debe contener números");
            txtTelefono.requestFocus();
            return;
        }
        
        if (telefono.length() < 7 || telefono.length() > 10) {
            mostrarError("El teléfono debe tener entre 7 y 10 dígitos");
            txtTelefono.requestFocus();
            return;
        }
        
        // ===== 5. VALIDAR TELÉFONO ALTERNATIVO (si se proporciona) =====
        String telefonoAlt = txtTelefonoAlt.getText().trim();
        if (!telefonoAlt.isEmpty()) {
            if (!telefonoAlt.matches("\\d+")) {
                mostrarError("El teléfono alternativo solo debe contener números");
                txtTelefonoAlt.requestFocus();
                return;
            }
            if (telefonoAlt.length() < 7 || telefonoAlt.length() > 10) {
                mostrarError("El teléfono alternativo debe tener entre 7 y 10 dígitos");
                txtTelefonoAlt.requestFocus();
                return;
            }
        }
        
        // ===== 6. VALIDAR TELÉFONO EMERGENCIA (si se proporciona) =====
        String telEmergencia = txtTelefonoEmergencia.getText().trim();
        if (!telEmergencia.isEmpty()) {
            if (!telEmergencia.matches("\\d+")) {
                mostrarError("El teléfono de emergencia solo debe contener números");
                txtTelefonoEmergencia.requestFocus();
                return;
            }
            if (telEmergencia.length() < 7 || telEmergencia.length() > 10) {
                mostrarError("El teléfono de emergencia debe tener entre 7 y 10 dígitos");
                txtTelefonoEmergencia.requestFocus();
                return;
            }
        }
        
        // ===== 7. VALIDAR EMAIL (si se proporciona) =====
        String email = txtEmail.getText().trim();
        if (!email.isEmpty()) {
            if (!validarEmail(email)) {
                mostrarError("El email no es válido. Ejemplo: usuario@dominio.com");
                txtEmail.requestFocus();
                return;
            }
        }
        
        // ===== 8. VALIDAR EDAD =====
        try {
            String edadStr = txtEdad.getText().trim();
            if (!edadStr.isEmpty()) {
                int edad = Integer.parseInt(edadStr);
                if (edad < 0 || edad > 150) {
                    mostrarError("La edad debe estar entre 0 y 150 años");
                    txtEdad.requestFocus();
                    return;
                }
            }
        } catch (NumberFormatException e) {
            mostrarError("Ingrese una edad válida (número)");
            txtEdad.requestFocus();
            return;
        }
        
        // ===== 9. VALIDAR DOCUMENTO ÚNICO (con excepción para edición) =====
        Paciente existente = controller.buscarPorDocumento(documento);
        // Si existe un paciente con el mismo documento Y no es el que estamos editando
        if (existente != null) {
            // Si estamos editando y el ID coincide, permitir
            if (paciente != null && existente.getId() == paciente.getId()) {
                // Es el mismo paciente, permitir
            } else {
                mostrarError("Ya existe un paciente con este número de documento");
                txtDocumento.requestFocus();
                return;
            }
        }
        
        // ===== 10. CREAR PACIENTE =====
        Paciente nuevoPaciente = new Paciente();
        nuevoPaciente.setId(paciente != null ? paciente.getId() : 0);
        nuevoPaciente.setNombre(capitalizar(nombre));
        nuevoPaciente.setApellido(capitalizar(apellido));
        nuevoPaciente.setNumeroDocumento(documento);
        nuevoPaciente.setTelefono(telefono);
        nuevoPaciente.setTelefonoAlternativo(telefonoAlt);
        nuevoPaciente.setEmail(email);
        nuevoPaciente.setDireccion(txtDireccion.getText().trim());
        nuevoPaciente.setOcupacion(txtOcupacion.getText().trim());
        nuevoPaciente.setContactoEmergenciaNombre(txtContactoEmergencia.getText().trim());
        nuevoPaciente.setContactoEmergenciaTelefono(telEmergencia);
        nuevoPaciente.setGenero((String) cbGenero.getSelectedItem());
        nuevoPaciente.setEstadoCivil((String) cbEstadoCivil.getSelectedItem());
        
        try {
            String edadStr = txtEdad.getText().trim();
            nuevoPaciente.setEdad(edadStr.isEmpty() ? 0 : Integer.parseInt(edadStr));
        } catch (NumberFormatException e) {
            nuevoPaciente.setEdad(0);
        }
        
        nuevoPaciente.setAlergias(txtAlergias.getText().trim());
        nuevoPaciente.setEnfermedadesSistema(txtEnfermedades.getText().trim());
        nuevoPaciente.setMedicamentos(txtMedicamentos.getText().trim());
        nuevoPaciente.setEstado(1);
        
        // ===== 11. GUARDAR =====
        if (controller.guardarPaciente(nuevoPaciente)) {
            guardado = true;
            dispose();
        }
        
    } catch (Exception e) {
        System.err.println("Error al guardar paciente: " + e.getMessage());
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, 
            "Error al guardar: " + e.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
    }
}
    
    // ================================================================
    // ========== MÉTODOS DE UTILIDAD ==========
    // ================================================================
    
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, 
            "❌ " + mensaje,
            "Error de validación", 
            JOptionPane.WARNING_MESSAGE);
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
    
    public boolean isGuardado() {
        return guardado;
    }
}