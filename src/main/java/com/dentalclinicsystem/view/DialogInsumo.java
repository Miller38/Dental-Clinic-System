package com.dentalclinicsystem.view;

import com.dentalclinicsystem.controller.InsumoController;
import com.dentalclinicsystem.model.Insumo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DialogInsumo extends JDialog {
    
    private InsumoController controller;
    private Insumo insumo;
    private boolean guardado = false;
    private boolean esEdicion = false;
    
    private JTextField txtNombre, txtCodigo, txtStock, txtStockMinimo;
    private JTextField txtStockMaximo, txtUbicacion, txtPrecioCompra, txtPrecioVenta;
    private JTextField txtLote, txtProveedor;
    private JComboBox<String> cbCategoria, cbTipoInsumo, cbPresentacion, cbEstado;
    private JTextField txtFechaCompra, txtFechaVencimiento;
    private JButton btnGuardar, btnCancelar;
    
    private Color darkBg = new Color(30, 30, 35);
    private Color darkCard = new Color(40, 40, 45);
    private Color textLight = new Color(220, 220, 230);
    private Color textGray = new Color(150, 150, 165);
    private Color accentGreen = new Color(60, 180, 110);
    private Color accentRed = new Color(210, 80, 80);
    private Color fieldBg = new Color(50, 50, 55);
    private Color fieldBorder = new Color(60, 60, 65);
    
    public DialogInsumo(JFrame parent, boolean edicion) {
        super(parent, edicion ? "Editar Insumo" : "Nuevo Insumo", true);
        this.controller = new InsumoController();
        this.esEdicion = edicion;
        initComponents();
        setSize(580, 650);
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
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        // Título
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel(esEdicion ? "Editar Insumo" : "Nuevo Insumo");
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
        
        // Código
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Código *:"), gbc);
        gbc.gridx = 1;
        txtCodigo = createTextField(15);
        formPanel.add(txtCodigo, gbc);
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
        
        // Tipo de Insumo
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Tipo de Insumo:"), gbc);
        gbc.gridx = 1;
        cbTipoInsumo = new JComboBox<>(controller.getTiposInsumo());
        cbTipoInsumo.setBackground(fieldBg);
        cbTipoInsumo.setForeground(textLight);
        cbTipoInsumo.setBorder(BorderFactory.createLineBorder(fieldBorder));
        cbTipoInsumo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(cbTipoInsumo, gbc);
        row++;
        
        // Presentación
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Presentación:"), gbc);
        gbc.gridx = 1;
        cbPresentacion = new JComboBox<>(new String[]{"Unidad", "Caja", "Kit", "Paquete", "Tubo", "Frasco"});
        cbPresentacion.setBackground(fieldBg);
        cbPresentacion.setForeground(textLight);
        cbPresentacion.setBorder(BorderFactory.createLineBorder(fieldBorder));
        cbPresentacion.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(cbPresentacion, gbc);
        row++;
        
        // Stock
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Stock:"), gbc);
        gbc.gridx = 1;
        txtStock = createTextField(8);
        txtStock.setText("0");
        formPanel.add(txtStock, gbc);
        row++;
        
        // Stock Mínimo
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Stock Mínimo:"), gbc);
        gbc.gridx = 1;
        txtStockMinimo = createTextField(8);
        txtStockMinimo.setText("5");
        formPanel.add(txtStockMinimo, gbc);
        row++;
        
        // Stock Máximo
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Stock Máximo:"), gbc);
        gbc.gridx = 1;
        txtStockMaximo = createTextField(8);
        formPanel.add(txtStockMaximo, gbc);
        row++;
        
        // Ubicación
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Ubicación:"), gbc);
        gbc.gridx = 1;
        txtUbicacion = createTextField(20);
        formPanel.add(txtUbicacion, gbc);
        row++;
        
        // Precio Compra
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Precio Compra:"), gbc);
        gbc.gridx = 1;
        txtPrecioCompra = createTextField(12);
        txtPrecioCompra.setText("0.00");
        formPanel.add(txtPrecioCompra, gbc);
        row++;
        
        // Precio Venta
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Precio Venta:"), gbc);
        gbc.gridx = 1;
        txtPrecioVenta = createTextField(12);
        txtPrecioVenta.setText("0.00");
        formPanel.add(txtPrecioVenta, gbc);
        row++;
        
        // Fecha Compra
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Fecha Compra:"), gbc);
        gbc.gridx = 1;
        txtFechaCompra = createTextField(12);
        formPanel.add(txtFechaCompra, gbc);
        row++;
        
        // Fecha Vencimiento
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Fecha Vencimiento:"), gbc);
        gbc.gridx = 1;
        txtFechaVencimiento = createTextField(12);
        formPanel.add(txtFechaVencimiento, gbc);
        row++;
        
        // Lote
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Lote:"), gbc);
        gbc.gridx = 1;
        txtLote = createTextField(15);
        formPanel.add(txtLote, gbc);
        row++;
        
        // Proveedor
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Proveedor:"), gbc);
        gbc.gridx = 1;
        txtProveedor = createTextField(25);
        formPanel.add(txtProveedor, gbc);
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
        scrollForm.setPreferredSize(new Dimension(520, 550));
        
        // Botones
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnPanel.setBackground(darkBg);
        
        btnGuardar = createDialogButton("Guardar", accentGreen);
        btnGuardar.addActionListener(e -> guardarInsumo());
        btnPanel.add(btnGuardar);
        
        btnCancelar = createDialogButton("Cancelar", accentRed);
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
    
    public void setInsumo(Insumo insumo) {
        this.insumo = insumo;
        if (insumo != null) {
            txtNombre.setText(insumo.getNombre());
            txtCodigo.setText(insumo.getCodigo());
            cbCategoria.setSelectedItem(insumo.getCategoria());
            if (insumo.getTipoInsumo() != null) {
                cbTipoInsumo.setSelectedItem(insumo.getTipoInsumo());
            }
            if (insumo.getPresentacion() != null) {
                cbPresentacion.setSelectedItem(insumo.getPresentacion());
            }
            txtStock.setText(String.valueOf(insumo.getStock()));
            txtStockMinimo.setText(String.valueOf(insumo.getStockMinimo()));
            txtStockMaximo.setText(insumo.getStockMaximo() > 0 ? String.valueOf(insumo.getStockMaximo()) : "");
            txtUbicacion.setText(insumo.getUbicacion() != null ? insumo.getUbicacion() : "");
            txtPrecioCompra.setText(String.format("%.2f", insumo.getPrecioCompra()));
            txtPrecioVenta.setText(String.format("%.2f", insumo.getPrecioVenta()));
            txtFechaCompra.setText(insumo.getFechaCompra() != null ? insumo.getFechaCompra() : "");
            txtFechaVencimiento.setText(insumo.getFechaVencimiento() != null ? insumo.getFechaVencimiento() : "");
            txtLote.setText(insumo.getLote() != null ? insumo.getLote() : "");
            txtProveedor.setText(insumo.getProveedor() != null ? insumo.getProveedor() : "");
            
            if (esEdicion && cbEstado != null) {
                cbEstado.setSelectedItem(insumo.getEstado() == 1 ? "Activo" : "Inactivo");
            }
        }
    }
    
    //--------------------------------Metodo ---------------------------------------//
    private void guardarInsumo() {
        try {
            String nombre = txtNombre.getText().trim();
            String codigo = txtCodigo.getText().trim();
            String categoria = (String) cbCategoria.getSelectedItem();
            
            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El nombre es obligatorio", "Error", JOptionPane.WARNING_MESSAGE);
                txtNombre.requestFocus();
                return;
            }
            
            if (codigo.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El código es obligatorio", "Error", JOptionPane.WARNING_MESSAGE);
                txtCodigo.requestFocus();
                return;
            }
            
            Insumo nuevoInsumo = new Insumo();
            nuevoInsumo.setId(insumo != null ? insumo.getId() : 0);
            nuevoInsumo.setNombre(nombre);
            nuevoInsumo.setCodigo(codigo);
            nuevoInsumo.setCategoria(categoria);
            nuevoInsumo.setTipoInsumo((String) cbTipoInsumo.getSelectedItem());
            nuevoInsumo.setPresentacion((String) cbPresentacion.getSelectedItem());
            
            try {
                nuevoInsumo.setStock(Integer.parseInt(txtStock.getText().trim()));
            } catch (NumberFormatException e) {
                nuevoInsumo.setStock(0);
            }
            
            try {
                nuevoInsumo.setStockMinimo(Integer.parseInt(txtStockMinimo.getText().trim()));
            } catch (NumberFormatException e) {
                nuevoInsumo.setStockMinimo(5);
            }
            
            try {
                String maxStr = txtStockMaximo.getText().trim();
                nuevoInsumo.setStockMaximo(maxStr.isEmpty() ? 0 : Integer.parseInt(maxStr));
            } catch (NumberFormatException e) {
                nuevoInsumo.setStockMaximo(0);
            }
            
            nuevoInsumo.setUbicacion(txtUbicacion.getText().trim().isEmpty() ? null : txtUbicacion.getText().trim());
            
            try {
                nuevoInsumo.setPrecioCompra(Double.parseDouble(txtPrecioCompra.getText().trim()));
            } catch (NumberFormatException e) {
                nuevoInsumo.setPrecioCompra(0);
            }
            
            try {
                nuevoInsumo.setPrecioVenta(Double.parseDouble(txtPrecioVenta.getText().trim()));
            } catch (NumberFormatException e) {
                nuevoInsumo.setPrecioVenta(0);
            }
            
            nuevoInsumo.setFechaCompra(txtFechaCompra.getText().trim().isEmpty() ? null : txtFechaCompra.getText().trim());
            nuevoInsumo.setFechaVencimiento(txtFechaVencimiento.getText().trim().isEmpty() ? null : txtFechaVencimiento.getText().trim());
            nuevoInsumo.setLote(txtLote.getText().trim().isEmpty() ? null : txtLote.getText().trim());
            nuevoInsumo.setProveedor(txtProveedor.getText().trim().isEmpty() ? null : txtProveedor.getText().trim());
            
            if (esEdicion && cbEstado != null) {
                nuevoInsumo.setEstado(cbEstado.getSelectedItem().equals("Activo") ? 1 : 0);
            } else {
                nuevoInsumo.setEstado(1);
            }
            
            if (controller.guardarInsumo(nuevoInsumo)) {
                guardado = true;
                dispose();
            }
            
        } catch (Exception e) {
            System.err.println("Error al guardar insumo: " + e.getMessage());
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