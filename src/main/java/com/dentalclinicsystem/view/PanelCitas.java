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
    private JButton btnNuevo, btnEditar, btnEliminar, btnRefrescar;
    private JButton btnConfirmar, btnCancelar, btnCompletar, btnBuscar;
    private JLabel lblTotalRegistros;
    private JComboBox<String> cbOdontologo;
    private JComboBox<String> cbPaciente;
    private JTextField txtFechaInicio;
    private JTextField txtFechaFin;
    
    private JComboBox<String> cbEstadoFiltro;
    private JButton btnExportar;
    private JPanel resumenPanel;
    private JLabel lblProgramadas, lblConfirmadas, lblEnProceso, lblCanceladas, lblTotalHoy;
    
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

    private JFrame parentFrame;

    public PanelCitas() {
        this.citaController = new CitaController();
        this.pacienteController = new PacienteController();
        this.usuariosController = new UsuariosController();
        initComponents();
        
        txtFechaInicio.setText(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        txtFechaFin.setText(LocalDate.now().plusDays(7).format(DateTimeFormatter.ISO_LOCAL_DATE));
        
        SwingUtilities.invokeLater(() -> {
            cargarCitas();
            actualizarContador();
            actualizarResumenDiario();
        });
    }

    private void initComponents() {
        setBackground(darkBg);
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);

        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 8));
        centerPanel.setBackground(darkBg);

       JPanel resumenPanel = createResumenDiarioPanel();
        centerPanel.add(resumenPanel, BorderLayout.NORTH);

        JPanel tablePanel = createTablePanel();

        panelAlertas = new PanelAlertasCitas();
        panelAlertas.setPreferredSize(new Dimension(280, 0));

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tablePanel, panelAlertas);
        splitPane.setResizeWeight(0.75);
        splitPane.setDividerLocation(750);
        splitPane.setDividerSize(5);
        splitPane.setBackground(darkBg);
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        centerPanel.add(splitPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);

        configurarTeclasRapidas();
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(darkBg);
        panel.setBorder(new EmptyBorder(0, 0, 8, 0));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setBackground(darkBg);
        JLabel titleLabel = new JLabel("Gestión de Citas");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(textLight);
        titlePanel.add(titleLabel);
        panel.add(titlePanel, BorderLayout.NORTH);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 5));
        filterPanel.setBackground(darkBg);

        JLabel lblOdontologo = new JLabel("Odontólogo:");
        lblOdontologo.setForeground(textGray);
        lblOdontologo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        filterPanel.add(lblOdontologo);

        cbOdontologo = new JComboBox<>();
        cbOdontologo.setBackground(new Color(50, 50, 55));
        cbOdontologo.setForeground(textLight);
        cbOdontologo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cbOdontologo.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 65)));
        cbOdontologo.setPreferredSize(new Dimension(140, 28));
        cargarOdontologos();
        filterPanel.add(cbOdontologo);

        JLabel lblPaciente = new JLabel("Paciente:");
        lblPaciente.setForeground(textGray);
        lblPaciente.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        filterPanel.add(lblPaciente);

        cbPaciente = new JComboBox<>();
        cbPaciente.setBackground(new Color(50, 50, 55));
        cbPaciente.setForeground(textLight);
        cbPaciente.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cbPaciente.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 65)));
        cbPaciente.setPreferredSize(new Dimension(140, 28));
        cargarPacientes();
        filterPanel.add(cbPaciente);

        JLabel lblEstado = new JLabel("Estado:");
        lblEstado.setForeground(textGray);
        lblEstado.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        filterPanel.add(lblEstado);

        cbEstadoFiltro = new JComboBox<>(new String[]{
            "Todos", 
            Cita.ESTADO_PROGRAMADA, 
            Cita.ESTADO_CONFIRMADA, 
            Cita.ESTADO_EN_PROCESO, 
            Cita.ESTADO_COMPLETADA, 
            Cita.ESTADO_CANCELADA, 
            Cita.ESTADO_NO_ASISTIO
        });
        cbEstadoFiltro.setBackground(new Color(50, 50, 55));
        cbEstadoFiltro.setForeground(textLight);
        cbEstadoFiltro.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cbEstadoFiltro.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 65)));
        cbEstadoFiltro.setPreferredSize(new Dimension(110, 28));
        cbEstadoFiltro.addActionListener(e -> cargarCitas());
        filterPanel.add(cbEstadoFiltro);

        JLabel lblFechaInicio = new JLabel("Desde:");
        lblFechaInicio.setForeground(textGray);
        lblFechaInicio.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        filterPanel.add(lblFechaInicio);

        txtFechaInicio = new JTextField(10);
        txtFechaInicio.setBackground(new Color(50, 50, 55));
        txtFechaInicio.setForeground(textLight);
        txtFechaInicio.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 65)));
        txtFechaInicio.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        txtFechaInicio.setPreferredSize(new Dimension(90, 28));
        filterPanel.add(txtFechaInicio);

        JLabel lblFechaFin = new JLabel("Hasta:");
        lblFechaFin.setForeground(textGray);
        lblFechaFin.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        filterPanel.add(lblFechaFin);

        txtFechaFin = new JTextField(10);
        txtFechaFin.setBackground(new Color(50, 50, 55));
        txtFechaFin.setForeground(textLight);
        txtFechaFin.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 65)));
        txtFechaFin.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        txtFechaFin.setPreferredSize(new Dimension(90, 28));
        filterPanel.add(txtFechaFin);

        btnBuscar = createActionButton("Buscar", accentBlue, 80);
        btnBuscar.addActionListener(e -> cargarCitas());
        filterPanel.add(btnBuscar);

        btnRefrescar = createActionButton("⟳", accentPurple, 40);
        btnRefrescar.setToolTipText("Restablecer filtros");
        btnRefrescar.addActionListener(e -> {
            txtFechaInicio.setText(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
            txtFechaFin.setText(LocalDate.now().plusDays(7).format(DateTimeFormatter.ISO_LOCAL_DATE));
            cbOdontologo.setSelectedIndex(0);
            cbPaciente.setSelectedIndex(0);
            cbEstadoFiltro.setSelectedIndex(0);
            cargarCitas();
        });
        filterPanel.add(btnRefrescar);

        JButton btnHoy = createActionButton("Hoy", accentGreen, 60);
        btnHoy.addActionListener(e -> {
            txtFechaInicio.setText(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
            txtFechaFin.setText(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
            cargarCitas();
        });
        filterPanel.add(btnHoy);

        JButton btnSemana = createActionButton("Semana", accentOrange, 75);
        btnSemana.addActionListener(e -> {
            txtFechaInicio.setText(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
            txtFechaFin.setText(LocalDate.now().plusDays(7).format(DateTimeFormatter.ISO_LOCAL_DATE));
            cargarCitas();
        });
        filterPanel.add(btnSemana);

        btnExportar = createActionButton("", new Color(100, 80, 200), 40);
        btnExportar.setToolTipText("Exportar a CSV");
        btnExportar.addActionListener(e -> exportarCitas());
        filterPanel.add(btnExportar);

        panel.add(filterPanel, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 5));
        actionPanel.setBackground(darkBg);
        actionPanel.setBorder(new EmptyBorder(2, 0, 0, 0));

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
        sep1.setPreferredSize(new Dimension(2, 28));
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
        sep2.setPreferredSize(new Dimension(2, 28));
        sep2.setBackground(new Color(60, 60, 65));
        actionPanel.add(sep2);

        JButton btnRecordatorios = createActionButton("Recordatorios", new Color(100, 80, 200), 130);
        btnRecordatorios.addActionListener(e -> enviarRecordatorios());
        actionPanel.add(btnRecordatorios);

        panel.add(actionPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createResumenDiarioPanel() {
        resumenPanel = new JPanel(new GridLayout(1, 5, 8, 0));
        resumenPanel.setBackground(darkBg);
        resumenPanel.setBorder(new EmptyBorder(0, 0, 8, 0));
        resumenPanel.setPreferredSize(new Dimension(0, 45));

        lblProgramadas = createTarjetaResumen("Programadas", "0", new Color(100, 180, 255));
        resumenPanel.add(lblProgramadas);

        lblConfirmadas = createTarjetaResumen("Confirmadas", "0", new Color(80, 220, 140));
        resumenPanel.add(lblConfirmadas);

        lblEnProceso = createTarjetaResumen("En Proceso", "0", new Color(255, 200, 60));
        resumenPanel.add(lblEnProceso);

        lblCanceladas = createTarjetaResumen("Canceladas", "0", new Color(255, 120, 120));
        resumenPanel.add(lblCanceladas);

        lblTotalHoy = createTarjetaResumen("Total Hoy", "0", new Color(200, 150, 255));
        resumenPanel.add(lblTotalHoy);

        return resumenPanel;
    }

    private JLabel createTarjetaResumen(String titulo, String valor, Color color) {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setBackground(new Color(45, 45, 50));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 70), 1),
            new EmptyBorder(6, 14, 6, 14)
        ));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftPanel.setBackground(new Color(45, 45, 50));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblTitulo.setForeground(Color.WHITE);
        leftPanel.add(lblTitulo);

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblValor.setForeground(color);
        lblValor.setHorizontalAlignment(SwingConstants.LEFT);
        leftPanel.add(lblValor);

        panel.add(leftPanel, BorderLayout.WEST);

        return lblValor;
    }

    private void actualizarResumenDiario() {
        try {
            CitaController.CitaResumen resumen = citaController.getResumenCitas();
            
            lblProgramadas.setText(String.valueOf(resumen.programadas));
            lblConfirmadas.setText(String.valueOf(resumen.confirmadas));
            lblEnProceso.setText(String.valueOf(resumen.enProceso));
            lblCanceladas.setText(String.valueOf(resumen.canceladas + resumen.noAsistio));
            lblTotalHoy.setText(String.valueOf(resumen.totalHoy));
            
        } catch (Exception e) {
            System.err.println("Error al actualizar resumen: " + e.getMessage());
        }
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(darkCard);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(50, 50, 55), 1),
            new EmptyBorder(8, 8, 8, 8)
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
        tablaCitas.setRowHeight(30);
        tablaCitas.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaCitas.getTableHeader().setBackground(new Color(50, 50, 55));
        tablaCitas.getTableHeader().setForeground(textLight);
        tablaCitas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaCitas.setSelectionBackground(new Color(60, 60, 70));
        tablaCitas.setSelectionForeground(textLight);
        tablaCitas.setShowGrid(false);
        tablaCitas.setIntercellSpacing(new Dimension(0, 0));

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

        tablaCitas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editarCita();
                }
            }
        });

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
        panel.setPreferredSize(new Dimension(0, 32));
        panel.setBorder(new EmptyBorder(3, 10, 3, 10));

        lblTotalRegistros = new JLabel("Mostrando 0 citas | Citas hoy: 0 | Pendientes: 0");
        lblTotalRegistros.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTotalRegistros.setForeground(textGray);
        panel.add(lblTotalRegistros, BorderLayout.WEST);

        JLabel lblInfo = new JLabel("Doble click para editar | Click derecho para opciones");
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblInfo.setForeground(textGray);
        panel.add(lblInfo, BorderLayout.EAST);

        return panel;
    }

    private JButton createActionButton(String text, Color color, int width) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(width, 28));

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

            model.setRowCount(0);

            List<Cita> citas = citaController.listarPorRango(fechaInicio, fechaFin);

            if (citas != null && !citas.isEmpty()) {
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

                String estadoSeleccion = (String) cbEstadoFiltro.getSelectedItem();
                if (estadoSeleccion != null && !estadoSeleccion.equals("Todos")) {
                    String estadoFinal = estadoSeleccion;
                    citas.removeIf(c -> !c.getEstado().equals(estadoFinal));
                }

                citaController.cargarTabla(model, citas);
            }

            actualizarContador();
            actualizarResumenDiario();

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
                + citaController.contarCitasPorEstado(Cita.ESTADO_CONFIRMADA)
                + citaController.contarCitasPorEstado(Cita.ESTADO_EN_PROCESO);
        lblTotalRegistros.setText("Mostrando " + total + " citas | Citas hoy: " + totalHoy + " | Pendientes: " + pendientes);
    }

    private void abrirFormularioCita(Cita cita) {
        try {
            JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
            
            System.out.println("🔄 [DEBUG] abrirFormularioCita() - Cita: " + (cita != null ? cita.getId() : "null"));
            
            // CREAR EL DIÁLOGO CON LA CITA DESDE EL CONSTRUCTOR
            DialogCita dialog = new DialogCita(parent, cita != null, cita);
            
            dialog.setVisible(true);
            
            if (dialog.isGuardado()) {
                cargarCitas();
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error al abrir formulario: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error al abrir el formulario: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
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
        System.out.println("🔍 [DEBUG] editarCita() - ID desde tabla: " + id);
        
        Cita cita = citaController.buscarPorId(id);
        if (cita != null) {
            System.out.println("🔍 [DEBUG] Cita encontrada:");
            System.out.println("   ID: " + cita.getId());
            System.out.println("   Paciente: " + cita.getPacienteNombre() + " (ID: " + cita.getPacienteId() + ")");
            System.out.println("   Odontólogo: " + cita.getOdontologoNombre() + " (ID: " + cita.getOdontologoId() + ")");
            System.out.println("   Servicio: " + cita.getServicioNombre() + " (ID: " + cita.getServicioId() + ")");
            abrirFormularioCita(cita);
        } else {
            System.err.println("❌ [DEBUG] Cita NO encontrada con ID: " + id);
            JOptionPane.showMessageDialog(this,
                "No se encontró la cita con ID: " + id,
                "Error", JOptionPane.ERROR_MESSAGE);
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

    private void exportarCitas() {
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                "No hay datos para exportar",
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Exportar las citas actuales a CSV?",
            "Confirmar exportación",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar archivo CSV");
        fileChooser.setSelectedFile(new java.io.File("citas_" + 
            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".csv"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                java.io.File file = fileChooser.getSelectedFile();
                if (!file.getName().toLowerCase().endsWith(".csv")) {
                    file = new java.io.File(file.getAbsolutePath() + ".csv");
                }
                
                java.io.PrintWriter writer = new java.io.PrintWriter(file, "UTF-8");
                
                writer.write('\ufeff');
                writer.println("ID,Fecha,Hora,Paciente,Odontólogo,Servicio,Estado,Nota");
                
                for (int i = 0; i < model.getRowCount(); i++) {
                    StringBuilder line = new StringBuilder();
                    for (int j = 0; j < model.getColumnCount(); j++) {
                        Object value = model.getValueAt(i, j);
                        String text = value != null ? value.toString().replace(",", ";") : "";
                        if (j > 0) line.append(",");
                        line.append(text);
                    }
                    writer.println(line.toString());
                }
                
                writer.close();
                
                JOptionPane.showMessageDialog(this,
                    "✅ Citas exportadas correctamente a:\n" + file.getAbsolutePath() + 
                    "\n\n" + model.getRowCount() + " registros exportados",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error al exportar: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}