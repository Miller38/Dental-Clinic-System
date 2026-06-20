package com.dentalclinicsystem.view;

import com.dentalclinicsystem.controller.ConfiguracionController;
import com.dentalclinicsystem.controller.PacienteController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

public class PanelConfiguracion extends JPanel {
    
    private ConfiguracionController controller;
    private JFrame parentFrame;
    
    // Componentes de tema
    private JRadioButton rbtnOscuro;
    private JRadioButton rbtnClaro;
    private ButtonGroup grupoTema;
    
    // Componentes de fuente
    private JSlider sliderFuente;
    private JLabel lblTamañoFuente;
    private JLabel lblPreview;
    
    // Componentes de información
    private JLabel lblVersion;
    private JLabel lblPacientes;
    private JLabel lblCitas;
    private JLabel lblEstado;
    
    private Color darkBg = new Color(30, 30, 35);
    private Color darkCard = new Color(40, 40, 45);
    private Color textLight = new Color(220, 220, 230);
    private Color textGray = new Color(150, 150, 165);
    private Color accentBlue = new Color(70, 130, 200);
    private Color accentGreen = new Color(60, 180, 110);
    
    public PanelConfiguracion(JFrame parent) {
        this.parentFrame = parent;
        this.controller = new ConfiguracionController();
        initComponents();
        cargarConfiguracion();
        cargarInformacion();
    }
    
    private void initComponents() {
        setBackground(darkBg);
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(darkBg);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        int row = 0;
        
        // Título
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JLabel titleLabel = new JLabel("️ Configuración del Sistema");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(textLight);
        mainPanel.add(titleLabel, gbc);
        row++;
        
        // Panel de Tema (Apariencia)
        gbc.gridy = row; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel temaPanel = crearPanelTema();
        mainPanel.add(temaPanel, gbc);
        row++;
        
        // Panel de Fuente
        gbc.gridy = row; gbc.gridwidth = 2;
        JPanel fuentePanel = crearPanelFuente();
        mainPanel.add(fuentePanel, gbc);
        row++;
        
        // Panel de Información del Sistema
        gbc.gridy = row; gbc.gridwidth = 2;
        JPanel infoPanel = crearPanelInformacion();
        mainPanel.add(infoPanel, gbc);
        row++;
        
        // Panel de Acciones
        gbc.gridy = row; gbc.gridwidth = 2;
        JPanel accionesPanel = crearPanelAcciones();
        mainPanel.add(accionesPanel, gbc);
        
        JScrollPane scroll = new JScrollPane(mainPanel);
        scroll.setBackground(darkBg);
        scroll.getViewport().setBackground(darkBg);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        
        add(scroll, BorderLayout.CENTER);
    }
    
    private JPanel crearPanelTema() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(darkCard);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(50, 50, 55), 1),
            new TitledBorder(new EmptyBorder(15, 15, 15, 15), " Tema de Apariencia", 
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), textLight)
        ));
        panel.setPreferredSize(new Dimension(400, 120));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        grupoTema = new ButtonGroup();
        
        rbtnOscuro = new JRadioButton(" Oscuro");
        rbtnOscuro.setBackground(darkCard);
        rbtnOscuro.setForeground(textLight);
        rbtnOscuro.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        rbtnOscuro.setFocusPainted(false);
        grupoTema.add(rbtnOscuro);
        
        rbtnClaro = new JRadioButton("️ Claro");
        rbtnClaro.setBackground(darkCard);
        rbtnClaro.setForeground(textLight);
        rbtnClaro.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        rbtnClaro.setFocusPainted(false);
        grupoTema.add(rbtnClaro);
        
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 5));
        radioPanel.setBackground(darkCard);
        radioPanel.add(rbtnOscuro);
        radioPanel.add(rbtnClaro);
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(radioPanel, gbc);
        
        // Acción al cambiar tema
        rbtnOscuro.addActionListener(e -> {
            controller.setTema(ConfiguracionController.TEMA_OSCURO);
            System.out.println("Tema seleccionado: Oscuro");
        });
        rbtnClaro.addActionListener(e -> {
            controller.setTema(ConfiguracionController.TEMA_CLARO);
            System.out.println("Tema seleccionado: Claro");
        });
        
        return panel;
    }
    
    private JPanel crearPanelFuente() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(darkCard);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(50, 50, 55), 1),
            new TitledBorder(new EmptyBorder(15, 15, 15, 15), " Tamaño de Fuente", 
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), textLight)
        ));
        panel.setPreferredSize(new Dimension(400, 150));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Slider
        sliderFuente = new JSlider(10, 24, 14);
        sliderFuente.setBackground(darkCard);
        sliderFuente.setForeground(textLight);
        sliderFuente.setMajorTickSpacing(2);
        sliderFuente.setPaintTicks(true);
        sliderFuente.setPaintLabels(true);
        sliderFuente.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        sliderFuente.addChangeListener(e -> {
            int size = sliderFuente.getValue();
            lblTamañoFuente.setText("Tamaño: " + size + "px");
            lblPreview.setFont(new Font("Segoe UI", Font.PLAIN, size));
        });
        
        // Label de tamaño
        JPanel sizePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        sizePanel.setBackground(darkCard);
        
        lblTamañoFuente = new JLabel("Tamaño: 14px");
        lblTamañoFuente.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTamañoFuente.setForeground(textLight);
        sizePanel.add(lblTamañoFuente);
        
        // Preview
        JPanel previewPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        previewPanel.setBackground(darkCard);
        previewPanel.setBorder(BorderFactory.createLineBorder(new Color(50, 50, 55)));
        previewPanel.setPreferredSize(new Dimension(350, 50));
        
        lblPreview = new JLabel("Texto de ejemplo - AaBbCc 123");
        lblPreview.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblPreview.setForeground(textLight);
        previewPanel.add(lblPreview);
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(sliderFuente, gbc);
        
        gbc.gridy = 1;
        panel.add(sizePanel, gbc);
        
        gbc.gridy = 2;
        panel.add(previewPanel, gbc);
        
        return panel;
    }
    
    private JPanel crearPanelInformacion() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(darkCard);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(50, 50, 55), 1),
            new TitledBorder(new EmptyBorder(15, 15, 15, 15), "️ Información del Sistema", 
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), textLight)
        ));
        panel.setPreferredSize(new Dimension(400, 130));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 10, 3, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Datos de información
        lblVersion = new JLabel(" Versión: 2.0.0");
        lblVersion.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblVersion.setForeground(textGray);
        
        lblPacientes = new JLabel(" Total Pacientes: Cargando...");
        lblPacientes.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblPacientes.setForeground(textGray);
        
        lblCitas = new JLabel(" Citas Hoy: Cargando...");
        lblCitas.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblCitas.setForeground(textGray);
        
        lblEstado = new JLabel(" Estado: Activo");
        lblEstado.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblEstado.setForeground(new Color(60, 180, 110));
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(lblVersion, gbc);
        gbc.gridy = 1;
        panel.add(lblPacientes, gbc);
        gbc.gridy = 2;
        panel.add(lblCitas, gbc);
        gbc.gridy = 3;
        panel.add(lblEstado, gbc);
        
        return panel;
    }
    
    private JPanel crearPanelAcciones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBackground(darkBg);
        
        JButton btnGuardar = createActionButton(" Guardar Configuración", accentGreen);
        btnGuardar.addActionListener(e -> guardarConfiguracion());
        
        JButton btnAplicar = createActionButton(" Aplicar Cambios", new Color(150, 80, 200));
        btnAplicar.addActionListener(e -> aplicarCambios());
        
        JButton btnRestaurar = createActionButton("️ Restaurar Predeterminados", accentBlue);
        btnRestaurar.addActionListener(e -> restaurarPredeterminados());
        
        panel.add(btnGuardar);
        panel.add(btnAplicar);
        panel.add(btnRestaurar);
        
        return panel;
    }
    
    private JButton createActionButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(200, 40));
        
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(color.brighter());
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(color);
            }
        });
        
        return btn;
    }
    
    private void cargarConfiguracion() {
        try {
            String tema = controller.getTema();
            if (ConfiguracionController.TEMA_CLARO.equals(tema)) {
                rbtnClaro.setSelected(true);
            } else {
                rbtnOscuro.setSelected(true);
            }
            
            int tamaño = controller.getTamañoFuente();
            sliderFuente.setValue(tamaño);
            lblTamañoFuente.setText("Tamaño: " + tamaño + "px");
            lblPreview.setFont(new Font("Segoe UI", Font.PLAIN, tamaño));
            
        } catch (Exception e) {
            System.err.println("Error al cargar configuración: " + e.getMessage());
        }
    }
    
    private void cargarInformacion() {
        try {
            PacienteController pacienteController = new PacienteController();
            int totalPacientes = pacienteController.contarPacientes();
            lblPacientes.setText(" Total Pacientes: " + totalPacientes);
            
            // Aquí podrías agregar citas de hoy
            // CitaDAO citaDAO = new CitaDAO();
            // int citasHoy = citaDAO.contarCitasHoy();
            // lblCitas.setText("🔹 Citas Hoy: " + citasHoy);
            
        } catch (Exception e) {
            System.err.println("Error al cargar información: " + e.getMessage());
        }
    }
    
    private void guardarConfiguracion() {
        try {
            // Guardar tamaño de fuente
            int tamaño = sliderFuente.getValue();
            controller.setTamañoFuente(tamaño);
            
            // El tema ya se guardó al seleccionar el radio button
            
            JOptionPane.showMessageDialog(this,
                " Configuración guardada correctamente",
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al guardar configuración: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void aplicarCambios() {
    try {
        // Guardar configuración
        int tamaño = sliderFuente.getValue();
        controller.setTamañoFuente(tamaño);
        
        // Aplicar cambios al sistema
        if (parentFrame != null) {
            // Aplicar configuración global
            controller.aplicarConfiguracionGlobal(parentFrame);
            
            // Si el frame es DashboardModerno2, recrear contenido
            if (parentFrame instanceof DashboardModerno2) {
                ((DashboardModerno2) parentFrame).recrearContenido();
            }
            
            JOptionPane.showMessageDialog(this,
                "✅ Configuración aplicada correctamente.\n" +
                "Todos los componentes han sido actualizados.",
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }
        
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this,
            "Error al aplicar configuración: " + e.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
    }
}
    
    private void restaurarPredeterminados() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de restaurar la configuración predeterminada?\n" +
            "Tema: Oscuro\n" +
            "Tamaño de fuente: 14px",
            "Restaurar predeterminados",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Restaurar valores predeterminados
                controller.setTema(ConfiguracionController.TEMA_OSCURO);
                controller.setTamañoFuente(14);
                
                // Actualizar UI
                cargarConfiguracion();
                
                JOptionPane.showMessageDialog(this,
                    " Configuración restaurada a valores predeterminados\n" +
                    "Aplique los cambios para verlos reflejados.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error al restaurar configuración: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void reiniciarAplicacion() {
        try {
            System.out.println("🔄 Reiniciando aplicación...");
            
            // Método 1: Reiniciar usando la misma JVM
            String javaHome = System.getProperty("java.home");
            String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
            String classpath = System.getProperty("java.class.path");
            String className = System.getProperty("sun.java.command");
            
            // Si estamos ejecutando desde un JAR
            if (className != null && className.endsWith(".jar")) {
                String jarPath = className;
                ProcessBuilder processBuilder = new ProcessBuilder(javaBin, "-jar", jarPath);
                processBuilder.directory(new File(System.getProperty("user.dir")));
                processBuilder.start();
                System.exit(0);
            } else {
                // Si estamos ejecutando desde IDE
                ProcessBuilder processBuilder = new ProcessBuilder(javaBin, "-cp", classpath, "com.dentalclinicsystem.main.Main");
                processBuilder.directory(new File(System.getProperty("user.dir")));
                processBuilder.start();
                System.exit(0);
            }
            
        } catch (IOException e) {
            System.err.println("Error al reiniciar: " + e.getMessage());
            e.printStackTrace();
            
            // Método alternativo: sugerir reinicio manual
            JOptionPane.showMessageDialog(this,
                "No se pudo reiniciar automáticamente.\n" +
                "Por favor, cierre y vuelva a abrir la aplicación manualmente.",
                "Reinicio manual", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}