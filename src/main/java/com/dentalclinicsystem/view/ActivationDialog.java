package com.dentalclinicsystem.view;

import com.dentalclinicsystem.util.LicenseManager;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;


public class ActivationDialog extends JDialog {
    
    private JTextField txtLicencia;
    private JLabel lblHWID;
    private JLabel lblInfo;
    private boolean activado = false;
    
    public ActivationDialog(JFrame parent) {
        super(parent, "Activar Dental Clinic System", true);
        initComponents();
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        setSize(500, 400);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);
        
        //---------------------------------- TITULO ---------------------------------//
        JLabel titulo = new JLabel("ACTIVACIÓN DE LICENCIA");
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        titulo.setForeground(new Color(52, 73, 94));
        
        //------------------------------INSTRUCCIONES --------------------------//
        JTextArea instrucciones = new JTextArea();
        instrucciones.setText("Para activar Dental Clinic System:\n\n" +
                              "1. Copie el HWID de abajo\n" +
                              "2. Envíelo al desarrollador\n" +
                              "3. Recibirá un código de licencia\n" +
                              "4. Ingrese el código y presione ACTIVAR");
        instrucciones.setEditable(false);
        instrucciones.setBackground(new Color(240, 248, 255));
        instrucciones.setForeground(Color.BLACK);
        instrucciones.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        instrucciones.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        //-----------------------------------------------Panel HWID --------------------------------------//
        String hwid = LicenseManager.getCurrentHWID();
        String hwidFormateado = hwid.substring(0, 16) + "..." + hwid.substring(hwid.length() - 16);
        
        JPanel panelHWID = new JPanel(new BorderLayout(5, 5));
        panelHWID.setBorder(BorderFactory.createTitledBorder("HWID de su computadora"));
        
        lblHWID = new JLabel(hwidFormateado);
        lblHWID.setFont(new Font("Monospaced", Font.BOLD, 12));
        lblHWID.setForeground(new Color(46, 204, 113));
        lblHWID.setBackground(new Color(43, 43, 43));
        lblHWID.setOpaque(true);
        lblHWID.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        
        JButton btnCopiar = new JButton("Copiar HWID completo");
        btnCopiar.setBackground(new Color(52, 152, 219));
        btnCopiar.setForeground(Color.WHITE);
        btnCopiar.setFocusPainted(false);
        
        btnCopiar.addActionListener(e -> {
            StringSelection ss = new StringSelection(hwid);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
            JOptionPane.showMessageDialog(this, "HWID copiado");
        });
        
        panelHWID.add(lblHWID, BorderLayout.CENTER);
        panelHWID.add(btnCopiar, BorderLayout.EAST);
        
        // ------------------------------------------CAMPO LICENCIA----------------------------------//
        JPanel panelLicencia = new JPanel(new BorderLayout(5, 5));
        panelLicencia.setBorder(BorderFactory.createTitledBorder("Código de licencia"));
        
        txtLicencia = new JTextField();
        txtLicencia.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        panelLicencia.add(txtLicencia, BorderLayout.CENTER);
        
        //---------------------------------------- INFORMACION ------------------------------------ //
        lblInfo = new JLabel("Ingrese el código de licencia que recibió");
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblInfo.setForeground(Color.GRAY);
        
        // ------------------------------------------BOTONES -----------------------------------------//
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        JButton btnActivar = new JButton("ACTIVAR");
        btnActivar.setBackground(new Color(46, 204, 113));
        btnActivar.setForeground(Color.WHITE);
        btnActivar.setFont(new Font("Arial", Font.BOLD, 14));
        btnActivar.setFocusPainted(false);
        btnActivar.setPreferredSize(new Dimension(100, 50));
        
        JButton btnSalir = new JButton("SALIR");
        btnSalir.setBackground(new Color(231, 76, 60));
        btnSalir.setForeground(Color.WHITE);
        btnSalir.setFont(new Font("Arial", Font.BOLD, 14));
        btnSalir.setFocusPainted(false);
        btnSalir.setPreferredSize(new Dimension(100, 50));
        
        btnActivar.addActionListener(e -> activar());
        btnSalir.addActionListener(e -> System.exit(0));
        
        panelBotones.add(btnActivar);
        panelBotones.add(btnSalir);
        
        // --------------------------------------ENSAMBLAR ---------------------------------------//
        JPanel centerPanel = new JPanel(new GridLayout(3, 1, 10, 15));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(panelHWID);
        centerPanel.add(panelLicencia);
        centerPanel.add(lblInfo);
        
        mainPanel.add(titulo, BorderLayout.NORTH);
        mainPanel.add(instrucciones, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }
    
    private void activar() {
        String licencia = txtLicencia.getText().trim();
        
        if (licencia.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el código de licencia");
            return;
        }
        
        if (LicenseManager.activar(licencia)) {
            JOptionPane.showMessageDialog(this, "¡SOFTWARE ACTIVADO CORRECTAMENTE!");
            activado = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "CÓDIGO DE LICENCIA INVÁLIDO");
            txtLicencia.setText("");
        }
    }
    
    public boolean isActivado() {
        return activado;
    }
}