package com.dentalclinicsystem.view;

import com.dentalclinicsystem.controller.InsumoController;
import com.dentalclinicsystem.model.Movimiento;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DialogMovimiento extends JDialog {
    
    private InsumoController controller;
    private int insumoId;
    private String nombreInsumo;
    private int stockActual;
    private boolean guardado = false;
    
    private JComboBox<String> cbTipoMovimiento;
    private JTextField txtCantidad;
    private JTextArea txtMotivo;
    private JButton btnGuardar, btnCancelar;
    private JLabel lblStockActual, lblNuevoStock;
    
    private Color darkBg = new Color(30, 30, 35);
    private Color textLight = new Color(220, 220, 230);
    private Color textGray = new Color(150, 150, 165);
    private Color accentGreen = new Color(60, 180, 110);
    private Color accentRed = new Color(210, 80, 80);
    private Color fieldBg = new Color(50, 50, 55);
    private Color fieldBorder = new Color(60, 60, 65);
    
    public DialogMovimiento(JFrame parent, int insumoId, String nombreInsumo, int stockActual) {
        super(parent, "Registrar Movimiento", true);
        this.controller = new InsumoController();
        this.insumoId = insumoId;
        this.nombreInsumo = nombreInsumo;
        this.stockActual = stockActual;
        initComponents();
        setSize(450, 400);
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
        
        // -----------------------------------------Título-----------------------------------//
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("Registrar Movimiento");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(textLight);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        formPanel.add(titleLabel, gbc);
        row++;
        gbc.gridwidth = 1;
        
        // ---------------------------------Información del insumo---------------------//
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JLabel infoLabel = new JLabel("Insumo: " + nombreInsumo);
        infoLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        infoLabel.setForeground(textLight);
        formPanel.add(infoLabel, gbc);
        row++;
        gbc.gridwidth = 1;
        
        // --------------------------------------Stock actual---------------------------//
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        lblStockActual = new JLabel("Stock actual: " + stockActual + " unidades");
        lblStockActual.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblStockActual.setForeground(textGray);
        formPanel.add(lblStockActual, gbc);
        row++;
        gbc.gridwidth = 1;
        
        // ------------------------------------Tipo de movimiento--------------------//
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Tipo de Movimiento *:"), gbc);
        gbc.gridx = 1;
        cbTipoMovimiento = new JComboBox<>(controller.getTiposMovimiento());
        cbTipoMovimiento.setBackground(fieldBg);
        cbTipoMovimiento.setForeground(textLight);
        cbTipoMovimiento.setBorder(BorderFactory.createLineBorder(fieldBorder));
        cbTipoMovimiento.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbTipoMovimiento.addActionListener(e -> actualizarNuevoStock());
        formPanel.add(cbTipoMovimiento, gbc);
        row++;
        
        //------------------------------------- Cantidad------------------------------//
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Cantidad *:"), gbc);
        gbc.gridx = 1;
        txtCantidad = new JTextField(8);
        txtCantidad.setText("1");
        txtCantidad.setBackground(fieldBg);
        txtCantidad.setForeground(textLight);
        txtCantidad.setBorder(BorderFactory.createLineBorder(fieldBorder));
        txtCantidad.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtCantidad.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                actualizarNuevoStock();
            }
        });
        formPanel.add(txtCantidad, gbc);
        row++;
        
        // -------------------------------Nuevo Stock (preview)----------------------//
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        lblNuevoStock = new JLabel("Nuevo stock: " + stockActual);
        lblNuevoStock.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblNuevoStock.setForeground(accentGreen);
        lblNuevoStock.setHorizontalAlignment(SwingConstants.CENTER);
        formPanel.add(lblNuevoStock, gbc);
        row++;
        gbc.gridwidth = 1;
        
        // --------------------------------------Motivo---------------------------------//
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Motivo:"), gbc);
        gbc.gridx = 1;
        txtMotivo = new JTextArea(3, 20);
        txtMotivo.setLineWrap(true);
        txtMotivo.setWrapStyleWord(true);
        txtMotivo.setBackground(fieldBg);
        txtMotivo.setForeground(textLight);
        txtMotivo.setCaretColor(textLight);
        txtMotivo.setBorder(BorderFactory.createLineBorder(fieldBorder));
        txtMotivo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JScrollPane scrollMotivo = new JScrollPane(txtMotivo);
        scrollMotivo.setBackground(fieldBg);
        scrollMotivo.setBorder(BorderFactory.createLineBorder(fieldBorder));
        formPanel.add(scrollMotivo, gbc);
        row++;
        
        JScrollPane scrollForm = new JScrollPane(formPanel);
        scrollForm.setBackground(darkBg);
        scrollForm.setBorder(BorderFactory.createEmptyBorder());
        scrollForm.getViewport().setBackground(darkBg);
        scrollForm.setPreferredSize(new Dimension(400, 300));
        
        // ---------------------------------Botones--------------------------------------//
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnPanel.setBackground(darkBg);
        
        btnGuardar = createDialogButton("Guardar", accentGreen);
        btnGuardar.addActionListener(e -> guardarMovimiento());
        btnPanel.add(btnGuardar);
        
        btnCancelar = createDialogButton("Cancelar", accentRed);
        btnCancelar.addActionListener(e -> dispose());
        btnPanel.add(btnCancelar);
        
        mainPanel.add(scrollForm, BorderLayout.CENTER);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        actualizarNuevoStock();
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
    //--------------------------------Metodo actualizar stock---------------------//
    private void actualizarNuevoStock() {
        try {
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());
            String tipo = (String) cbTipoMovimiento.getSelectedItem();
            
            int nuevoStock = stockActual;
            if (tipo.equals(Movimiento.TIPO_ENTRADA)) {
                nuevoStock = stockActual + cantidad;
                lblNuevoStock.setForeground(accentGreen);
            } else if (tipo.equals(Movimiento.TIPO_SALIDA) || tipo.equals(Movimiento.TIPO_VENCIDO)) {
                nuevoStock = stockActual - cantidad;
                lblNuevoStock.setForeground(nuevoStock < 0 ? accentRed : textLight);
            } else {
                nuevoStock = stockActual;
                lblNuevoStock.setForeground(textLight);
            }
            
            lblNuevoStock.setText("Nuevo stock: " + nuevoStock + " unidades");
            
        } catch (NumberFormatException e) {
            lblNuevoStock.setText("Nuevo stock: " + stockActual);
        }
    }
    
    //-------------------------------Metodo guardar movimiento----------------//
    private void guardarMovimiento() {
        try {
            String tipo = (String) cbTipoMovimiento.getSelectedItem();
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());
            String motivo = txtMotivo.getText().trim();
            
            if (cantidad <= 0) {
                JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor a 0", "Error", JOptionPane.WARNING_MESSAGE);
                txtCantidad.requestFocus();
                return;
            }
            
            if (tipo.equals(Movimiento.TIPO_SALIDA) || tipo.equals(Movimiento.TIPO_VENCIDO)) {
                if (cantidad > stockActual) {
                    JOptionPane.showMessageDialog(this, 
                        "Stock insuficiente. Disponible: " + stockActual, 
                        "Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
            
            boolean exito = controller.registrarMovimiento(insumoId, tipo, cantidad, motivo);
            if (exito) {
                guardado = true;
                dispose();
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese una cantidad válida", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    public boolean isGuardado() {
        return guardado;
    }
}