package com.dentalclinicsystem.view;

import com.dentalclinicsystem.controller.VentaController;
import com.dentalclinicsystem.model.Venta;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class PanelFinanzas extends JPanel {
    
    private VentaController controller;
    private JTable tablaVentas;
    private DefaultTableModel model;
    private JLabel lblTotalRegistros, lblTotalHoy, lblTotalMes;
    private JButton btnNuevo, btnVerDetalle, btnAnular, btnRefrescar;
    private JButton btnReenviarFactura;
    private JDateChooser dateInicio;  // Cambiado de JTextField a JDateChooser
    private JDateChooser dateFin;     // Cambiado de JTextField a JDateChooser
    private JButton btnBuscar;
    
    private Color darkBg = new Color(30, 30, 35);
    private Color darkCard = new Color(40, 40, 45);
    private Color textLight = new Color(220, 220, 230);
    private Color textGray = new Color(150, 150, 165);
    private Color accentBlue = new Color(70, 130, 200);
    private Color accentGreen = new Color(60, 180, 110);
    private Color accentRed = new Color(210, 80, 80);
    private Color accentOrange = new Color(230, 160, 50);
    private Color accentPurple = new Color(150, 80, 200);
    private Color accentPink = new Color(200, 100, 150);
    
    public PanelFinanzas() {
        this.controller = new VentaController();
        initComponents();
        // Cargar ventas al iniciar
        SwingUtilities.invokeLater(() -> {
            cargarVentas();
            actualizarResumen();
        });
    }
    
    private void initComponents() {
        setBackground(darkBg);
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        JPanel resumenPanel = createResumenPanel();
        add(resumenPanel, BorderLayout.CENTER);
        
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(darkBg);
        panel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setBackground(darkBg);
        titlePanel.setPreferredSize(new Dimension(0, 40));
        
        JLabel titleLabel = new JLabel("Gestión de Finanzas");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(textLight);
        titlePanel.add(titleLabel);
        panel.add(titlePanel, BorderLayout.NORTH);
        
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        actionPanel.setBackground(darkBg);
        
        JLabel lblInicio = new JLabel("Desde:");
        lblInicio.setForeground(textGray);
        lblInicio.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        actionPanel.add(lblInicio);
        
        // 🔥 NUEVO: JDateChooser en lugar de JTextField
        dateInicio = new JDateChooser();
        dateInicio.setDateFormatString("yyyy-MM-dd");
        dateInicio.setBackground(new Color(50, 50, 55));
        dateInicio.setForeground(textLight);
        dateInicio.getCalendarButton().setBackground(new Color(60, 60, 65));
        dateInicio.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateInicio.setPreferredSize(new Dimension(120, 28));
        actionPanel.add(dateInicio);
        
        JLabel lblFin = new JLabel("Hasta:");
        lblFin.setForeground(textGray);
        lblFin.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        actionPanel.add(lblFin);
        
        // 🔥 NUEVO: JDateChooser en lugar de JTextField
        dateFin = new JDateChooser();
        dateFin.setDateFormatString("yyyy-MM-dd");
        dateFin.setBackground(new Color(50, 50, 55));
        dateFin.setForeground(textLight);
        dateFin.getCalendarButton().setBackground(new Color(60, 60, 65));
        dateFin.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateFin.setPreferredSize(new Dimension(120, 28));
        actionPanel.add(dateFin);
        
        // Establecer fechas por defecto
        establecerFechasPorDefecto();
        
        btnBuscar = createActionButton("Buscar", accentBlue);
        btnBuscar.addActionListener(e -> {
            System.out.println("Buscando ventas con fechas seleccionadas...");
            cargarVentas();
            actualizarResumen();
        });
        actionPanel.add(btnBuscar);
        
//        btnRefrescar = createActionButton(" ", accentPurple);
//        btnRefrescar.setPreferredSize(new Dimension(40, 32));
//        btnRefrescar.setToolTipText("Restablecer fechas al mes actual");
//        btnRefrescar.addActionListener(e -> {
//            System.out.println("Restableciendo fechas...");
//            establecerFechasPorDefecto();
//            cargarVentas();
//            actualizarResumen();
//        });
//        actionPanel.add(btnRefrescar);
        
        JSeparator sep = new JSeparator(SwingConstants.VERTICAL);
        sep.setPreferredSize(new Dimension(2, 30));
        sep.setBackground(new Color(60, 60, 65));
        actionPanel.add(sep);
        
        btnNuevo = createActionButton("Nueva Venta", accentGreen);
        btnNuevo.addActionListener(e -> abrirFormularioVenta());
        actionPanel.add(btnNuevo);
        
        btnVerDetalle = createActionButton("Detalle", accentBlue);
        btnVerDetalle.addActionListener(e -> verDetalleVenta());
        actionPanel.add(btnVerDetalle);
        
        btnAnular = createActionButton("Anular", accentRed);
        btnAnular.addActionListener(e -> anularVenta());
        actionPanel.add(btnAnular);
        
        JSeparator sep2 = new JSeparator(SwingConstants.VERTICAL);
        sep2.setPreferredSize(new Dimension(2, 30));
        sep2.setBackground(new Color(60, 60, 65));
        actionPanel.add(sep2);
        
        btnReenviarFactura = createActionButton("Reenviar Factura", accentPink);
        btnReenviarFactura.addActionListener(e -> reenviarFactura());
        actionPanel.add(btnReenviarFactura);
        
        panel.add(actionPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    // 🔥 NUEVO MÉTODO: Establecer fechas por defecto
    private void establecerFechasPorDefecto() {
        try {
            // Primer día del mes actual
            LocalDate inicioMes = LocalDate.now().withDayOfMonth(1);
            // Hoy
            LocalDate hoy = LocalDate.now();
            
            // Convertir LocalDate a Date
            Date fechaInicio = java.sql.Date.valueOf(inicioMes);
            Date fechaFin = java.sql.Date.valueOf(hoy);
            
            dateInicio.setDate(fechaInicio);
            dateFin.setDate(fechaFin);
            
            System.out.println("📅 Fechas establecidas: " + inicioMes + " - " + hoy);
        } catch (Exception e) {
            System.err.println("❌ Error al establecer fechas: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // 🔥 MÉTODO: Obtener fecha como String desde JDateChooser
    private String getFechaInicio() {
        try {
            Date fecha = dateInicio.getDate();
            if (fecha != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                return sdf.format(fecha);
            }
        } catch (Exception e) {
            System.err.println("❌ Error al obtener fecha inicio: " + e.getMessage());
        }
        return LocalDate.now().withDayOfMonth(1).toString();
    }
    
    // 🔥 MÉTODO: Obtener fecha como String desde JDateChooser
    private String getFechaFin() {
        try {
            Date fecha = dateFin.getDate();
            if (fecha != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                return sdf.format(fecha);
            }
        } catch (Exception e) {
            System.err.println("❌ Error al obtener fecha fin: " + e.getMessage());
        }
        return LocalDate.now().toString();
    }
    
    private JPanel createResumenPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 15, 0));
        panel.setBackground(darkBg);
        panel.setBorder(new EmptyBorder(0, 0, 10, 0));
        panel.setPreferredSize(new Dimension(0, 60));
        
        lblTotalHoy = new JLabel(" Hoy: $0.00");
        lblTotalHoy.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotalHoy.setForeground(textLight);
        lblTotalHoy.setHorizontalAlignment(SwingConstants.CENTER);
        lblTotalHoy.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 65), 1),
            new EmptyBorder(10, 10, 10, 10)
        ));
        lblTotalHoy.setBackground(darkCard);
        lblTotalHoy.setOpaque(true);
        panel.add(lblTotalHoy);
        
        lblTotalMes = new JLabel(" Mes: $0.00");
        lblTotalMes.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotalMes.setForeground(textLight);
        lblTotalMes.setHorizontalAlignment(SwingConstants.CENTER);
        lblTotalMes.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 65), 1),
            new EmptyBorder(10, 10, 10, 10)
        ));
        lblTotalMes.setBackground(darkCard);
        lblTotalMes.setOpaque(true);
        panel.add(lblTotalMes);
        
        lblTotalRegistros = new JLabel(" Ventas: 0");
        lblTotalRegistros.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotalRegistros.setForeground(textLight);
        lblTotalRegistros.setHorizontalAlignment(SwingConstants.CENTER);
        lblTotalRegistros.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 65), 1),
            new EmptyBorder(10, 10, 10, 10)
        ));
        lblTotalRegistros.setBackground(darkCard);
        lblTotalRegistros.setOpaque(true);
        panel.add(lblTotalRegistros);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(darkCard);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(50, 50, 55), 1),
            new EmptyBorder(10, 10, 10, 10)
        ));
        panel.setPreferredSize(new Dimension(0, 300));
        
        String[] columnas = {"ID", "Fecha", "Paciente", "Tipo", "Total", "Pago", "Estado", "📧 Factura"};
        model = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaVentas = new JTable(model);
        tablaVentas.setBackground(new Color(45, 45, 50));
        tablaVentas.setForeground(textLight);
        tablaVentas.setGridColor(new Color(55, 55, 60));
        tablaVentas.setRowHeight(30);
        tablaVentas.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaVentas.getTableHeader().setBackground(new Color(50, 50, 55));
        tablaVentas.getTableHeader().setForeground(textLight);
        tablaVentas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaVentas.setSelectionBackground(new Color(60, 60, 70));
        tablaVentas.setSelectionForeground(textLight);
        tablaVentas.setShowGrid(false);
        tablaVentas.setIntercellSpacing(new Dimension(0, 0));
        
        tablaVentas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    verDetalleVenta();
                }
            }
        });
        
        JScrollPane scroll = new JScrollPane(tablaVentas);
        scroll.setBackground(darkCard);
        scroll.getViewport().setBackground(darkCard);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setPreferredSize(new Dimension(0, 250));
        
        panel.add(scroll, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JButton createActionButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(110, 32));
        
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
   
    // ========== MÉTODOS PRINCIPALES ==========
    
    private void cargarVentas() {
        try {
            // 🔥 OBTENER FECHAS USANDO LOS NUEVOS MÉTODOS
            String inicio = getFechaInicio();
            String fin = getFechaFin();
            
            System.out.println("🔍 ===== CARGANDO VENTAS =====");
            System.out.println("🔍 Desde: " + inicio);
            System.out.println("🔍 Hasta: " + fin);
            
            // 🔥 LIMPIAR TABLA
            model.setRowCount(0);
            model.fireTableDataChanged();
            
            // 🔥 OBTENER VENTAS
            List<Venta> ventas = controller.listarPorRango(inicio, fin);
            
            System.out.println("📋 Ventas encontradas: " + (ventas != null ? ventas.size() : 0));
            
            if (ventas != null && !ventas.isEmpty()) {
                System.out.println("📋 Cargando " + ventas.size() + " ventas en la tabla");
                
                for (Venta v : ventas) {
                    // Verificar email del paciente
                    String emailStatus = "Sin email";
                    try {
                        com.dentalclinicsystem.dao.PacienteDAO pacienteDAO = new com.dentalclinicsystem.dao.PacienteDAO();
                        com.dentalclinicsystem.model.Paciente paciente = pacienteDAO.obtenerPorId(v.getPacienteId());
                        if (paciente != null && paciente.getEmail() != null && !paciente.getEmail().isEmpty()) {
                            emailStatus = "   Enviable";
                        }
                    } catch (Exception e) {
                        emailStatus = "️ Error";
                    }
                    
                    model.addRow(new Object[]{
                        v.getId(),
                        v.getFecha() != null ? v.getFecha().substring(0, 10) : "",
                        v.getPacienteNombre() != null ? v.getPacienteNombre() : "N/A",
                        v.getTipoComprobanteTexto(),
                        v.getTotalFormateado(),
                        v.getMetodoPagoTexto(),
                        v.getEstadoTexto(),
                        emailStatus
                    });
                    System.out.println("   ✅ Agregada venta ID: " + v.getId() + " | Total: " + v.getTotalFormateado());
                }
            } else {
                System.out.println("📋 No hay ventas en el rango seleccionado");
            }
            
            // 🔥 FORZAR ACTUALIZACIÓN DE LA TABLA
            model.fireTableDataChanged();
            tablaVentas.revalidate();
            tablaVentas.repaint();
            
            // 🔥 ACTUALIZAR RESUMEN
            actualizarResumen();
            
        } catch (Exception e) {
            System.err.println("❌ Error al cargar ventas: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error al cargar ventas: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void actualizarResumen() {
        try {
            // 🔥 OBTENER TOTALES REALES DIRECTAMENTE DE LA BD
            double totalHoy = controller.obtenerTotalHoy();
            double totalMes = controller.obtenerTotalMes();
            int totalVentas = model.getRowCount();
            
            lblTotalHoy.setText(" Hoy : $  " + String.format("%,.2f", totalHoy));
            lblTotalMes.setText(" Mes : $ " + String.format("%,.2f", totalMes));
            lblTotalRegistros.setText(" Ventas : " + totalVentas);
            
            System.out.println("📊 Resumen actualizado: Hoy $" + totalHoy + " | Mes $" + totalMes + " | Ventas " + totalVentas);
            
        } catch (Exception e) {
            System.err.println("❌ Error al actualizar resumen: " + e.getMessage());
        }
    }
    
    private void abrirFormularioVenta() {
        DialogVenta dialog = new DialogVenta((JFrame) SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);
        
        if (dialog.isGuardado()) {
            System.out.println("🔄 Actualizando panel después de guardar venta...");
            // 🔥 RECARGAR TODOS LOS DATOS CON UN PEQUEÑO DELAY
            SwingUtilities.invokeLater(() -> {
                cargarVentas();
                actualizarResumen();
            });
        }
    }
    
    private void verDetalleVenta() {
        int row = tablaVentas.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una venta para ver detalle", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (int) model.getValueAt(row, 0);
        Venta venta = controller.buscarPorId(id);
        if (venta != null) {
            mostrarDetalleVenta(venta);
        }
    }
    
    private void mostrarDetalleVenta(Venta venta) {
        StringBuilder sb = new StringBuilder();
        sb.append("========== DETALLE DE VENTA ==========\n\n");
        sb.append("ID: ").append(venta.getId()).append("\n");
        sb.append("Fecha: ").append(venta.getFecha()).append("\n");
        sb.append("Paciente: ").append(venta.getPacienteNombre()).append("\n");
        sb.append("Tipo: ").append(venta.getTipoComprobanteTexto()).append("\n");
        sb.append("Método Pago: ").append(venta.getMetodoPagoTexto()).append("\n\n");
        sb.append("--- DETALLES ---\n");
        
        for (com.dentalclinicsystem.model.DetalleVenta d : venta.getDetalles()) {
            sb.append(d.getNombre()).append(" x").append(d.getCantidad())
              .append(" = ").append(d.getSubtotalFormateado()).append("\n");
        }
        
        sb.append("\nSubtotal: ").append(venta.getSubtotalFormateado()).append("\n");
        sb.append("IVA (19%): ").append(String.format("$%.2f", venta.getImpuesto())).append("\n");
        sb.append("Total: ").append(venta.getTotalFormateado()).append("\n");
        sb.append("Estado: ").append(venta.getEstadoTexto());
        
        sb.append("\n\n FACTURA:\n");
        try {
            com.dentalclinicsystem.dao.PacienteDAO pacienteDAO = new com.dentalclinicsystem.dao.PacienteDAO();
            com.dentalclinicsystem.model.Paciente paciente = pacienteDAO.obtenerPorId(venta.getPacienteId());
            if (paciente != null && paciente.getEmail() != null && !paciente.getEmail().isEmpty()) {
                sb.append("Email: ").append(paciente.getEmail());
                sb.append("\n Se puede reenviar la factura");
            } else {
                sb.append("️ El paciente no tiene email registrado");
            }
        } catch (Exception e) {
            sb.append("️ Error al obtener información del paciente");
        }
        
        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setBackground(new Color(45, 45, 50));
        textArea.setForeground(textLight);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        
        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setPreferredSize(new Dimension(450, 450));
        
        JOptionPane.showMessageDialog(this, scroll, "Detalle de Venta #" + venta.getId(), JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void anularVenta() {
        int row = tablaVentas.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una venta para anular", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (int) model.getValueAt(row, 0);
        String estado = (String) model.getValueAt(row, 6);
        
        if (estado.equals("Anulada")) {
            JOptionPane.showMessageDialog(this, "Esta venta ya está anulada", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de anular esta venta?\n\n" +
            "ID: " + id + "\n" +
            "Paciente: " + model.getValueAt(row, 2) + "\n" +
            "Total: " + model.getValueAt(row, 4) + "\n\n" +
            "Esta acción no se puede deshacer",
            "Confirmar anulación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (controller.anularVenta(id)) {
                cargarVentas();
                actualizarResumen();
            }
        }
    }
    
    private void reenviarFactura() {
        int row = tablaVentas.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione una venta para reenviar la factura", 
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (int) model.getValueAt(row, 0);
        String paciente = (String) model.getValueAt(row, 2);
        String total = (String) model.getValueAt(row, 4);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Reenviar factura de la venta #" + id + "?\n\n" +
            "Paciente: " + paciente + "\n" +
            "Total: " + total,
            "Confirmar reenvío",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            controller.reenviarFactura(id);
        }
    }
}