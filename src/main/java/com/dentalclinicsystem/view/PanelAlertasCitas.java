package com.dentalclinicsystem.view;

import com.dentalclinicsystem.controller.CitaController;
import com.dentalclinicsystem.model.Cita;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PanelAlertasCitas extends JPanel {
    
    private CitaController citaController;
    private JPanel panelAlertas;
    private JLabel lblHoraActual;
    private Timer timer;
    
    private Color darkBg = new Color(30, 30, 35);
    private Color darkCard = new Color(40, 40, 45);
    private Color textLight = new Color(220, 220, 230);
    private Color textGray = new Color(150, 150, 165);
    private Color accentGreen = new Color(60, 180, 110);
    private Color accentRed = new Color(210, 80, 80);
    private Color accentOrange = new Color(230, 160, 50);
    private Color accentBlue = new Color(70, 130, 200);
    private Color accentPurple = new Color(150, 80, 200);
    
    public PanelAlertasCitas() {
        this.citaController = new CitaController();
        initComponents();
        iniciarActualizacionAutomatica();
    }
    
    private void initComponents() {
        setBackground(darkBg);
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setPreferredSize(new Dimension(300, 0));
        
        // Panel superior - Título y hora
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(darkBg);
        headerPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        JLabel lblTitulo = new JLabel("🔔 Alertas de Citas");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitulo.setForeground(textLight);
        headerPanel.add(lblTitulo, BorderLayout.WEST);
        
        lblHoraActual = new JLabel(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        lblHoraActual.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblHoraActual.setForeground(textGray);
        headerPanel.add(lblHoraActual, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Panel de alertas (scrollable)
        panelAlertas = new JPanel();
        panelAlertas.setLayout(new BoxLayout(panelAlertas, BoxLayout.Y_AXIS));
        panelAlertas.setBackground(darkBg);
        
        JScrollPane scrollAlertas = new JScrollPane(panelAlertas);
        scrollAlertas.setBackground(darkBg);
        scrollAlertas.getViewport().setBackground(darkBg);
        scrollAlertas.setBorder(BorderFactory.createEmptyBorder());
        scrollAlertas.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        add(scrollAlertas, BorderLayout.CENTER);
        
        // Cargar alertas iniciales
        actualizarAlertas();
    }
    
    /**
     * Inicia la actualización automática cada 30 segundos
     */
    private void iniciarActualizacionAutomatica() {
        timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    actualizarHora();
                    actualizarAlertas();
                });
            }
        }, 0, 30000); // Cada 30 segundos
    }
    
    /**
     * Actualiza la hora actual
     */
    private void actualizarHora() {
        lblHoraActual.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }
    
    /**
     * Actualiza todas las alertas
     */
    private void actualizarAlertas() {
        panelAlertas.removeAll();
        
        List<AlertaCita> alertas = generarAlertas();
        
        if (alertas.isEmpty()) {
            JLabel lblNoAlertas = new JLabel("✅ No hay alertas");
            lblNoAlertas.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lblNoAlertas.setForeground(textGray);
            lblNoAlertas.setAlignmentX(Component.CENTER_ALIGNMENT);
            panelAlertas.add(lblNoAlertas);
        } else {
            // Ordenar por prioridad
            alertas.sort((a1, a2) -> Integer.compare(a2.prioridad, a1.prioridad));
            
            for (AlertaCita alerta : alertas) {
                panelAlertas.add(crearPanelAlerta(alerta));
                panelAlertas.add(Box.createRigidArea(new Dimension(0, 5)));
            }
        }
        
        panelAlertas.revalidate();
        panelAlertas.repaint();
    }
    
    /**
     * Genera todas las alertas del momento
     */
    private List<AlertaCita> generarAlertas() {
        List<AlertaCita> alertas = new ArrayList<>();
        LocalDateTime ahora = LocalDateTime.now();
        LocalDate hoy = ahora.toLocalDate();
        String fechaHoy = hoy.toString();
        
        // Obtener citas de hoy
        List<Cita> citasHoy = citaController.listarPorFecha(fechaHoy);
        
        if (citasHoy == null || citasHoy.isEmpty()) {
            return alertas;
        }
        
        // 1. ALERTA: Próximo paciente (cita en menos de 30 minutos)
        for (Cita cita : citasHoy) {
            if (cita.getEstado().equals(Cita.ESTADO_PROGRAMADA) || 
                cita.getEstado().equals(Cita.ESTADO_CONFIRMADA)) {
                
                try {
                    LocalTime horaCita = LocalTime.parse(cita.getHora());
                    LocalDateTime fechaHoraCita = LocalDateTime.of(hoy, horaCita);
                    
                    long minutosDiff = java.time.Duration.between(ahora, fechaHoraCita).toMinutes();
                    
                    // Próximo paciente (0-30 minutos)
                    if (minutosDiff >= 0 && minutosDiff <= 30) {
                        AlertaCita alerta = new AlertaCita();
                        alerta.tipo = "PRÓXIMO";
                        alerta.titulo = "⏰ Próximo paciente";
                        alerta.mensaje = cita.getPacienteNombre() + " - " + cita.getHora();
                        alerta.detalle = cita.getServicioNombre() + " con " + cita.getOdontologoNombre();
                        alerta.prioridad = 4; // Muy alta
                        alerta.color = accentGreen;
                        alerta.cita = cita;
                        alertas.add(alerta);
                    }
                    
                    // Paciente esperando (30-60 minutos)
                    if (minutosDiff > 30 && minutosDiff <= 60) {
                        AlertaCita alerta = new AlertaCita();
                        alerta.tipo = "ESPERANDO";
                        alerta.titulo = "⏳ Paciente en espera";
                        alerta.mensaje = cita.getPacienteNombre() + " - " + cita.getHora();
                        alerta.detalle = "Llega en " + (int)minutosDiff + " minutos";
                        alerta.prioridad = 3;
                        alerta.color = accentOrange;
                        alerta.cita = cita;
                        alertas.add(alerta);
                    }
                    
                } catch (Exception e) {
                    // Ignorar errores de parseo
                }
            }
        }
        
        // 2. ALERTA: Citas sin confirmar para hoy
        for (Cita cita : citasHoy) {
            if (cita.getEstado().equals(Cita.ESTADO_PROGRAMADA)) {
                AlertaCita alerta = new AlertaCita();
                alerta.tipo = "SIN_CONFIRMAR";
                alerta.titulo = "❓ Cita sin confirmar";
                alerta.mensaje = cita.getPacienteNombre() + " - " + cita.getHora();
                alerta.detalle = "Pendiente de confirmación";
                alerta.prioridad = 2;
                alerta.color = accentBlue;
                alerta.cita = cita;
                alertas.add(alerta);
            }
        }
        
        // 3. ALERTA: Citas que ya pasaron y no se atendieron
        for (Cita cita : citasHoy) {
            if (cita.getEstado().equals(Cita.ESTADO_PROGRAMADA) || 
                cita.getEstado().equals(Cita.ESTADO_CONFIRMADA)) {
                
                try {
                    LocalTime horaCita = LocalTime.parse(cita.getHora());
                    LocalDateTime fechaHoraCita = LocalDateTime.of(hoy, horaCita);
                    
                    if (fechaHoraCita.isBefore(ahora)) {
                        AlertaCita alerta = new AlertaCita();
                        alerta.tipo = "PASADA";
                        alerta.titulo = "⚠️ Cita pasada";
                        alerta.mensaje = cita.getPacienteNombre() + " - " + cita.getHora();
                        alerta.detalle = "No se ha marcado asistencia";
                        alerta.prioridad = 3;
                        alerta.color = accentRed;
                        alerta.cita = cita;
                        alertas.add(alerta);
                    }
                    
                } catch (Exception e) {
                    // Ignorar errores de parseo
                }
            }
        }
        
        // 4. ALERTA: Próximas citas (próximas 2 horas)
        for (Cita cita : citasHoy) {
            if (cita.getEstado().equals(Cita.ESTADO_PROGRAMADA) || 
                cita.getEstado().equals(Cita.ESTADO_CONFIRMADA)) {
                
                try {
                    LocalTime horaCita = LocalTime.parse(cita.getHora());
                    LocalDateTime fechaHoraCita = LocalDateTime.of(hoy, horaCita);
                    
                    long minutosDiff = java.time.Duration.between(ahora, fechaHoraCita).toMinutes();
                    
                    // Próximas 2 horas (que no estén en los grupos anteriores)
                    if (minutosDiff > 60 && minutosDiff <= 120) {
                        AlertaCita alerta = new AlertaCita();
                        alerta.tipo = "PROXIMA";
                        alerta.titulo = "📋 Próxima cita";
                        alerta.mensaje = cita.getPacienteNombre() + " - " + cita.getHora();
                        alerta.detalle = "En " + (int)minutosDiff + " minutos";
                        alerta.prioridad = 2;
                        alerta.color = accentPurple;
                        alerta.cita = cita;
                        alertas.add(alerta);
                    }
                    
                } catch (Exception e) {
                    // Ignorar errores de parseo
                }
            }
        }
        
        // 5. ALERTA: Resumen del día
        int totalCitas = citasHoy.size();
        int atendidas = 0;
        int pendientes = 0;
        
        for (Cita c : citasHoy) {
            if (c.getEstado().equals(Cita.ESTADO_COMPLETADA)) {
                atendidas++;
            } else if (c.getEstado().equals(Cita.ESTADO_PROGRAMADA) || 
                       c.getEstado().equals(Cita.ESTADO_CONFIRMADA)) {
                pendientes++;
            }
        }
        
        if (totalCitas > 0) {
            AlertaCita alerta = new AlertaCita();
            alerta.tipo = "RESUMEN";
            alerta.titulo = "📊 Resumen del día";
            alerta.mensaje = "Total: " + totalCitas + " | Atendidas: " + atendidas + " | Pendientes: " + pendientes;
            alerta.detalle = "Faltan " + pendientes + " citas por atender";
            alerta.prioridad = 1;
            alerta.color = new Color(100, 100, 150);
            alerta.cita = null;
            alertas.add(alerta);
        }
        
        return alertas;
    }
    
    /**
     * Crea un panel visual para una alerta
     */
    private JPanel crearPanelAlerta(AlertaCita alerta) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBackground(new Color(45, 45, 50));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(alerta.color, 2),
            new EmptyBorder(10, 12, 10, 12)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Click para ver detalle de la cita
        if (alerta.cita != null) {
            panel.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    mostrarDetalleCita(alerta.cita);
                }
            });
        }
        
        // Panel izquierdo - Información
        JPanel infoPanel = new JPanel(new BorderLayout(5, 2));
        infoPanel.setOpaque(false);
        
        // Título
        JLabel lblTitulo = new JLabel(alerta.titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitulo.setForeground(textLight);
        infoPanel.add(lblTitulo, BorderLayout.NORTH);
        
        // Mensaje y detalle
        JPanel mensajePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        mensajePanel.setOpaque(false);
        
        JLabel lblMensaje = new JLabel(alerta.mensaje);
        lblMensaje.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblMensaje.setForeground(textGray);
        mensajePanel.add(lblMensaje);
        
        if (alerta.detalle != null && !alerta.detalle.isEmpty()) {
            JLabel lblDetalle = new JLabel(" | " + alerta.detalle);
            lblDetalle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            lblDetalle.setForeground(alerta.color);
            mensajePanel.add(lblDetalle);
        }
        
        infoPanel.add(mensajePanel, BorderLayout.CENTER);
        panel.add(infoPanel, BorderLayout.CENTER);
        
        // Indicador de prioridad
        JPanel prioridadPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        prioridadPanel.setOpaque(false);
        
        JLabel lblPrioridad = new JLabel("●");
        lblPrioridad.setForeground(alerta.color);
        lblPrioridad.setFont(new Font("Segoe UI", Font.BOLD, 18));
        prioridadPanel.add(lblPrioridad);
        
        panel.add(prioridadPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    /**
     * Muestra el detalle de una cita al hacer click
     */
    private void mostrarDetalleCita(Cita cita) {
        String mensaje = "========== DETALLE DE CITA ==========\n\n" +
                        "ID: " + cita.getId() + "\n" +
                        "Paciente: " + cita.getPacienteNombre() + "\n" +
                        "Fecha: " + cita.getFecha() + "\n" +
                        "Hora: " + cita.getHora() + "\n" +
                        "Servicio: " + cita.getServicioNombre() + "\n" +
                        "Odontólogo: " + cita.getOdontologoNombre() + "\n" +
                        "Estado: " + cita.getEstadoTexto() + "\n" +
                        "Duración: " + cita.getDuracion() + " min\n" +
                        "Nota: " + (cita.getNota() != null ? cita.getNota() : "Sin nota");
        
        JOptionPane.showMessageDialog(this,
            mensaje,
            "📋 Detalle de Cita",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Clase interna para representar una alerta
     */
    private static class AlertaCita {
        String tipo;
        String titulo;
        String mensaje;
        String detalle;
        int prioridad; // 1=baja, 2=media, 3=alta, 4=muy alta
        Color color;
        Cita cita;
    }
}