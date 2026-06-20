package com.dentalclinicsystem.view;

import com.dentalclinicsystem.controller.ServicioController;
import com.dentalclinicsystem.model.Servicio;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DialogServicio extends JDialog {
    
    private ServicioController controller;
    private Servicio servicio;
    private boolean guardado = false;
    private boolean esEdicion = false;
    
    private JTextField txtNombre, txtDuracion, txtPrecio;
    private JTextField txtPrecioParticular, txtPrecioAseguradora, txtCodigo;
    private JComboBox<String> cbCategoria, cbSubcategoria, cbEstado;
    private JCheckBox chkRequiereConsultaPrevia;
    private JTextArea txtMateriales;
    private JButton btnGuardar, btnCancelar;
    
    private Color darkBg = new Color(30, 30, 35);
    private Color darkCard = new Color(40, 40, 45);
    private Color textLight = new Color(220, 220, 230);
    private Color textGray = new Color(150, 150, 165);
    private Color accentGreen = new Color(60, 180, 110);
    private Color accentRed = new Color(210, 80, 80);
    private Color fieldBg = new Color(50, 50, 55);
    private Color fieldBorder = new Color(60, 60, 65);
    
    public DialogServicio(JFrame parent, boolean edicion) {
        super(parent, edicion ? "Editar Servicio" : "Nuevo Servicio", true);
        this.controller = new ServicioController();
        this.esEdicion = edicion;
        initComponents();
        setSize(550, 600);
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
        JLabel titleLabel = new JLabel(esEdicion ? "Editar Servicio" : " Nuevo Servicio");
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
        
        // Categoría
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Categoría *:"), gbc);
        gbc.gridx = 1;
        cbCategoria = new JComboBox<>(controller.getCategorias());
        cbCategoria.setBackground(fieldBg);
        cbCategoria.setForeground(textLight);
        cbCategoria.setBorder(BorderFactory.createLineBorder(fieldBorder));
        cbCategoria.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(cbCategoria, gbc);
        row++;
        
        // Subcategoría
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Subcategoría:"), gbc);
        gbc.gridx = 1;
        txtSubcategoria = createTextField(20);
        formPanel.add(txtSubcategoria, gbc);
        row++;
        
        // Duración
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Duración (minutos) *:"), gbc);
        gbc.gridx = 1;
        txtDuracion = createTextField(5);
        txtDuracion.setText("30");
        formPanel.add(txtDuracion, gbc);
        row++;
        
        // Precio
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Precio *:"), gbc);
        gbc.gridx = 1;
        txtPrecio = createTextField(10);
        txtPrecio.setText("0.00");
        formPanel.add(txtPrecio, gbc);
        row++;
        
        // Precio Particular
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Precio Particular:"), gbc);
        gbc.gridx = 1;
        txtPrecioParticular = createTextField(10);
        formPanel.add(txtPrecioParticular, gbc);
        row++;
        
        // Precio Aseguradora
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Precio Aseguradora:"), gbc);
        gbc.gridx = 1;
        txtPrecioAseguradora = createTextField(10);
        formPanel.add(txtPrecioAseguradora, gbc);
        row++;
        
        // Código de procedimiento
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Código de Procedimiento:"), gbc);
        gbc.gridx = 1;
        txtCodigo = createTextField(15);
        formPanel.add(txtCodigo, gbc);
        row++;
        
        // Requiere consulta previa
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Requiere Consulta Previa:"), gbc);
        gbc.gridx = 1;
        chkRequiereConsultaPrevia = new JCheckBox("Sí");
        chkRequiereConsultaPrevia.setBackground(fieldBg);
        chkRequiereConsultaPrevia.setForeground(textLight);
        chkRequiereConsultaPrevia.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        chkRequiereConsultaPrevia.setFocusPainted(false);
        formPanel.add(chkRequiereConsultaPrevia, gbc);
        row++;
        
        // Materiales necesarios
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Materiales Necesarios:"), gbc);
        gbc.gridx = 1;
        txtMateriales = new JTextArea(3, 20);
        txtMateriales.setLineWrap(true);
        txtMateriales.setWrapStyleWord(true);
        txtMateriales.setBackground(fieldBg);
        txtMateriales.setForeground(textLight);
        txtMateriales.setCaretColor(textLight);
        txtMateriales.setBorder(BorderFactory.createLineBorder(fieldBorder));
        txtMateriales.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JScrollPane scrollMateriales = new JScrollPane(txtMateriales);
        scrollMateriales.setBackground(fieldBg);
        scrollMateriales.setBorder(BorderFactory.createLineBorder(fieldBorder));
        formPanel.add(scrollMateriales, gbc);
        row++;
        
        // Estado
        if (esEdicion) {
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(createLabel("Estado:"), gbc);
            gbc.gridx = 1;
            cbEstado = new JComboBox<>(new String[]{"Activo", "Inactivo"});
            cbEstado.setBackground(fieldBg);
            cbEstado.setForeground(textLight);
            cbEstado.setBorder(BorderFactory.createLineBorder(fieldBorder));
            cbEstado.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            formPanel.add(cbEstado, gbc);
            row++;
        }
        
        JScrollPane scrollForm = new JScrollPane(formPanel);
        scrollForm.setBackground(darkBg);
        scrollForm.setBorder(BorderFactory.createEmptyBorder());
        scrollForm.getViewport().setBackground(darkBg);
        scrollForm.setPreferredSize(new Dimension(500, 500));
        
        // Botones
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnPanel.setBackground(darkBg);
        
        btnGuardar = createDialogButton("Guardar", accentGreen);
        btnGuardar.addActionListener(e -> guardarServicio());
        btnPanel.add(btnGuardar);
        
        btnCancelar = createDialogButton("Cancelar", accentRed);
        btnCancelar.addActionListener(e -> dispose());
        btnPanel.add(btnCancelar);
        
        mainPanel.add(scrollForm, BorderLayout.CENTER);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    // ========== MÉTODOS AUXILIARES ==========
    
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
    
    private JTextField txtSubcategoria;
    
    public void setServicio(Servicio servicio) {
        this.servicio = servicio;
        if (servicio != null) {
            txtNombre.setText(servicio.getNombre());
            cbCategoria.setSelectedItem(servicio.getCategoria());
            txtSubcategoria.setText(servicio.getSubcategoria() != null ? servicio.getSubcategoria() : "");
            txtDuracion.setText(String.valueOf(servicio.getDuracionMinutos()));
            txtPrecio.setText(String.format("%.2f", servicio.getPrecio()));
            if (servicio.getPrecioParticular() > 0) {
                txtPrecioParticular.setText(String.format("%.2f", servicio.getPrecioParticular()));
            }
            if (servicio.getPrecioAseguradora() > 0) {
                txtPrecioAseguradora.setText(String.format("%.2f", servicio.getPrecioAseguradora()));
            }
            txtCodigo.setText(servicio.getCodigoProcedimiento() != null ? servicio.getCodigoProcedimiento() : "");
            chkRequiereConsultaPrevia.setSelected(servicio.getRequiereConsultaPrevia() == 1);
            txtMateriales.setText(servicio.getMaterialesNecesarios() != null ? servicio.getMaterialesNecesarios() : "");
            
            if (esEdicion && cbEstado != null) {
                cbEstado.setSelectedItem(servicio.getEstado() == 1 ? "Activo" : "Inactivo");
            }
        }
    }
    
    private void guardarServicio() {
        try {
            String nombre = txtNombre.getText().trim();
            String categoria = (String) cbCategoria.getSelectedItem();
            String subcategoria = txtSubcategoria.getText().trim();
            int duracion;
            double precio;
            
            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El nombre es obligatorio", "Error", JOptionPane.WARNING_MESSAGE);
                txtNombre.requestFocus();
                return;
            }
            
            if (categoria == null || categoria.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Seleccione una categoría", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            try {
                duracion = Integer.parseInt(txtDuracion.getText().trim());
                if (duracion < 5 || duracion > 480) {
                    JOptionPane.showMessageDialog(this, "La duración debe estar entre 5 y 480 minutos", "Error", JOptionPane.WARNING_MESSAGE);
                    txtDuracion.requestFocus();
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Ingrese una duración válida", "Error", JOptionPane.WARNING_MESSAGE);
                txtDuracion.requestFocus();
                return;
            }
            
            try {
                precio = Double.parseDouble(txtPrecio.getText().trim());
                if (precio < 0) {
                    JOptionPane.showMessageDialog(this, "El precio no puede ser negativo", "Error", JOptionPane.WARNING_MESSAGE);
                    txtPrecio.requestFocus();
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Ingrese un precio válido", "Error", JOptionPane.WARNING_MESSAGE);
                txtPrecio.requestFocus();
                return;
            }
            
            Servicio nuevoServicio = new Servicio();
            nuevoServicio.setId(servicio != null ? servicio.getId() : 0);
            nuevoServicio.setNombre(nombre);
            nuevoServicio.setCategoria(categoria);
            nuevoServicio.setSubcategoria(subcategoria.isEmpty() ? null : subcategoria);
            nuevoServicio.setDuracionMinutos(duracion);
            nuevoServicio.setPrecio(precio);
            
            // Precios opcionales
            try {
                double precioParticular = Double.parseDouble(txtPrecioParticular.getText().trim());
                nuevoServicio.setPrecioParticular(precioParticular > 0 ? precioParticular : 0);
            } catch (NumberFormatException e) {
                nuevoServicio.setPrecioParticular(0);
            }
            
            try {
                double precioAseguradora = Double.parseDouble(txtPrecioAseguradora.getText().trim());
                nuevoServicio.setPrecioAseguradora(precioAseguradora > 0 ? precioAseguradora : 0);
            } catch (NumberFormatException e) {
                nuevoServicio.setPrecioAseguradora(0);
            }
            
            nuevoServicio.setCodigoProcedimiento(txtCodigo.getText().trim().isEmpty() ? null : txtCodigo.getText().trim());
            nuevoServicio.setRequiereConsultaPrevia(chkRequiereConsultaPrevia.isSelected() ? 1 : 0);
            nuevoServicio.setMaterialesNecesarios(txtMateriales.getText().trim().isEmpty() ? null : txtMateriales.getText().trim());
            
            if (esEdicion && cbEstado != null) {
                nuevoServicio.setEstado(cbEstado.getSelectedItem().equals("Activo") ? 1 : 0);
            } else {
                nuevoServicio.setEstado(1);
            }
            
            if (controller.guardarServicio(nuevoServicio)) {
                guardado = true;
                dispose();
            }
            
        } catch (Exception e) {
            System.err.println("Error al guardar servicio: " + e.getMessage());
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