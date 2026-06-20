package com.dentalclinicsystem.view;

import com.dentalclinicsystem.controller.CitaController;
import com.dentalclinicsystem.controller.NotificacionController;
import com.dentalclinicsystem.controller.PacienteController;
import com.dentalclinicsystem.controller.ServicioController;
import com.dentalclinicsystem.controller.VentaController;
import com.dentalclinicsystem.dao.Session;
import com.dentalclinicsystem.model.Cita;
import com.dentalclinicsystem.model.Usuario;


import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DashboardModerno2 extends JFrame {
    private JPanel mainContentPanel;
    private JPanel sidebarPanel;
    private JPanel mainPanel;
    private JButton btnDashboard, btnPacientes, btnCitas, btnServicios;
    private JButton btnVentas, btnInventario, btnUsuarios, btnSettings, btnLogout;
    private JButton btnReportes, btnBackup, btnEncuesta;
    
    //--------------------------- Componentes de estadísticas-----------------------------------//
    private JLabel lblCitasHoy, lblPacientes, lblIngresos, lblTratamientos, lblPendientes, lblSatisfaccion;
    private JLabel lblCitasSemana, lblCompletadas, lblCanceladas, lblNoAsistio;
    private JLabel lblUsuarioNombre, lblUsuarioRol, lblUsuarioAvatar, lblActualizacion;
    private JPanel cardPacientes, cardCitasHoy, cardPendientes, cardIngresos, cardCompletadas, cardSemana;
    
    // ----------------------------Progress bars para distribución--------------------------------//
    private JProgressBar progressCompletadas, progressCanceladas, progressNoAsistio, progressPendientes;
    private JLabel lblPorcCompletadas, lblPorcCanceladas, lblPorcNoAsistio, lblPorcPendientes;
    
    private PacienteController pacienteController;
    private CitaController citaController;
    private ServicioController servicioController;
    private VentaController ventaController;
    private Timer timerActualizacion;
    
    private String panelActual = "Dashboard";
    
    private Color darkBg = new Color(30, 30, 35);
    private Color darkCard = new Color(40, 40, 45);
    private Color darkSidebar = new Color(25, 25, 30);
    private Color textLight = new Color(220, 220, 230);
    private Color textGray = new Color(150, 150, 165);
    
    private Color blueColor = new Color(70, 130, 200);
    private Color greenColor = new Color(60, 180, 110);
    private Color orangeColor = new Color(230, 160, 50);
    private Color redColor = new Color(210, 80, 80);
    private Color purpleColor = new Color(150, 80, 200);
    private Color tealColor = new Color(50, 180, 180);
    private Color pinkColor = new Color(200, 100, 150);
    
    private Color blueHover = new Color(90, 160, 230);
    private Color greenHover = new Color(80, 210, 130);
    private Color orangeHover = new Color(255, 190, 70);
    private Color redHover = new Color(235, 110, 110);
    private Color purpleHover = new Color(180, 110, 230);
    private Color tealHover = new Color(70, 210, 210);
    
    public DashboardModerno2() {
        this.pacienteController = new PacienteController();
        this.citaController = new CitaController();
        this.servicioController = new ServicioController();
        this.ventaController = new VentaController();
        crearDirectorios();
        initComponents();
        setTitle("Dental Clinic System- Dashboard Dark Mode");
        setSize(1400, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        
        iniciarActualizacionAutomatica();
        mostrarUsuarioActual();
    }
    
    private void crearDirectorios() {
        File uploadDir = new File("uploads/pacientes/");
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
    }
    
    private void initComponents() {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(darkBg);
        setContentPane(mainPanel);
        
        createDarkSidebar();
        mainPanel.add(sidebarPanel, BorderLayout.WEST);
        
        mainContentPanel = createDashboardContent();
        mainPanel.add(mainContentPanel, BorderLayout.CENTER);
    }
    
    private void iniciarActualizacionAutomatica() {
        timerActualizacion = new Timer(30000, e -> {
            if (panelActual.equals("Dashboard")) {
                actualizarEstadisticas();
                if (lblActualizacion != null) {
                    lblActualizacion.setText("Actualizado: " + 
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                }
            }
        });
        timerActualizacion.start();
        actualizarEstadisticas();
    }
    
    //-------------------------------Mostrar usuario actual -------------------------------------//
    private void mostrarUsuarioActual() {
        Usuario usuario = Session.getUsuarioActual();
        if (usuario != null) {
            if (lblUsuarioNombre != null) {
                lblUsuarioNombre.setText(usuario.getNombre());
            }
            if (lblUsuarioRol != null) {
                lblUsuarioRol.setText(usuario.getRol());
            }
            if (lblUsuarioAvatar != null) {
                lblUsuarioAvatar.setText(getAvatarByRol(usuario.getRol()));
            }
        } else {
            if (lblUsuarioNombre != null) {
                lblUsuarioNombre.setText("Invitado");
            }
            if (lblUsuarioRol != null) {
                lblUsuarioRol.setText("Sin sesión");
            }
        }
    }
    
    //-----------------------------Carga avatar del rol ------------------------------------------//
    private String getAvatarByRol(String rol) {
        if (rol == null) return "";
        switch (rol.toUpperCase()) {
            case "ADMIN": return "️";
            case "DENTISTA": return "️";
            case "RECEPCIONISTA": return "";
            case "ASISTENTE": return "";
            default: return "";
        }
    }
    
    //----------------------------Actualiza estadisticas-----------------------------------------//
    public void actualizarEstadisticas() {
        try {
            System.out.println("Actualizando estadísticas del dashboard...");
            
            // ===== 1. PACIENTES =====
            int totalPacientes = pacienteController.contarPacientes();
            if (lblPacientes != null) {
                lblPacientes.setText(formatNumber(totalPacientes));
            }
            
            // ===== 2. CITAS =====
            String hoy = LocalDate.now().toString();
            int citasHoy = citaController.contarCitasHoy();
            
            // Citas de la semana
            String inicioSemana = LocalDate.now().with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY)).toString();
            String finSemana = LocalDate.now().with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY)).toString();
            int citasSemana = citaController.listarPorRango(inicioSemana, finSemana).size();
            
            // Estadísticas por estado
            int programadas = citaController.contarCitasPorEstado(Cita.ESTADO_PROGRAMADA);
            int confirmadas = citaController.contarCitasPorEstado(Cita.ESTADO_CONFIRMADA);
            int pendientes = programadas + confirmadas;
            int completadas = citaController.contarCitasPorEstado(Cita.ESTADO_COMPLETADA);
            int canceladas = citaController.contarCitasPorEstado(Cita.ESTADO_CANCELADA);
            int noAsistio = citaController.contarCitasPorEstado(Cita.ESTADO_NO_ASISTIO);
            int enProceso = citaController.contarCitasPorEstado(Cita.ESTADO_EN_PROCESO);
            
            int totalCitas = programadas + confirmadas + completadas + canceladas + noAsistio + enProceso;
            
            // Actualizar labels de citas
            if (lblCitasHoy != null) {
                lblCitasHoy.setText(String.valueOf(citasHoy));
            }
            
            if (lblCitasSemana != null) {
                lblCitasSemana.setText(String.valueOf(citasSemana));
            }
            
            if (lblPendientes != null) {
                lblPendientes.setText(String.valueOf(pendientes));
            }
            
            if (lblCompletadas != null) {
                lblCompletadas.setText(String.valueOf(completadas));
            }
            
            // ===== 3. SERVICIOS =====
            int totalServicios = servicioController.contarServicios();
            if (lblTratamientos != null) {
                lblTratamientos.setText(String.valueOf(totalServicios));
            }
            
            // ===== 4. INGRESOS (DESDE LA BD) =====
            // 🔥 OBTENER INGRESOS REALES DESDE LA BASE DE DATOS
            double ingresosHoy = ventaController.obtenerTotalHoy();
            double ingresosMes = ventaController.obtenerTotalMes();
            
            // 🔥 FORMATEAR Y ACTUALIZAR
            if (lblIngresos != null) {
                // Si hay ingresos, mostrarlos. Si no, mostrar $0.00
                if (ingresosMes > 0) {
                    lblIngresos.setText("$" + String.format("%,.2f", ingresosMes));
                } else {
                    lblIngresos.setText("$0.00");
                }
            }
            
            System.out.println(" Ingresos - Hoy: $" + ingresosHoy + " | Mes: $" + ingresosMes);
            
            // ===== 5. DISTRIBUCIÓN DE CITAS =====
            if (totalCitas > 0) {
                int pCompletadas = (completadas * 100) / totalCitas;
                int pCanceladas = (canceladas * 100) / totalCitas;
                int pNoAsistio = (noAsistio * 100) / totalCitas;
                int pPendientes = (pendientes * 100) / totalCitas;
                
                actualizarProgressBar(progressCompletadas, lblPorcCompletadas, pCompletadas, greenColor);
                actualizarProgressBar(progressCanceladas, lblPorcCanceladas, pCanceladas, redColor);
                actualizarProgressBar(progressNoAsistio, lblPorcNoAsistio, pNoAsistio, orangeColor);
                actualizarProgressBar(progressPendientes, lblPorcPendientes, pPendientes, blueColor);
            } else {
                // Si no hay citas, mostrar 0
                actualizarProgressBar(progressCompletadas, lblPorcCompletadas, 0, greenColor);
                actualizarProgressBar(progressCanceladas, lblPorcCanceladas, 0, redColor);
                actualizarProgressBar(progressNoAsistio, lblPorcNoAsistio, 0, orangeColor);
                actualizarProgressBar(progressPendientes, lblPorcPendientes, 0, blueColor);
            }
            
            System.out.println("📊 Estadísticas actualizadas: " +
                "Pacientes=" + totalPacientes + 
                ", CitasHoy=" + citasHoy + 
                ", IngresosMes=" + ingresosMes);
            
        } catch (Exception e) {
            System.err.println("❌ Error al actualizar estadísticas: " + e.getMessage());
            e.printStackTrace();
        }
    }
   
    //-----------------------------Actualiza los progress bar-----------------------------------//
    private void actualizarProgressBar(JProgressBar bar, JLabel label, int valor, Color color) {
        if (bar != null) {
            bar.setValue(valor);
            bar.setForeground(color);
        }
        if (label != null) {
            label.setText(valor + "%");
            label.setForeground(color);
        }
    }
    
    //------------------------------Formato de numeros --------------------------------------//
    private String formatNumber(int number) {
        if (number >= 1000) {
            return String.format("%.1fK", number / 1000.0);
        }
        return String.valueOf(number);
    }
    
    //-----------------------CREAR CONTENIDO DEL DASHBOARD -------------------------//    
    private JPanel createDashboardContent() {
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(darkBg);
        content.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel header = createDarkHeader();
        content.add(header, BorderLayout.NORTH);
        
        JPanel scrollPanel = new JPanel();
        scrollPanel.setLayout(new BoxLayout(scrollPanel, BoxLayout.Y_AXIS));
        scrollPanel.setBackground(darkBg);
        
        scrollPanel.add(createStatsGrid());
        scrollPanel.add(Box.createVerticalStrut(15));
        scrollPanel.add(createDistributionPanel());
        
        JScrollPane mainScroll = new JScrollPane(scrollPanel);
        mainScroll.setBackground(darkBg);
        mainScroll.getViewport().setBackground(darkBg);
        mainScroll.setBorder(BorderFactory.createEmptyBorder());
        mainScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        content.add(mainScroll, BorderLayout.CENTER);
        
        return content;
    }
    
    // --------------------------FILA 1: TARJETAS PRINCIPALES -------------------------------//    
    private JPanel createStatsGrid() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 15, 15));
        panel.setBackground(darkBg);
        
        cardPacientes = createDarkStatCard("", "Pacientes", "0", greenColor, greenHover);
        cardCitasHoy = createDarkStatCard("", "Citas Hoy", "0", blueColor, blueHover);
        cardSemana = createDarkStatCard("", "Citas Semana", "0", purpleColor, purpleHover);
        cardPendientes = createDarkStatCard("", "Pendientes", "0", orangeColor, orangeHover);
        cardCompletadas = createDarkStatCard("", "Completadas", "0", tealColor, tealHover);
        cardIngresos = createDarkStatCard("", "Ingresos Mes", "$0", pinkColor, pinkColor.brighter());
        
        panel.add(cardPacientes);
        panel.add(cardCitasHoy);
        panel.add(cardSemana);
        panel.add(cardPendientes);
        panel.add(cardCompletadas);
        panel.add(cardIngresos);
        
        return panel;
    }
    
    private JPanel createDarkStatCard(String icon, String label, String value, Color color, Color hoverColor) {
        JPanel card = new JPanel(new BorderLayout(8, 3));
        card.setBackground(darkCard);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(50, 50, 55), 1),
            new EmptyBorder(12, 15, 12, 15)
        ));
        
        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                card.setBackground(color);
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(color.brighter(), 2),
                    new EmptyBorder(12, 15, 12, 15)
                ));
                for (Component comp : card.getComponents()) {
                    if (comp instanceof JPanel) {
                        ((JPanel) comp).setBackground(color);
                    }
                    if (comp instanceof JLabel) {
                        JLabel lbl = (JLabel) comp;
                        if (lbl.getFont().getStyle() == Font.BOLD) {
                            lbl.setForeground(Color.WHITE);
                        }
                    }
                }
                card.repaint();
            }
            public void mouseExited(MouseEvent e) {
                card.setBackground(darkCard);
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(50, 50, 55), 1),
                    new EmptyBorder(12, 15, 12, 15)
                ));
                for (Component comp : card.getComponents()) {
                    if (comp instanceof JPanel) {
                        ((JPanel) comp).setBackground(darkCard);
                    }
                    if (comp instanceof JLabel) {
                        JLabel lbl = (JLabel) comp;
                        if (lbl.getFont().getStyle() == Font.BOLD) {
                            lbl.setForeground(textLight);
                        }
                    }
                }
                card.repaint();
            }
        });
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        card.add(iconLabel, BorderLayout.WEST);
        
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(darkCard);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        valueLabel.setForeground(textLight);
        valueLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        rightPanel.add(valueLabel);
        
        JLabel labelLabel = new JLabel(label);
        labelLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        labelLabel.setForeground(textGray);
        labelLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        rightPanel.add(labelLabel);
        
        card.add(rightPanel, BorderLayout.CENTER);
        
        JPanel colorLine = new JPanel();
        colorLine.setBackground(color);
        colorLine.setPreferredSize(new Dimension(4, 0));
        card.add(colorLine, BorderLayout.EAST);
        
        if (label.equals("Pacientes")) lblPacientes = valueLabel;
        else if (label.equals("Citas Hoy")) lblCitasHoy = valueLabel;
        else if (label.equals("Citas Semana")) lblCitasSemana = valueLabel;
        else if (label.equals("Pendientes")) lblPendientes = valueLabel;
        else if (label.equals("Completadas")) lblCompletadas = valueLabel;
        else if (label.equals("Ingresos Mes")) lblIngresos = valueLabel;
        else if (label.equals("Tratamientos")) lblTratamientos = valueLabel;
        
        return card;
    }
    
    // --------------------------FILA 2: DISTRIBUCIÓN DE CITAS ----------------------------//    
    private JPanel createDistributionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(darkCard);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(50, 50, 55), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel title = new JLabel(" Distribución de Citas");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(textLight);
        panel.add(title, BorderLayout.NORTH);
        
        JPanel gridPanel = new JPanel(new GridLayout(2, 2, 20, 10));
        gridPanel.setBackground(darkCard);
        gridPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        gridPanel.add(createProgressItem(" Completadas", progressCompletadas = new JProgressBar(), lblPorcCompletadas = new JLabel("0%"), greenColor));
        gridPanel.add(createProgressItem(" Canceladas", progressCanceladas = new JProgressBar(), lblPorcCanceladas = new JLabel("0%"), redColor));
        gridPanel.add(createProgressItem(" No Asistió", progressNoAsistio = new JProgressBar(), lblPorcNoAsistio = new JLabel("0%"), orangeColor));
        gridPanel.add(createProgressItem(" Pendientes", progressPendientes = new JProgressBar(), lblPorcPendientes = new JLabel("0%"), blueColor));
        
        panel.add(gridPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createProgressItem(String label, JProgressBar bar, JLabel porcentaje, Color color) {
        JPanel panel = new JPanel(new BorderLayout(8, 2));
        panel.setBackground(darkCard);
        
        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblLabel.setForeground(textGray);
        panel.add(lblLabel, BorderLayout.WEST);
        
        porcentaje.setFont(new Font("Segoe UI", Font.BOLD, 14));
        porcentaje.setForeground(color);
        panel.add(porcentaje, BorderLayout.EAST);
        
        bar.setMaximum(100);
        bar.setValue(0);
        bar.setPreferredSize(new Dimension(0, 18));
        bar.setBackground(new Color(60, 60, 65));
        bar.setForeground(color);
        bar.setBorderPainted(false);
        panel.add(bar, BorderLayout.SOUTH);
        
        return panel;
    }
    
    // ------------------------------------HEADER ---------------------------------------------//    
    private JPanel createDarkHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(darkBg);
        header.setPreferredSize(new Dimension(0, 60));
        
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setBackground(darkBg);
        
        JLabel title = new JLabel(" Dashboard Principal");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(textLight);
        titlePanel.add(title);
        
        Usuario usuario = Session.getUsuarioActual();
        if (usuario != null) {
            JLabel saludo = new JLabel("             Hola,  " + usuario.getNombre().split(" ")[0]);
            saludo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            saludo.setForeground(textGray);
            titlePanel.add(saludo);
        }
        
        header.add(titlePanel, BorderLayout.WEST);
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        rightPanel.setBackground(darkBg);
        
        lblActualizacion = new JLabel("Actualizado: ahora");
        lblActualizacion.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblActualizacion.setForeground(new Color(60, 180, 110));
        rightPanel.add(lblActualizacion);
        
        JButton btnRefresh = new JButton("");
        btnRefresh.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        btnRefresh.setBackground(darkBg);
        btnRefresh.setForeground(textLight);
        btnRefresh.setBorderPainted(false);
        btnRefresh.setFocusPainted(false);
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.addActionListener(e -> {
            actualizarEstadisticas();
            lblActualizacion.setText(" Actualizado: ahora");
        });
        rightPanel.add(btnRefresh);
        
        JButton btnRecordatorios = new JButton("");
        btnRecordatorios.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        btnRecordatorios.setBackground(darkBg);
        btnRecordatorios.setForeground(textLight);
        btnRecordatorios.setBorderPainted(false);
        btnRecordatorios.setFocusPainted(false);
        btnRecordatorios.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRecordatorios.setToolTipText("Enviar recordatorios de citas para mañana");
        btnRecordatorios.addActionListener(e -> {
            NotificacionController controller = new NotificacionController();
            controller.enviarRecordatoriosManana(this);
        });
        rightPanel.add(btnRecordatorios);
        
        header.add(rightPanel, BorderLayout.EAST);
        
        return header;
    }
    
    //-------------------------------------SIDEBAR--------------------------------------------//    
    private void createDarkSidebar() {
        sidebarPanel = new JPanel();
        sidebarPanel.setPreferredSize(new Dimension(280, 0));
        sidebarPanel.setBackground(darkSidebar);
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBorder(new EmptyBorder(25, 20, 25, 20));
        
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        logoPanel.setBackground(darkSidebar);
        logoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel logoIcon = new JLabel("");
        logoIcon.setFont(new Font("Segoe UI", Font.PLAIN, 32));
        logoPanel.add(logoIcon);
        
        JLabel logoText = new JLabel("DENTAL CLINIC SYSTEM");
        logoText.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logoText.setForeground(textLight);
        logoPanel.add(logoText);
        
        sidebarPanel.add(logoPanel);
        sidebarPanel.add(Box.createVerticalStrut(30));
        
        btnDashboard = createDarkMenuButton("", "Dashboard");
        btnPacientes = createDarkMenuButton("", "Pacientes");
        btnCitas = createDarkMenuButton("", "Citas");
        btnServicios = createDarkMenuButton("", "Tratamientos");
        btnVentas = createDarkMenuButton("", "Finanzas");
        btnInventario = createDarkMenuButton("", "Inventario");
        btnUsuarios = createDarkMenuButton("", "Usuarios");
        btnReportes = createDarkMenuButton("", "Reportes");
        btnBackup = createDarkMenuButton("", "Backup");
        btnEncuesta = createDarkMenuButton("", "Encuesta");
        btnSettings = createDarkMenuButton("️", "Configuración");
        btnLogout = createDarkMenuButton("", "Cerrar Sesión");
        
        sidebarPanel.add(btnDashboard);
        sidebarPanel.add(Box.createVerticalStrut(5));
        sidebarPanel.add(btnPacientes);
        sidebarPanel.add(Box.createVerticalStrut(5));
        sidebarPanel.add(btnCitas);
        sidebarPanel.add(Box.createVerticalStrut(5));
        sidebarPanel.add(btnServicios);
        sidebarPanel.add(Box.createVerticalStrut(5));
        sidebarPanel.add(btnVentas);
        sidebarPanel.add(Box.createVerticalStrut(5));
        sidebarPanel.add(btnInventario);
        sidebarPanel.add(Box.createVerticalStrut(5));
        sidebarPanel.add(btnUsuarios);
        sidebarPanel.add(Box.createVerticalStrut(5));
        sidebarPanel.add(btnReportes);
        sidebarPanel.add(Box.createVerticalStrut(5));
        sidebarPanel.add(btnBackup);
        sidebarPanel.add(Box.createVerticalStrut(5));
        sidebarPanel.add(btnEncuesta);
        sidebarPanel.add(Box.createVerticalStrut(5));
        sidebarPanel.add(btnSettings);
        
        sidebarPanel.add(Box.createVerticalGlue());
        
        JPanel userCard = new JPanel(new BorderLayout(10, 5));
        userCard.setBackground(new Color(45, 45, 50));
        userCard.setBorder(new EmptyBorder(12, 12, 12, 12));
        userCard.setMaximumSize(new Dimension(250, 75));
        userCard.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        lblUsuarioAvatar = new JLabel("");
        lblUsuarioAvatar.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        userCard.add(lblUsuarioAvatar, BorderLayout.WEST);
        
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(new Color(45, 45, 50));
        
        lblUsuarioNombre = new JLabel("Cargando...");
        lblUsuarioNombre.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUsuarioNombre.setForeground(textLight);
        infoPanel.add(lblUsuarioNombre);
        
        JPanel rolPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 2));
        rolPanel.setBackground(new Color(45, 45, 50));
        
        lblUsuarioRol = new JLabel("Sin sesión");
        lblUsuarioRol.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblUsuarioRol.setForeground(textGray);
        rolPanel.add(lblUsuarioRol);
        
        JLabel lblEstado = new JLabel(" ● Activo");
        lblEstado.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblEstado.setForeground(new Color(60, 180, 110));
        rolPanel.add(lblEstado);
        
        infoPanel.add(rolPanel);
        userCard.add(infoPanel, BorderLayout.CENTER);
        
        sidebarPanel.add(userCard);
        sidebarPanel.add(Box.createVerticalStrut(10));
        sidebarPanel.add(btnLogout);
    }
    
    private JButton createDarkMenuButton(String icon, String text) {
        JButton btn = new JButton(icon + "  " + text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setForeground(textGray);
        btn.setBackground(darkSidebar);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setMaximumSize(new Dimension(250, 42));
        btn.setPreferredSize(new Dimension(250, 42));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(50, 50, 55));
                btn.setForeground(textLight);
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(darkSidebar);
                btn.setForeground(textGray);
            }
        });
        
        return btn;
    }
    
    // --------------------------------NAVEGACIÓN ----------------------------------------//    
    public void mostrarPanelPacientes() {
        try {
            panelActual = "Pacientes";
            if (timerActualizacion != null) timerActualizacion.stop();
            PanelPacientes panel = new PanelPacientes();
            setMainContentPanel(panel);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error al cargar pacientes: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    //-------------------------------Mostrar panelCitas-------------------------------------//
    public void mostrarPanelCitas() {
        try {
            panelActual = "Citas";
            if (timerActualizacion != null) timerActualizacion.stop();
            PanelCitas panel = new PanelCitas();
            setMainContentPanel(panel);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error al cargar citas: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    //-------------------------------Mostrar panelServicios---------------------------------//
    public void mostrarPanelServicios() {
        try {
            panelActual = "Servicios";
            if (timerActualizacion != null) timerActualizacion.stop();
            PanelServicios panel = new PanelServicios();
            setMainContentPanel(panel);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error al cargar servicios: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    //-------------------------------Mostrar panelUsuarios --------------------------------//
    public void mostrarPanelUsuarios() {
        try {
            panelActual = "Usuarios";
            if (timerActualizacion != null) timerActualizacion.stop();
            PanelUsuarios panel = new PanelUsuarios();
            setMainContentPanel(panel);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error al cargar usuarios: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    //------------------------------Mostrar panelFinanzas---------------------------------//
    public void mostrarPanelFinanzas() {
        try {
            panelActual = "Finanzas";
            if (timerActualizacion != null) timerActualizacion.stop();
            PanelFinanzas panel = new PanelFinanzas();
            setMainContentPanel(panel);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error al cargar finanzas: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    //------------------------------Mostrar panelInventario ------------------------------//
    public void mostrarPanelInventario() {
        try {
            panelActual = "Inventario";
            if (timerActualizacion != null) timerActualizacion.stop();
            PanelInventario panel = new PanelInventario();
            setMainContentPanel(panel);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error al cargar inventario: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    //-----------------------------Mostrar panelReportes---------------------------------//
    public void mostrarPanelReportes() {
        try {
            panelActual = "Reportes";
            if (timerActualizacion != null) timerActualizacion.stop();
            PanelReportes panel = new PanelReportes();
            setMainContentPanel(panel);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error al cargar reportes: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    //-----------------------------Mostrar panelBackup----------------------------------//
    public void mostrarPanelBackup() {
        try {
            panelActual = "Backup";
            if (timerActualizacion != null) timerActualizacion.stop();
            PanelBackup panel = new PanelBackup();
            setMainContentPanel(panel);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error al cargar backup: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // ----------------------------Mostrar panelEncuesta--------------------------------//
    public void mostrarPanelEncuesta() {
        try {
            panelActual = "Encuesta";
            if (timerActualizacion != null) timerActualizacion.stop();
            PanelEncuesta panel = new PanelEncuesta();
            setMainContentPanel(panel);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error al cargar encuesta: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    //----------------------------Mostrar panelConfiguracion--------------------------//
    public void mostrarPanelConfiguracion() {
        try {
            panelActual = "Configuracion";
            if (timerActualizacion != null) timerActualizacion.stop();
            PanelConfiguracion panel = new PanelConfiguracion(this);
            setMainContentPanel(panel);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error al cargar configuración: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    //-------------------------------Mostrar Dshboard----------------------------------//
    public void mostrarDashboard() {
        panelActual = "Dashboard";
        if (timerActualizacion != null) timerActualizacion.start();
        setMainContentPanel(createDashboardContent());
        actualizarEstadisticas();
        mostrarUsuarioActual();
    }
    
    
    public void setMainContentPanel(JPanel panel) {
        if (mainContentPanel != null) {
            Container parent = mainContentPanel.getParent();
            if (parent != null) {
                parent.remove(mainContentPanel);
                mainContentPanel = panel;
                parent.add(panel, BorderLayout.CENTER);
                parent.revalidate();
                parent.repaint();
            }
        }
    }
    
    public void recrearContenido() {
        try {
            if (mainContentPanel != null) {
                mainPanel.remove(mainContentPanel);
            }
            switch (panelActual) {
                case "Pacientes":
                    mainContentPanel = new PanelPacientes();
                    break;
                case "Citas":
                    mainContentPanel = new PanelCitas();
                    break;
                case "Servicios":
                    mainContentPanel = new PanelServicios();
                    break;
                case "Usuarios":
                    mainContentPanel = new PanelUsuarios();
                    break;
                case "Finanzas":
                    mainContentPanel = new PanelFinanzas();
                    break;
                case "Inventario":
                    mainContentPanel = new PanelInventario();
                    break;
                case "Reportes":
                    mainContentPanel = new PanelReportes();
                    break;
                case "Backup":
                    mainContentPanel = new PanelBackup();
                    break;
                case "Encuesta":
                    mainContentPanel = new PanelEncuesta();
                    break;
                case "Configuracion":
                    mainContentPanel = new PanelConfiguracion(this);
                    break;
                default:
                    mainContentPanel = createDashboardContent();
                    break;
            }
            mainPanel.add(mainContentPanel, BorderLayout.CENTER);
            mainPanel.revalidate();
            mainPanel.repaint();
        } catch (Exception e) {
            e.printStackTrace();
            mainContentPanel = createDashboardContent();
            mainPanel.add(mainContentPanel, BorderLayout.CENTER);
            mainPanel.revalidate();
            mainPanel.repaint();
        }
    }
    
    @Override
    public void dispose() {
        if (timerActualizacion != null) timerActualizacion.stop();
        super.dispose();
    }
    
    //-----------------------------------GETTERS ------------------------------------//    
    public JPanel getMainContentPanel() { return mainContentPanel; }
    public JButton getBtnDashboard() { return btnDashboard; }
    public JButton getBtnPacientes() { return btnPacientes; }
    public JButton getBtnCitas() { return btnCitas; }
    public JButton getBtnServicios() { return btnServicios; }
    public JButton getBtnVentas() { return btnVentas; }
    public JButton getBtnInventario() { return btnInventario; }
    public JButton getBtnUsuarios() { return btnUsuarios; }
    public JButton getBtnReportes() { return btnReportes; }
    public JButton getBtnBackup() { return btnBackup; }
    public JButton getBtnEncuesta() { return btnEncuesta; }
    public JButton getBtnSettings() { return btnSettings; }
    public JButton getBtnLogout() { return btnLogout; }
}