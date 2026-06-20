package com.dentalclinicsystem.view;

import com.dentalclinicsystem.service.ConfiguracionService;
import com.dentalclinicsystem.service.EncuestaService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class PanelEncuesta extends JPanel {
    
    private EncuestaService encuestaService;
    
    // Campos del formulario
    private JTextField txtNombre, txtEmail, txtTelefono, txtConsultorio;
    private JComboBox<String>[] cbRespuestas;
    private JTextArea txtComentarios, txtMejoras, txtCompetencia;
    private JLabel lblCalificacion;
    private JSlider sliderCalificacion;
    private JRadioButton[] rbRecomendacion;
    private ButtonGroup grupoRecomendacion;
    private JComboBox<String> cbPrecio;
    
    private JTextArea txtGusto, txtDisgusto;
    
    private String[] preguntasCompletas;
    private int totalPreguntas;
    private int contadorPreguntas = 0;
    
    // ===== TODAS LAS PREGUNTAS ORGANIZADAS POR SECCIÓN =====
    private String[] preguntasInstalacion = {
        "1. ¿La instalación fue fácil y rápida?",
        "2. ¿La configuración inicial fue clara?",
        "3. ¿El sistema inició correctamente?"
    };
    
    private String[] preguntasDiseno = {
        "4. ¿El diseño es atractivo y moderno?",
        "5. ¿Los colores y la interfaz son agradables?",
        "6. ¿La navegación es intuitiva?",
        "7. ¿Los botones y opciones son fáciles de encontrar?"
    };
    
    private String[] preguntasPacientes = {
        "8. ¿Es fácil registrar un nuevo paciente?",
        "9. ¿La búsqueda de pacientes funciona correctamente?",
        "10. ¿Es fácil editar la información de un paciente?"
    };
    
    private String[] preguntasCitas = {
        "11. ¿El calendario es fácil de usar?",
        "12. ¿Crear una cita es rápido e intuitivo?",
        "13. ¿Cambiar el estado de una cita es fácil?"
    };
    
    private String[] preguntasFinanzas = {
        "14. ¿Crear una venta/factura es rápido?",
        "15. ¿El cálculo de totales es correcto?",
        "16. ¿El envío de facturas por email funciona?"
    };
    
    private String[] preguntasReportes = {
        "17. ¿Los reportes son fáciles de entender?",
        "18. ¿Exportar a Excel es fácil?",
        "19. ¿Exportar a PDF es fácil?"
    };
    
    private String[] preguntasRendimiento = {
        "20. ¿El sistema es rápido al cargar datos?",
        "21. ¿Las búsquedas son rápidas?",
        "22. ¿La aplicación se siente fluida y responsiva?"
    };
    
    private String[] preguntasSeguridad = {
        "23. ¿El login es seguro?",
        "24. ¿Los backups automáticos funcionan?",
        "25. ¿Se pueden restaurar backups fácilmente?"
    };
    
    private String[] preguntasSoporte = {
        "26. ¿El sistema es fácil de aprender?",
        "27. ¿Los mensajes de error son claros?",
        "28. ¿Las ayudas visuales (tooltips) son útiles?"
    };
    
    private String[] opciones = {"Excelente (5)", "Bueno (4)", "Regular (3)", "Malo (2)", "Muy Malo (1)"};
    
    private Color darkBg = new Color(30, 30, 35);
    private Color darkCard = new Color(40, 40, 45);
    private Color textLight = new Color(220, 220, 230);
    private Color textGray = new Color(150, 150, 165);
    private Color accentGreen = new Color(60, 180, 110);
    private Color accentBlue = new Color(70, 130, 200);
    private Color accentRed = new Color(210, 80, 80);
    private Color accentOrange = new Color(230, 160, 50);
    private Color fieldBg = new Color(50, 50, 55);
    private Color fieldBorder = new Color(60, 60, 65);
    
    public PanelEncuesta() {
        this.encuestaService = new EncuestaService();
        combinarPreguntas();
        initComponents();
    }
    
    private void combinarPreguntas() {
        List<String> lista = new ArrayList<>();
        for (String p : preguntasInstalacion) lista.add(p);
        for (String p : preguntasDiseno) lista.add(p);
        for (String p : preguntasPacientes) lista.add(p);
        for (String p : preguntasCitas) lista.add(p);
        for (String p : preguntasFinanzas) lista.add(p);
        for (String p : preguntasReportes) lista.add(p);
        for (String p : preguntasRendimiento) lista.add(p);
        for (String p : preguntasSeguridad) lista.add(p);
        for (String p : preguntasSoporte) lista.add(p);
        preguntasCompletas = lista.toArray(new String[0]);
        totalPreguntas = preguntasCompletas.length;
        cbRespuestas = new JComboBox[totalPreguntas];
        System.out.println("📋 Total preguntas: " + totalPreguntas);
    }
    
    private void initComponents() {
        setBackground(darkBg);
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        JScrollPane centerPanel = createFormPanel();
        add(centerPanel, BorderLayout.CENTER);
        
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(darkBg);
        panel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(darkBg);
        
        JLabel titleLabel = new JLabel(" Encuesta de Satisfacción - Dental Clinic System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(textLight);
        titlePanel.add(titleLabel);
        
        panel.add(titlePanel, BorderLayout.NORTH);
        
        JLabel subLabel = new JLabel("Ayúdanos a mejorar calificando tu experiencia con el sistema");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subLabel.setForeground(textGray);
        subLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(subLabel, BorderLayout.CENTER);
        
        JLabel tiempoLabel = new JLabel("️ Tiempo estimado: 5-7 minutos |  La encuesta se enviará al administrador");
        tiempoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tiempoLabel.setForeground(accentOrange);
        tiempoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(tiempoLabel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JScrollPane createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(darkBg);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        int row = 0;
        
        // ===== SECCIÓN: DATOS DEL CLIENTE =====
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 4;
        panel.add(crearSeccion(" Datos del Cliente"), gbc);
        row++;
        gbc.gridwidth = 1;
        
        // Nombre
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(createLabel("Nombre completo *:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        txtNombre = createTextField(30);
        panel.add(txtNombre, gbc);
        row++;
        
        // Email
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 1;
        panel.add(createLabel("Email *:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        txtEmail = createTextField(30);
        panel.add(txtEmail, gbc);
        row++;
        
        // Teléfono
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(createLabel("Teléfono:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        txtTelefono = createTextField(30);
        panel.add(txtTelefono, gbc);
        row++;
        
        // Consultorio
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(createLabel("Nombre del consultorio:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        txtConsultorio = createTextField(30);
        panel.add(txtConsultorio, gbc);
        row++;
        
        // ===== SECCIÓN: INSTALACIÓN =====
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 4;
        panel.add(crearSeccion("️ Instalación y Configuración"), gbc);
        row++;
        gbc.gridwidth = 1;
        
        row = agregarPreguntas(panel, gbc, row, preguntasInstalacion);
        
        // ===== SECCIÓN: DISEÑO =====
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 4;
        panel.add(crearSeccion(" Diseño y Usabilidad"), gbc);
        row++;
        gbc.gridwidth = 1;
        
        row = agregarPreguntas(panel, gbc, row, preguntasDiseno);
        
        // ===== SECCIÓN: PACIENTES =====
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 4;
        panel.add(crearSeccion(" Módulo de Pacientes"), gbc);
        row++;
        gbc.gridwidth = 1;
        
        row = agregarPreguntas(panel, gbc, row, preguntasPacientes);
        
        // ===== SECCIÓN: CITAS =====
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 4;
        panel.add(crearSeccion(" Módulo de Citas"), gbc);
        row++;
        gbc.gridwidth = 1;
        
        row = agregarPreguntas(panel, gbc, row, preguntasCitas);
        
        // ===== SECCIÓN: FINANZAS =====
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 4;
        panel.add(crearSeccion(" Módulo de Finanzas"), gbc);
        row++;
        gbc.gridwidth = 1;
        
        row = agregarPreguntas(panel, gbc, row, preguntasFinanzas);
        
        // ===== SECCIÓN: REPORTES =====
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 4;
        panel.add(crearSeccion(" Módulo de Reportes"), gbc);
        row++;
        gbc.gridwidth = 1;
        
        row = agregarPreguntas(panel, gbc, row, preguntasReportes);
        
        // ===== SECCIÓN: RENDIMIENTO =====
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 4;
        panel.add(crearSeccion(" Rendimiento"), gbc);
        row++;
        gbc.gridwidth = 1;
        
        row = agregarPreguntas(panel, gbc, row, preguntasRendimiento);
        
        // ===== SECCIÓN: SEGURIDAD =====
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 4;
        panel.add(crearSeccion(" Seguridad"), gbc);
        row++;
        gbc.gridwidth = 1;
        
        row = agregarPreguntas(panel, gbc, row, preguntasSeguridad);
        
        // ===== SECCIÓN: SOPORTE =====
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 4;
        panel.add(crearSeccion("📞 Soporte y Documentación"), gbc);
        row++;
        gbc.gridwidth = 1;
        
        row = agregarPreguntas(panel, gbc, row, preguntasSoporte);
        
        // ===== CALIFICACIÓN GENERAL =====
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 4;
        panel.add(crearSeccion(" Calificación General"), gbc);
        row++;
        gbc.gridwidth = 1;
        
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        panel.add(createLabel("Califica el sistema en general:"), gbc);
        
        gbc.gridx = 2; gbc.gridwidth = 2;
        JPanel sliderPanel = new JPanel(new BorderLayout(10, 0));
        sliderPanel.setBackground(darkBg);
        
        sliderCalificacion = new JSlider(1, 5, 3);
        sliderCalificacion.setBackground(darkBg);
        sliderCalificacion.setForeground(textLight);
        sliderCalificacion.setMajorTickSpacing(1);
        sliderCalificacion.setPaintTicks(true);
        sliderCalificacion.setPaintLabels(true);
        sliderCalificacion.setSnapToTicks(true);
        sliderCalificacion.addChangeListener(e -> {
            int valor = sliderCalificacion.getValue();
            String texto = "";
            switch (valor) {
                case 1: texto = "⭐ Muy Malo"; break;
                case 2: texto = "⭐⭐ Malo"; break;
                case 3: texto = "⭐⭐⭐ Regular"; break;
                case 4: texto = "⭐⭐⭐⭐ Bueno"; break;
                case 5: texto = "⭐⭐⭐⭐⭐ Excelente"; break;
            }
            lblCalificacion.setText("Calificación: " + texto);
        });
        
        lblCalificacion = new JLabel("Calificación: ⭐⭐⭐ Regular");
        lblCalificacion.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblCalificacion.setForeground(accentOrange);
        lblCalificacion.setHorizontalAlignment(SwingConstants.CENTER);
        
        sliderPanel.add(sliderCalificacion, BorderLayout.CENTER);
        sliderPanel.add(lblCalificacion, BorderLayout.SOUTH);
        
        panel.add(sliderPanel, gbc);
        row++;
        
        // ===== PREGUNTAS ABIERTAS =====
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 4;
        panel.add(crearSeccion("💬 Preguntas Abiertas"), gbc);
        row++;
        gbc.gridwidth = 1;
        
        // Recomendación
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 4;
        panel.add(createLabel("¿Qué tan probable es que recomiendes este software a otros odontólogos?"), gbc);
        row++;
        
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 4;
        JPanel recomendacionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        recomendacionPanel.setBackground(darkBg);
        
        grupoRecomendacion = new ButtonGroup();
        String[] recomendaciones = {"Muy probable", "Probable", "Neutral", "Poco probable", "Nada probable"};
        rbRecomendacion = new JRadioButton[recomendaciones.length];
        for (int i = 0; i < recomendaciones.length; i++) {
            rbRecomendacion[i] = new JRadioButton(recomendaciones[i]);
            rbRecomendacion[i].setBackground(darkBg);
            rbRecomendacion[i].setForeground(textLight);
            rbRecomendacion[i].setFont(new Font("Segoe UI", Font.PLAIN, 12));
            rbRecomendacion[i].setFocusPainted(false);
            grupoRecomendacion.add(rbRecomendacion[i]);
            recomendacionPanel.add(rbRecomendacion[i]);
        }
        panel.add(recomendacionPanel, gbc);
        row++;
        
        // Precio
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(createLabel("¿Cuánto estarías dispuesto a pagar por el software?"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        cbPrecio = new JComboBox<>(new String[]{
            "Seleccionar...",
            "$100.000 - $200.000 COP/mes",
            "$200.000 - $300.000 COP/mes",
            "$300.000 - $400.000 COP/mes",
            "$400.000 - $500.000 COP/mes",
            "Más de $500.000 COP/mes",
            "Prefiero licencia perpetua"
        });
        cbPrecio.setBackground(fieldBg);
        cbPrecio.setForeground(textLight);
        cbPrecio.setBorder(BorderFactory.createLineBorder(fieldBorder));
        cbPrecio.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(cbPrecio, gbc);
        row++;
        gbc.gridwidth = 1;
        
        // Lo que más gustó
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 4;
        panel.add(createLabel("✅ ¿Qué fue lo que más te gustó del sistema?"), gbc);
        row++;
        
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 0.3;
        txtGusto = new JTextArea(3, 30);
        txtGusto.setLineWrap(true);
        txtGusto.setWrapStyleWord(true);
        txtGusto.setBackground(fieldBg);
        txtGusto.setForeground(textLight);
        txtGusto.setCaretColor(textLight);
        txtGusto.setBorder(BorderFactory.createLineBorder(fieldBorder));
        txtGusto.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JScrollPane scrollGusto = new JScrollPane(txtGusto);
        scrollGusto.setBackground(fieldBg);
        scrollGusto.setBorder(BorderFactory.createLineBorder(fieldBorder));
        panel.add(scrollGusto, gbc);
        row++;
        
        // Lo que menos gustó
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        panel.add(createLabel("❌ ¿Qué fue lo que menos te gustó?"), gbc);
        row++;
        
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 0.3;
        txtDisgusto = new JTextArea(3, 30);
        txtDisgusto.setLineWrap(true);
        txtDisgusto.setWrapStyleWord(true);
        txtDisgusto.setBackground(fieldBg);
        txtDisgusto.setForeground(textLight);
        txtDisgusto.setCaretColor(textLight);
        txtDisgusto.setBorder(BorderFactory.createLineBorder(fieldBorder));
        txtDisgusto.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JScrollPane scrollDisgusto = new JScrollPane(txtDisgusto);
        scrollDisgusto.setBackground(fieldBg);
        scrollDisgusto.setBorder(BorderFactory.createLineBorder(fieldBorder));
        panel.add(scrollDisgusto, gbc);
        row++;
        
        // Mejoras sugeridas
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        panel.add(createLabel(" ¿Qué funcionalidad agregarías o mejorarías?"), gbc);
        row++;
        
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 0.3;
        txtMejoras = new JTextArea(3, 30);
        txtMejoras.setLineWrap(true);
        txtMejoras.setWrapStyleWord(true);
        txtMejoras.setBackground(fieldBg);
        txtMejoras.setForeground(textLight);
        txtMejoras.setCaretColor(textLight);
        txtMejoras.setBorder(BorderFactory.createLineBorder(fieldBorder));
        txtMejoras.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JScrollPane scrollMejoras = new JScrollPane(txtMejoras);
        scrollMejoras.setBackground(fieldBg);
        scrollMejoras.setBorder(BorderFactory.createLineBorder(fieldBorder));
        panel.add(scrollMejoras, gbc);
        row++;
        
        // Competencia
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        panel.add(createLabel(" ¿Qué otro software has usado y cómo lo comparas con este?"), gbc);
        row++;
        
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 0.3;
        txtCompetencia = new JTextArea(3, 30);
        txtCompetencia.setLineWrap(true);
        txtCompetencia.setWrapStyleWord(true);
        txtCompetencia.setBackground(fieldBg);
        txtCompetencia.setForeground(textLight);
        txtCompetencia.setCaretColor(textLight);
        txtCompetencia.setBorder(BorderFactory.createLineBorder(fieldBorder));
        txtCompetencia.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JScrollPane scrollCompetencia = new JScrollPane(txtCompetencia);
        scrollCompetencia.setBackground(fieldBg);
        scrollCompetencia.setBorder(BorderFactory.createLineBorder(fieldBorder));
        panel.add(scrollCompetencia, gbc);
        row++;
        
        // Comentarios adicionales
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        panel.add(createLabel(" Comentarios y sugerencias adicionales:"), gbc);
        row++;
        
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 0.3;
        txtComentarios = new JTextArea(3, 30);
        txtComentarios.setLineWrap(true);
        txtComentarios.setWrapStyleWord(true);
        txtComentarios.setBackground(fieldBg);
        txtComentarios.setForeground(textLight);
        txtComentarios.setCaretColor(textLight);
        txtComentarios.setBorder(BorderFactory.createLineBorder(fieldBorder));
        txtComentarios.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JScrollPane scrollComentarios = new JScrollPane(txtComentarios);
        scrollComentarios.setBackground(fieldBg);
        scrollComentarios.setBorder(BorderFactory.createLineBorder(fieldBorder));
        panel.add(scrollComentarios, gbc);
        
        JScrollPane scrollForm = new JScrollPane(panel);
        scrollForm.setBackground(darkBg);
        scrollForm.setBorder(BorderFactory.createEmptyBorder());
        scrollForm.getViewport().setBackground(darkBg);
        
        return scrollForm;
    }
    
    private JPanel crearSeccion(String titulo) {
        JPanel panel = new JPanel();
        panel.setBackground(darkCard);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 65)),
            titulo,
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            accentBlue
        ));
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        panel.setPreferredSize(new Dimension(0, 35));
        return panel;
    }
    
    private int agregarPreguntas(JPanel panel, GridBagConstraints gbc, int row, String[] preguntas) {
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        
        for (int i = 0; i < preguntas.length; i++) {
            gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
            panel.add(createLabel(preguntas[i]), gbc);
            
            gbc.gridx = 2; gbc.gridwidth = 2;
            cbRespuestas[contadorPreguntas] = createComboBox(opciones);
            panel.add(cbRespuestas[contadorPreguntas], gbc);
            contadorPreguntas++;
            row++;
        }
        return row;
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
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
    
    private JComboBox<String> createComboBox(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setBackground(fieldBg);
        combo.setForeground(textLight);
        combo.setBorder(BorderFactory.createLineBorder(fieldBorder));
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return combo;
    }
    
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBackground(darkBg);
        panel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        JButton btnEnviar = new JButton(" Enviar Encuesta");
        btnEnviar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnEnviar.setBackground(accentGreen);
        btnEnviar.setForeground(Color.WHITE);
        btnEnviar.setBorderPainted(false);
        btnEnviar.setFocusPainted(false);
        btnEnviar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEnviar.setPreferredSize(new Dimension(180, 45));
        btnEnviar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enviarEncuesta();
            }
        });
        panel.add(btnEnviar);
        
        JButton btnLimpiar = new JButton(" Limpiar");
        btnLimpiar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnLimpiar.setBackground(accentRed);
        btnLimpiar.setForeground(Color.WHITE);
        btnLimpiar.setBorderPainted(false);
        btnLimpiar.setFocusPainted(false);
        btnLimpiar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLimpiar.setPreferredSize(new Dimension(120, 45));
        btnLimpiar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limpiarFormulario();
            }
        });
        panel.add(btnLimpiar);
        
        return panel;
    }
    
    /**
     * Envía la encuesta - VERSIÓN CORREGIDA Y DEFINITIVA
     */
    private void enviarEncuesta() {
        // Validar campos obligatorios
        String nombre = txtNombre.getText().trim();
        String email = txtEmail.getText().trim();
        
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor ingresa tu nombre", "Campo requerido", JOptionPane.WARNING_MESSAGE);
            txtNombre.requestFocus();
            return;
        }
        
        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor ingresa tu email", "Campo requerido", JOptionPane.WARNING_MESSAGE);
            txtEmail.requestFocus();
            return;
        }
        
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(this, "Por favor ingresa un email válido", "Error", JOptionPane.ERROR_MESSAGE);
            txtEmail.requestFocus();
            return;
        }
        
        // Recolectar respuestas
        String[] respuestas = new String[cbRespuestas.length];
        for (int i = 0; i < cbRespuestas.length; i++) {
            if (cbRespuestas[i] != null) {
                respuestas[i] = (String) cbRespuestas[i].getSelectedItem();
            } else {
                respuestas[i] = "No respondida";
            }
        }
        
        double calificacion = sliderCalificacion.getValue();
        String comentarios = txtComentarios.getText().trim();
        String mejoras = txtMejoras.getText().trim();
        String competencia = txtCompetencia.getText().trim();
        String consultorio = txtConsultorio.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String gusto = txtGusto.getText().trim();
        String disgusto = txtDisgusto.getText().trim();
        
        // Obtener recomendación
        String recomendacion = "";
        for (int i = 0; i < rbRecomendacion.length; i++) {
            if (rbRecomendacion[i].isSelected()) {
                recomendacion = rbRecomendacion[i].getText();
                break;
            }
        }
        
        String precio = (String) cbPrecio.getSelectedItem();
        
        // ============================================================
        // 🔥 OBTENER EMAIL DESTINO DE FORMA SEGURA (NUNCA NULL)
        // ============================================================
        String emailDestino = obtenerEmailDestinoSeguro();
        
        // Mostrar resumen
        StringBuilder resumen = new StringBuilder();
        resumen.append("📋 RESUMEN DE LA ENCUESTA\n\n");
        resumen.append("Nombre: ").append(nombre).append("\n");
        resumen.append("Email: ").append(email).append("\n");
        resumen.append("Calificación: ").append(calificacion).append("/5\n");
        resumen.append("Recomendación: ").append(recomendacion).append("\n");
        resumen.append("Precio sugerido: ").append(precio).append("\n\n");
        resumen.append("La encuesta se enviará a:\n");
        resumen.append("📧 ").append(emailDestino);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            resumen.toString(),
            "Confirmar envío de encuesta",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            String comentariosCompletos = "✅ Lo que más gustó:\n" + gusto + "\n\n❌ Lo que menos gustó:\n" + disgusto;
            
            if (respuestas.length != preguntasCompletas.length) {
                JOptionPane.showMessageDialog(this, 
                    "Error interno: Las respuestas no coinciden con las preguntas",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            encuestaService.enviarEncuesta(
                nombre, 
                email, 
                telefono, 
                consultorio,
                respuestas, 
                preguntasCompletas,
                calificacion, 
                comentariosCompletos, 
                mejoras, 
                competencia,
                recomendacion, 
                precio
            );
            limpiarFormulario();
        }
    }
    
    /**
     * Obtiene el email destino de forma segura (nunca retorna null)
     */
    private String obtenerEmailDestinoSeguro() {
        try {
            ConfiguracionService config = ConfiguracionService.getInstance();
            String email = config.getEmailDestino();
            
            if (email != null && !email.isEmpty() && !email.equals("null")) {
                return email;
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error obteniendo email destino: " + e.getMessage());
        }
        
        // Valor por defecto seguro
        return "admin@dentalclinic.com";
    }
    
    private void limpiarFormulario() {
        txtNombre.setText("");
        txtEmail.setText("");
        txtTelefono.setText("");
        txtConsultorio.setText("");
        for (JComboBox<String> cb : cbRespuestas) {
            if (cb != null) {
                cb.setSelectedIndex(0);
            }
        }
        sliderCalificacion.setValue(3);
        txtComentarios.setText("");
        txtMejoras.setText("");
        txtCompetencia.setText("");
        txtGusto.setText("");
        txtDisgusto.setText("");
        lblCalificacion.setText("Calificación: ⭐⭐⭐ Regular");
        grupoRecomendacion.clearSelection();
        cbPrecio.setSelectedIndex(0);
    }
}