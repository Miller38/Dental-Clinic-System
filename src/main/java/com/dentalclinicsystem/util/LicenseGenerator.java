package com.dentalclinicsystem.util;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LicenseGenerator extends JFrame {
    
    private JTextField txtHWID;
    private JTextField txtLicencia;
    private JComboBox<String> cbDuracion;
    private JLabel lblFechaExpiracion;
    
    public LicenseGenerator() {
        setTitle("Generador de Licencias - Dental Clinic System");
        setSize(550, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Título
        JLabel titulo = new JLabel("GENERADOR DE LICENCIAS DENTAL-CLINIC-SYSTEM");
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        titulo.setForeground(new Color(52, 73, 94));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(titulo, gbc);
        
        // HWID de esta computadora (para copiar rápido)
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        panel.add(new JLabel("HWID de esta PC:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        JTextField txtMiHWID = new JTextField(HardwareUtils.getHWID(), 40);
        txtMiHWID.setEditable(false);
        txtMiHWID.setFont(new Font("Monospaced", Font.PLAIN, 10));
        panel.add(txtMiHWID, gbc);
        
        // Campo HWID del cliente
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("HWID del cliente:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        txtHWID = new JTextField(40);
        panel.add(txtHWID, gbc);
        
        // Duración
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Duración de licencia:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3;
        String[] duraciones = {"1 mes", "3 meses", "6 meses", "1 año", "2 años", "Permanente"};
        cbDuracion = new JComboBox<>(duraciones);
        panel.add(cbDuracion, gbc);
        
        // Fecha expiración
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Fecha expiración:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4;
        lblFechaExpiracion = new JLabel("--/--/----");
        lblFechaExpiracion.setFont(new Font("Monospaced", Font.BOLD, 12));
        lblFechaExpiracion.setForeground(new Color(231, 76, 60));
        panel.add(lblFechaExpiracion, gbc);
        
        // Actualizar fecha al cambiar duración
        cbDuracion.addActionListener(e -> actualizarFechaExpiracion());
        actualizarFechaExpiracion();
        
        // Botón generar
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        JButton btnGenerar = new JButton("GENERAR LICENCIA");
        btnGenerar.setBackground(new Color(46, 204, 113));
        btnGenerar.setForeground(Color.WHITE);
        btnGenerar.setFont(new Font("Arial", Font.BOLD, 14));
        btnGenerar.setFocusPainted(false);
        panel.add(btnGenerar, gbc);
        
        // Campo licencia
        gbc.gridx = 0; gbc.gridy = 6;
        panel.add(new JLabel("Licencia generada:"), gbc);
        gbc.gridx = 1; gbc.gridy = 6;
        txtLicencia = new JTextField(40);
        txtLicencia.setEditable(false);
        txtLicencia.setFont(new Font("Monospaced", Font.BOLD, 12));
        panel.add(txtLicencia, gbc);
        
        // Botón copiar licencia
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        JButton btnCopiar = new JButton("COPIAR LICENCIA");
        btnCopiar.setBackground(new Color(52, 152, 219));
        btnCopiar.setForeground(Color.WHITE);
        btnCopiar.setFocusPainted(false);
        panel.add(btnCopiar, gbc);
        
        // Botón copiar mensaje completo
        gbc.gridx = 0; gbc.gridy = 8;
        JButton btnCopiarMensaje = new JButton("COPIAR MENSAJE COMPLETO");
        btnCopiarMensaje.setBackground(new Color(155, 89, 182));
        btnCopiarMensaje.setForeground(Color.WHITE);
        btnCopiarMensaje.setFocusPainted(false);
        panel.add(btnCopiarMensaje, gbc);
        
        btnGenerar.addActionListener(e -> generarLicencia());
        btnCopiar.addActionListener(e -> copiarLicencia());
        btnCopiarMensaje.addActionListener(e -> copiarMensajeCompleto());
        
        add(panel);
    }
    
    private void actualizarFechaExpiracion() {
        LocalDate expiryDate = calcularFechaExpiracion();
        lblFechaExpiracion.setText(expiryDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }
    
    private LocalDate calcularFechaExpiracion() {
        String seleccion = (String) cbDuracion.getSelectedItem();
        LocalDate today = LocalDate.now();
        
        switch (seleccion) {
            case "1 mes": return today.plusMonths(1);
            case "3 meses": return today.plusMonths(3);
            case "6 meses": return today.plusMonths(6);
            case "1 año": return today.plusYears(1);
            case "2 años": return today.plusYears(2);
            default: return LocalDate.of(2099, 12, 31);
        }
    }
    
    private void generarLicencia() {
        String hwid = txtHWID.getText().trim();
        
        if (hwid.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el HWID del cliente");
            return;
        }
        
        if (hwid.length() < 16) {
            JOptionPane.showMessageDialog(this, "HWID inválido. Debe tener al menos 16 caracteres.");
            return;
        }
        
        LocalDate expiryDate = calcularFechaExpiracion();
        String licencia = LicenseManager.generarKey(hwid);
        
        txtLicencia.setText(licencia);
        
        JOptionPane.showMessageDialog(this, 
            "Licencia generada correctamente\n\n" +
            "Expira: " + expiryDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }
    
    private void copiarLicencia() {
        String licencia = txtLicencia.getText();
        if (licencia.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Primero genere una licencia");
            return;
        }
        
        StringSelection ss = new StringSelection(licencia);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
        JOptionPane.showMessageDialog(this, "Licencia copiada al portapapeles");
    }
    
    private void copiarMensajeCompleto() {
        String licencia = txtLicencia.getText();
        if (licencia.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Primero genere una licencia");
            return;
        }
        
        LocalDate expiryDate = calcularFechaExpiracion();
        String mensaje = String.format(
            "***************************************************************************\n" +
            "         CÓDIGO DE LICENCIA DENTAL-CLINIC-SYSTEM\n" +
            "**************************************************************************\n\n" +
            "Tu código de licencia es:\n\n" +
            "%s\n\n" +
            "📅 Fecha de expiración: %s\n\n" +
            "Instrucciones:\n" +
            "1. Copia el código completo\n" +
            "2. Pégalo en DentalClinicSystem\n" +
            "3. Presiona ACTIVAR\n\n" +
            "***************************************************************************",
            licencia, expiryDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        );
        
        StringSelection ss = new StringSelection(mensaje);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
        JOptionPane.showMessageDialog(this, "Mensaje completo copiado al portapapeles");
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}
        
        new LicenseGenerator().setVisible(true);
    }
}