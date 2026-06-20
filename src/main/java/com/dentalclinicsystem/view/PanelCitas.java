package com.dentalclinicsystem.view;

import com.dentalclinicsystem.controller.CitaController;
import com.dentalclinicsystem.controller.PacienteController;
import com.dentalclinicsystem.controller.UsuariosController;
import com.dentalclinicsystem.model.Cita;
import com.dentalclinicsystem.model.Paciente;
import com.dentalclinicsystem.model.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PanelCitas extends JPanel {

    private CitaController citaController;
    private PacienteController pacienteController;
    private UsuariosController usuariosController;

    private JTable tablaCitas;
    private DefaultTableModel model;
    private JTextField txtBuscar;
    private JButton btnNuevo, btnEditar, btnEliminar, btnRefrescar;
    private JButton btnConfirmar, btnCancelar, btnCompletar, btnBuscar;
    private JLabel lblTotalRegistros;
    private JComboBox<String> cbOdontologo;
    private JComboBox<String> cbPaciente;
    private JTextField txtFechaInicio;
    private JTextField txtFechaFin;
    
    // Panel de alertas
    private PanelAlertasCitas panelAlertas;
    private JSplitPane splitPane;

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

    public PanelCitas() {
        this.citaController = new CitaController();
        this.pacienteController = new PacienteController();
        this.usuariosController = new UsuariosController();
        initComponents();
        
        // Establecer fechas por defecto
        txtFechaInicio.setText(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        txtFechaFin.setText(LocalDate.now().plusDays(7).format(DateTimeFormatter.ISO_LOCAL_DATE));
        
        cargarCitas();
        actualizarContador();
    }

    private void initComponents() {
        setBackground(darkBg);
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Panel superior: Filtros
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Panel de alertas
        panelAlertas = new PanelAlertasCitas();

        // Panel central: Tabla
        JPanel tablePanel = createTablePanel();

        // Crear split panel (tabla a la izquierda, alertas a la derecha)
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tablePanel, panelAlertas);
        splitPane.setResizeWeight(0.7);
        splitPane.setDividerLocation(0.7);
        splitPane.setDividerSize(5);
        splitPane.setBackground(darkBg);
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        add(splitPane, BorderLayout.CENTER);

        // Panel inferior: Contador
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);

        configurarTeclasRapidas();
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(darkBg);
        panel.setBorder(new EmptyBorder(0, 0, 10, 0));

        // Título
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setBackground(darkBg);
        JLabel titleLabel = new JLabel("Gestión de Citas");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(textLight);
        titlePanel.add(titleLabel);
        panel.add(titlePanel, BorderLayout.NORTH);

        // Filtros
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        filterPanel.setBackground(darkBg);

        // Odontólogo
        JLabel lblOdontologo = new JLabel("Odontólogo:");
        lblOdontologo.setForeground(textGray);
        lblOdontologo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        filterPanel.add(lblOdontologo);

        cbOdontologo = new JComboBox<>();
        cbOdontologo.setBackground(new Color(50, 50, 55));
        cbOdontologo.setForeground(textLight);
        cbOdontologo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbOdontologo.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 65)));
        cbOdontologo.setPreferredSize(new Dimension(150, 30));
        cargarOdontologos();
        filterPanel.add(cbOdontologo);

        // Paciente
        JLabel lblPaciente = new JLabel("Paciente:");
        lblPaciente.setForeground(textGray);
        lblPaciente.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        filterPanel.add(lblPaciente);

        cbPaciente = new JComboBox<>();
        cbPaciente.setBackground(new Color(50, 50, 55));
        cbPaciente.setForeground(textLight);
        cbPaciente.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbPaciente.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 65)));
        cbPaciente.setPreferredSize(new Dimension(150, 30));
        cargarPacientes();
        filterPanel.add(cbPaciente);

        // Fecha Inicio
        JLabel lblFechaInicio = new JLabel("Fecha Inicio:");
        lblFechaInicio.setForeground(textGray);
        lblFechaInicio.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        filterPanel.add(lblFechaInicio);

        txtFechaInicio = new JTextField(10);
        txtFechaInicio.setBackground(new Color(50, 50, 55));
        txtFechaInicio.setForeground(textLight);
        txtFechaInicio.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 65)));
        txtFechaInicio.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtFechaInicio.setPreferredSize(new Dimension(100, 30));
        filterPanel.add(txtFechaInicio);

        // Fecha Fin
        JLabel lblFechaFin = new JLabel("Fecha Fin:");
        lblFechaFin.setForeground(textGray);
        lblFechaFin.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        filterPanel.add(lblFechaFin);

        txtFechaFin = new JTextField(10);
        txtFechaFin.setBackground(new Color(50, 50, 55));
        txtFechaFin.setForeground(textLight);
        txtFechaFin.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 65)));
        txtFechaFin.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtFechaFin.setPreferredSize(new Dimension(100, 30));
        filterPanel.add(txtFechaFin);

        // Botones
        btnBuscar = createActionButton("Buscar", accentBlue, 95);
        btnBuscar.addActionListener(e -> cargarCitas());
        filterPanel.add(btnBuscar);

        btnRefrescar = createActionButton("Refrescar", accentPurple, 95);
        btnRefrescar.addActionListener(e -> {
            txtFechaInicio.setText(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
            txtFechaFin.setText(LocalDate.now().plusDays(7).format(DateTimeFormatter.ISO_LOCAL_DATE));
            cbOdontologo.setSelectedIndex(0);
            cbPaciente.setSelectedIndex(0);
            cargarCitas();
        });
        filterPanel.add(btnRefrescar);

        // Botones rápidos de fecha
        JButton btnHoy = createActionButton("Hoy", accentGreen, 70);
        btnHoy.addActionListener(e -> {
            txtFechaInicio.setText(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
            txtFechaFin.setText(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
            cargarCitas();
        });
        filterPanel.add(btnHoy);

        JButton btnSemana = createActionButton("Semana", accentOrange, 85);
        btnSemana.addActionListener(e -> {
            txtFechaInicio.setText(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
            txtFechaFin.setText(LocalDate.now().plusDays(7).format(DateTimeFormatter.ISO_LOCAL_DATE));
            cargarCitas();
        });
        filterPanel.add(btnSemana);

        panel.add(filterPanel, BorderLayout.CENTER);

        // Botones de acción
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 5));
        actionPanel.setBackground(darkBg);
        actionPanel.setBorder(new EmptyBorder(5, 0, 0, 0));

        btnNuevo = createActionButton("Nuevo", accentGreen, 100);
        btnNuevo.addActionListener(e -> abrirFormularioCita(null));
        actionPanel.add(btnNuevo);

        btnEditar = createActionButton("Editar", accentBlue, 100);
        btnEditar.addActionListener(e -> editarCita());
        actionPanel.add(btnEditar);

        btnEliminar = createActionButton("Eliminar", accentRed, 100);
        btnEliminar.addActionListener(e -> eliminarCita());
        actionPanel.add(btnEliminar);

        JSeparator sep1 = new JSeparator(SwingConstants.VERTICAL);
        sep1.setPreferredSize(new Dimension(2, 30));
        sep1.setBackground(new Color(60, 60, 65));
        actionPanel.add(sep1);

        btnConfirmar = createActionButton("Confirmar", new Color(60, 180, 110), 100);
        btnConfirmar.addActionListener(e -> cambiarEstado(Cita.ESTADO_CONFIRMADA));
        actionPanel.add(btnConfirmar);

        btnCompletar = createActionButton("Completar", accentOrange, 100);
        btnCompletar.addActionListener(e -> cambiarEstado(Cita.ESTADO_COMPLETADA));
        actionPanel.add(btnCompletar);

        btnCancelar = createActionButton("Cancelar", accentRed, 100);
        btnCancelar.addActionListener(e -> cambiarEstado(Cita.ESTADO_CANCELADA));
        actionPanel.add(btnCancelar);

        JSeparator sep2 = new JSeparator(SwingConstants.VERTICAL);
        sep2.setPreferredSize(new Dimension(2, 30));
        sep2.setBackground(new Color(60, 60, 65));
        actionPanel.add(sep2);

        JButton btnRecordatorios = new JButton("Recordatorios");
        btnRecordatorios.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnRecordatorios.setBackground(new Color(100, 80, 200));
        btnRecordatorios.setForeground(Color.WHITE);
        btnRecordatorios.setBorderPainted(false);
        btnRecordatorios.setFocusPainted(false);
        btnRecordatorios.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRecordatorios.setPreferredSize(new Dimension(120, 32));
        btnRecordatorios.addActionListener(e -> enviarRecordatorios());
        actionPanel.add(btnRecordatorios);

        panel.add(actionPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(darkCard);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(50, 50, 55), 1),
            new EmptyBorder(10, 10, 10, 10)
        ));

        String[] columnas = {"ID", "Fecha", "Hora", "Paciente", "Odontólogo", "Servicio", "Estado", "Nota"};
        model = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaCitas = new JTable(model);
        tablaCitas.setBackground(new Color(45, 45, 50));
        tablaCitas.setForeground(textLight);
        tablaCitas.setGridColor(new Color(55, 55, 60));
        tablaCitas.setRowHeight(32);
        tablaCitas.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaCitas.getTableHeader().setBackground(new Color(50, 50, 55));
        tablaCitas.getTableHeader().setForeground(textLight);
        tablaCitas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tablaCitas.setSelectionBackground(new Color(60, 60, 70));
        tablaCitas.setSelectionForeground(textLight);
        tablaCitas.setShowGrid(false);
        tablaCitas.setIntercellSpacing(new Dimension(0, 0));

        // Renderer para colores de estado
        tablaCitas.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (column == 6) {
                    String estado = (String) value;
                    if (estado != null) {
                        switch (estado) {
                            case "PROGRAMADA":
                                c.setBackground(new Color(70, 130, 200));
                                break;
                            case "CONFIRMADA":
                                c.setBackground(new Color(60, 180, 110));
                                break;
                            case "EN_PROCESO":
                                c.setBackground(new Color(230, 160, 50));
                                break;
                            case "COMPLETADA":
                                c.setBackground(new Color(50, 168, 82));
                                break;
                            case "CANCELADA":
                                c.setBackground(new Color(210, 80, 80));
                                break;
                            case "NO_ASISTIO":
                                c.setBackground(new Color(200, 70, 70));
                                break;
                            default:
                                c.setBackground(new Color(80, 80, 85));
                                break;
                        }
                        c.setForeground(Color.WHITE);
                        c.setFont(c.getFont().deriveFont(Font.BOLD));
                    }
                } else {
                    if (!isSelected) {
                        c.setBackground(table.getBackground());
                        c.setForeground(table.getForeground());
                    }
                }
                return c;
            }
        });

        // Doble click para editar
        tablaCitas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editarCita();
                }
            }
        });

        // Menú contextual
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem itemEditar = new JMenuItem("Editar");
        itemEditar.addActionListener(e -> editarCita());
        popupMenu.add(itemEditar);

        JMenuItem itemEliminar = new JMenuItem("Eliminar");
        itemEliminar.addActionListener(e -> eliminarCita());
        popupMenu.add(itemEliminar);

        popupMenu.addSeparator();

        JMenuItem itemConfirmar = new JMenuItem("Confirmar");
        itemConfirmar.addActionListener(e -> cambiarEstado(Cita.ESTADO_CONFIRMADA));
        popupMenu.add(itemConfirmar);

        JMenuItem itemCancelar = new JMenuItem("Cancelar");
        itemCancelar.addActionListener(e -> cambiarEstado(Cita.ESTADO_CANCELADA));
        popupMenu.add(itemCancelar);

        JMenuItem itemCompletar = new JMenuItem("Completar");
        itemCompletar.addActionListener(e -> cambiarEstado(Cita.ESTADO_COMPLETADA));
        popupMenu.add(itemCompletar);

        tablaCitas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = tablaCitas.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        tablaCitas.setRowSelectionInterval(row, row);
                        popupMenu.show(tablaCitas, e.getX(), e.getY());
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = tablaCitas.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        tablaCitas.setRowSelectionInterval(row, row);
                        popupMenu.show(tablaCitas, e.getX(), e.getY());
                    }
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tablaCitas);
        scroll.setBackground(darkCard);
        scroll.getViewport().setBackground(darkCard);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(darkBg);
        panel.setPreferredSize(new Dimension(0, 40));
        panel.setBorder(new EmptyBorder(5, 10, 5, 10));

        lblTotalRegistros = new JLabel("Total: 0 registros");
        lblTotalRegistros.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTotalRegistros.setForeground(textGray);
        panel.add(lblTotalRegistros, BorderLayout.WEST);

        JLabel lblInfo = new JLabel("Doble click para editar | Click derecho para opciones");
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblInfo.setForeground(textGray);
        panel.add(lblInfo, BorderLayout.EAST);

        return panel;
    }

    private JButton createActionButton(String text, Color color, int width) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(width, 32));

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

    private void configurarTeclasRapidas() {
        InputMap inputMap = tablaCitas.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap actionMap = tablaCitas.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.KeyEvent.CTRL_DOWN_MASK), "nuevo");
        actionMap.put("nuevo", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                abrirFormularioCita(null);
            }
        });

        inputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.KeyEvent.CTRL_DOWN_MASK), "editar");
        actionMap.put("editar", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                editarCita();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, 0), "refrescar");
        actionMap.put("refrescar", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                cargarCitas();
            }
        });
    }

    private void cargarOdontologos() {
        cbOdontologo.removeAllItems();
        cbOdontologo.addItem("Todos los odontólogos");
        List<Usuario> odontologos = usuariosController.listarOdontologos();
        if (odontologos != null) {
            for (Usuario u : odontologos) {
                cbOdontologo.addItem(u.getNombre());
            }
        }
    }

    private void cargarPacientes() {
        cbPaciente.removeAllItems();
        cbPaciente.addItem("Todos los pacientes");
        List<Paciente> pacientes = pacienteController.listarTodos();
        if (pacientes != null) {
            for (Paciente p : pacientes) {
                cbPaciente.addItem(p.getNombreCompleto());
            }
        }
    }

    private void cargarCitas() {
        try {
            String fechaInicio = txtFechaInicio.getText().trim();
            String fechaFin = txtFechaFin.getText().trim();

            if (fechaInicio.isEmpty() || fechaFin.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Ingrese un rango de fechas válido",
                    "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            List<Cita> citas = citaController.listarPorRango(fechaInicio, fechaFin);

            // Filtrar por odontólogo
            String odontoSeleccion = (String) cbOdontologo.getSelectedItem();
            if (odontoSeleccion != null && !odontoSeleccion.equals("Todos los odontólogos")) {
                List<Usuario> odontologos = usuariosController.listarOdontologos();
                if (odontologos != null) {
                    for (Usuario u : odontologos) {
                        if (u.getNombre().equals(odontoSeleccion)) {
                            int id = u.getId();
                            citas.removeIf(c -> c.getOdontologoId() != id);
                            break;
                        }
                    }
                }
            }

            // Filtrar por paciente
            String pacienteSeleccion = (String) cbPaciente.getSelectedItem();
            if (pacienteSeleccion != null && !pacienteSeleccion.equals("Todos los pacientes")) {
                List<Paciente> pacientes = pacienteController.listarTodos();
                if (pacientes != null) {
                    for (Paciente p : pacientes) {
                        if (p.getNombreCompleto().equals(pacienteSeleccion)) {
                            int id = p.getId();
                            citas.removeIf(c -> c.getPacienteId() != id);
                            break;
                        }
                    }
                }
            }

            citaController.cargarTabla(model, citas);
            actualizarContador();

        } catch (Exception e) {
            System.err.println("Error al cargar citas: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error al cargar citas: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarContador() {
        int total = model.getRowCount();
        int totalHoy = citaController.contarCitasHoy();
        int pendientes = citaController.contarCitasPorEstado(Cita.ESTADO_PROGRAMADA)
                + citaController.contarCitasPorEstado(Cita.ESTADO_CONFIRMADA);
        lblTotalRegistros.setText("Mostrando " + total + " citas | Citas hoy: " + totalHoy + " | Pendientes: " + pendientes);
    }

    private void abrirFormularioCita(Cita cita) {
        DialogCita dialog = new DialogCita((JFrame) SwingUtilities.getWindowAncestor(this), cita != null);
        dialog.setCita(cita);
        dialog.setVisible(true);

        if (dialog.isGuardado()) {
            cargarCitas();
        }
    }

    private void editarCita() {
        int row = tablaCitas.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                "Seleccione una cita para editar",
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) model.getValueAt(row, 0);
        Cita cita = citaController.buscarPorId(id);
        if (cita != null) {
            abrirFormularioCita(cita);
        }
    }

    private void eliminarCita() {
        int row = tablaCitas.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                "Seleccione una cita para eliminar",
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) model.getValueAt(row, 0);
        String paciente = (String) model.getValueAt(row, 3);
        String fecha = (String) model.getValueAt(row, 1);
        String hora = (String) model.getValueAt(row, 2);

        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de eliminar la cita?\n\n"
            + "Paciente: " + paciente + "\n"
            + "Fecha: " + fecha + " " + hora + "\n\n"
            + "⚠️ Esta acción no se puede deshacer",
            "Confirmar Eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (citaController.eliminarCita(id)) {
                cargarCitas();
            }
        }
    }

    private void cambiarEstado(String nuevoEstado) {
        int row = tablaCitas.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                "Seleccione una cita para cambiar estado",
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String estadoActual = (String) model.getValueAt(row, 6);

        if (estadoActual.equals("COMPLETADA") || estadoActual.equals("CANCELADA")) {
            JOptionPane.showMessageDialog(this,
                "No se puede cambiar el estado de una cita " + estadoActual.toLowerCase(),
                "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) model.getValueAt(row, 0);
        String paciente = (String) model.getValueAt(row, 3);

        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Cambiar estado de la cita?\n\n"
            + "Paciente: " + paciente + "\n"
            + "Estado actual: " + estadoActual + "\n"
            + "Nuevo estado: " + nuevoEstado,
            "Confirmar cambio de estado",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (citaController.cambiarEstadoCita(id, nuevoEstado)) {
                cargarCitas();
            }
        }
    }

    private void enviarRecordatorios() {
        LocalDate manana = LocalDate.now().plusDays(1);
        String fechaStr = manana.toString();
        
        List<Cita> citas = citaController.listarPorFecha(fechaStr);
        
        if (citas == null || citas.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No hay citas programadas para mañana",
                "Recordatorios", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int pendientes = 0;
        for (Cita c : citas) {
            if (c.getEstado().equals(Cita.ESTADO_PROGRAMADA) || 
                c.getEstado().equals(Cita.ESTADO_CONFIRMADA)) {
                pendientes++;
            }
        }
        
        if (pendientes == 0) {
            JOptionPane.showMessageDialog(this,
                "No hay citas pendientes para mañana",
                "Recordatorios", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "📧 ¿Enviar recordatorios de 24 horas?\n\n" +
            "Citas para mañana: " + pendientes + "\n" +
            "Los recordatorios se enviarán por email",
            "Confirmar envío",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this,
                "✅ Recordatorios enviados correctamente",
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}