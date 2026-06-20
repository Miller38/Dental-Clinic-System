package com.dentalclinicsystem.view;

import com.dentalclinicsystem.controller.PacienteController;
import com.dentalclinicsystem.model.Paciente;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class PanelPacientes extends JPanel {
    
    private PacienteController controller;
    private JTable tablaPacientes;
    private DefaultTableModel model;
    private JTextField txtBuscar;
    private JButton btnNuevo, btnEditar, btnEliminar, btnBuscar, btnRefrescar;
    private JLabel lblTotalRegistros;
    private JComboBox<String> cbFiltro;
    private List<Paciente> listaPacientesActual;
    
    // Colores del tema oscuro
    private Color darkBg = new Color(30, 30, 35);
    private Color darkCard = new Color(40, 40, 45);
    private Color textLight = new Color(220, 220, 230);
    private Color textGray = new Color(150, 150, 165);
    private Color accentBlue = new Color(70, 130, 200);
    private Color accentGreen = new Color(60, 180, 110);
    private Color accentRed = new Color(210, 80, 80);
    private Color accentPurple = new Color(150, 80, 200);
    
    public PanelPacientes() {
        this.controller = new PacienteController();
        initComponents();
        cargarPacientes();
        actualizarContador();
    }
    
    private void initComponents() {
        setBackground(darkBg);
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Panel superior con título y botones
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Panel central con la tabla
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);
        
        // Panel inferior con el contador
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(darkBg);
        panel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        // ===== TÍTULO =====
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setBackground(darkBg);
        
        JLabel titleLabel = new JLabel("Gestión de Pacientes");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(textLight);
        titlePanel.add(titleLabel);
        
        panel.add(titlePanel, BorderLayout.NORTH); // Título arriba
        
        // ===== PANEL DE BÚSQUEDA Y BOTONES =====
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8)); // Alineado a la izquierda
        actionPanel.setBackground(darkBg);
        actionPanel.setBorder(new EmptyBorder(5, 0, 0, 0));
        
        // Filtro
        cbFiltro = new JComboBox<>(new String[]{"Todos", "Nombre", "Documento", "Teléfono"});
        cbFiltro.setBackground(new Color(50, 50, 55));
        cbFiltro.setForeground(textLight);
        cbFiltro.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbFiltro.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 65)));
        cbFiltro.setPreferredSize(new Dimension(100, 32));
        actionPanel.add(cbFiltro);
        
        // Campo de búsqueda
        txtBuscar = new JTextField(20);
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtBuscar.setPreferredSize(new Dimension(200, 32));
        txtBuscar.setBackground(new Color(50, 50, 55));
        txtBuscar.setForeground(textLight);
        txtBuscar.setCaretColor(textLight);
        txtBuscar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 65)),
            new EmptyBorder(0, 12, 0, 12)
        ));
        txtBuscar.addActionListener(e -> buscarPacientes());
        txtBuscar.addKeyListener(new KeyAdapter() {
            private javax.swing.Timer timer;
            @Override
            public void keyReleased(KeyEvent e) {
                if (txtBuscar.getText().trim().isEmpty()) {
                    cargarPacientes();
                    return;
                }
                if (timer != null && timer.isRunning()) {
                    timer.stop();
                }
                timer = new javax.swing.Timer(300, evt -> buscarPacientes());
                timer.setRepeats(false);
                timer.start();
            }
        });
        actionPanel.add(txtBuscar);
        
        // Botón Buscar
        btnBuscar = createActionButton("Buscar", accentBlue);
        btnBuscar.addActionListener(e -> buscarPacientes());
        actionPanel.add(btnBuscar);
        
        // Botón Refrescar
        btnRefrescar = createActionButton("Refrescar", accentPurple);
        btnRefrescar.addActionListener(e -> {
            txtBuscar.setText("");
            cargarPacientes();
        });
        actionPanel.add(btnRefrescar);
        
        // Separador
        JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
        separator.setPreferredSize(new Dimension(1, 30));
        separator.setBackground(new Color(60, 60, 65));
        actionPanel.add(separator);
        
        // Botón Nuevo
        btnNuevo = createActionButton("Nuevo", accentGreen);
        btnNuevo.addActionListener(e -> abrirFormularioPaciente(null));
        actionPanel.add(btnNuevo);
        
        // Botón Editar
        btnEditar = createActionButton("Editar", accentBlue);
        btnEditar.addActionListener(e -> editarPaciente());
        actionPanel.add(btnEditar);
        
        // Botón Eliminar
        btnEliminar = createActionButton(" Eliminar", accentRed);
        btnEliminar.addActionListener(e -> eliminarPaciente());
        actionPanel.add(btnEliminar);
        
        panel.add(actionPanel, BorderLayout.CENTER); // Botones debajo del título
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(darkCard);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(50, 50, 55), 1),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        // Columnas de la tabla
        String[] columnas = {"ID", "Nombre Completo", "Documento", "Teléfono", "Email", "Género", "Edad"};
        model = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaPacientes = new JTable(model);
        tablaPacientes.setBackground(new Color(45, 45, 50));
        tablaPacientes.setForeground(textLight);
        tablaPacientes.setGridColor(new Color(55, 55, 60));
        tablaPacientes.setRowHeight(32);
        tablaPacientes.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaPacientes.getTableHeader().setBackground(new Color(50, 50, 55));
        tablaPacientes.getTableHeader().setForeground(textLight);
        tablaPacientes.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tablaPacientes.setSelectionBackground(new Color(60, 60, 70));
        tablaPacientes.setSelectionForeground(textLight);
        tablaPacientes.setShowGrid(false);
        tablaPacientes.setIntercellSpacing(new Dimension(0, 0));
        
        // Doble click para editar
        tablaPacientes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editarPaciente();
                }
            }
        });
        
        // Menú contextual
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem itemEditar = new JMenuItem("Editar");
        itemEditar.addActionListener(e -> editarPaciente());
        popupMenu.add(itemEditar);
        
        JMenuItem itemEliminar = new JMenuItem("Eliminar");
        itemEliminar.addActionListener(e -> eliminarPaciente());
        popupMenu.add(itemEliminar);
        
        popupMenu.addSeparator();
        
        JMenuItem itemVerDetalle = new JMenuItem("Ver Detalle");
        itemVerDetalle.addActionListener(e -> verDetallePaciente());
        popupMenu.add(itemVerDetalle);
        
        tablaPacientes.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = tablaPacientes.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        tablaPacientes.setRowSelectionInterval(row, row);
                        popupMenu.show(tablaPacientes, e.getX(), e.getY());
                    }
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = tablaPacientes.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        tablaPacientes.setRowSelectionInterval(row, row);
                        popupMenu.show(tablaPacientes, e.getX(), e.getY());
                    }
                }
            }
        });
        
        JScrollPane scroll = new JScrollPane(tablaPacientes);
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
        
        JLabel lblInfo = new JLabel("Doble click para editar | Click derecho para opciones | Ctrl+N Nuevo | Ctrl+E Editar | Ctrl+D Eliminar | Ctrl+F Buscar | F5 Refrescar");
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblInfo.setForeground(textGray);
        panel.add(lblInfo, BorderLayout.EAST);
        
        return panel;
    }
    
    // Crea un botón con estilo consistente
    private JButton createActionButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(95, 32));
        
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
    
    // Carga todos los pacientes desde la base de datos
    private void cargarPacientes() {
        try {
            listaPacientesActual = controller.listarTodos();
            cargarTablaConLista(listaPacientesActual);
            actualizarContador();
        } catch (Exception e) {
            System.err.println("Error al cargar pacientes: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error al cargar pacientes: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Carga la tabla con una lista de pacientes
    private void cargarTablaConLista(List<Paciente> pacientes) {
        model.setRowCount(0);
        
        if (pacientes == null || pacientes.isEmpty()) {
            return;
        }
        
        for (Paciente p : pacientes) {
            model.addRow(new Object[]{
                p.getId(),
                p.getNombreCompleto(),
                p.getNumeroDocumento(),
                p.getTelefono(),
                p.getEmail() != null ? p.getEmail() : "",
                p.getGenero() != null ? p.getGenero() : "",
                p.getEdad() > 0 ? p.getEdad() : ""
            });
        }
    }
    
    // Busca pacientes por texto y filtro
    private void buscarPacientes() {
        String texto = txtBuscar.getText().trim();
        String filtro = (String) cbFiltro.getSelectedItem();
        
        if (texto.isEmpty()) {
            cargarPacientes();
            return;
        }
        
        try {
            List<Paciente> resultados = controller.buscar(texto);
            
            if (!"Todos".equals(filtro) && resultados != null && !resultados.isEmpty()) {
                String textoLower = texto.toLowerCase();
                List<Paciente> filtrados = new java.util.ArrayList<>();
                
                for (Paciente p : resultados) {
                    boolean coincide = false;
                    switch (filtro) {
                        case "Nombre":
                            coincide = p.getNombreCompleto().toLowerCase().contains(textoLower);
                            break;
                        case "Documento":
                            coincide = p.getNumeroDocumento().contains(texto);
                            break;
                        case "Teléfono":
                            coincide = p.getTelefono().contains(texto);
                            if (p.getTelefonoAlternativo() != null) {
                                coincide = coincide || p.getTelefonoAlternativo().contains(texto);
                            }
                            break;
                        default:
                            coincide = true;
                            break;
                    }
                    if (coincide) {
                        filtrados.add(p);
                    }
                }
                resultados = filtrados;
            }
            
            cargarTablaConLista(resultados);
            actualizarContador();
            
            if (resultados == null || resultados.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No se encontraron pacientes con: '" + texto + "'",
                    "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (Exception e) {
            System.err.println("Error al buscar: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error al buscar: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Actualiza el contador de registros
    private void actualizarContador() {
        int total = model.getRowCount();
        int totalGeneral = controller.contarPacientes();
        lblTotalRegistros.setText("Mostrando " + total + " de " + totalGeneral + " registros");
    }
    
    // Abre el formulario para crear o editar un paciente
    private void abrirFormularioPaciente(Paciente paciente) {
        DialogPaciente dialog = new DialogPaciente((JFrame) SwingUtilities.getWindowAncestor(this), paciente != null);
        dialog.setPaciente(paciente);
        dialog.setVisible(true);
        
        if (dialog.isGuardado()) {
            cargarPacientes();
        }
    }
    
    // Obtiene el paciente seleccionado y abre para editar
    private void editarPaciente() {
        int row = tablaPacientes.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione un paciente para editar", 
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (int) model.getValueAt(row, 0);
        Paciente paciente = controller.buscarPorId(id);
        if (paciente != null) {
            abrirFormularioPaciente(paciente);
        }
    }
    
    // Elimina el paciente seleccionado
    private void eliminarPaciente() {
        int row = tablaPacientes.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione un paciente para eliminar", 
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String nombre = (String) model.getValueAt(row, 1);
        String documento = (String) model.getValueAt(row, 2);
        int id = (int) model.getValueAt(row, 0);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de eliminar al paciente?\n\n" +
            "Nombre: " + nombre + "\n" +
            "Documento: " + documento + "\n\n" +
            "⚠️ Esta acción no se puede deshacer",
            "Confirmar Eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (controller.eliminarPaciente(id)) {
                cargarPacientes();
            }
        }
    }
    
    // Muestra el detalle del paciente seleccionado
    private void verDetallePaciente() {
        int row = tablaPacientes.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione un paciente para ver detalles", 
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (int) model.getValueAt(row, 0);
        Paciente paciente = controller.buscarPorId(id);
        if (paciente != null) {
            mostrarDetallePaciente(paciente);
        }
    }
    
    // Muestra un diálogo con el detalle completo del paciente
    private void mostrarDetallePaciente(Paciente p) {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Detalle del Paciente", true);
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(darkBg);
        tabbedPane.setForeground(textLight);
        
        tabbedPane.addTab("📋 Información", crearPanelInfoPersonal(p));
        tabbedPane.addTab("🏥 Datos Médicos", crearPanelDatosMedicos(p));
        
        dialog.add(tabbedPane, BorderLayout.CENTER);
        
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setBackground(accentBlue);
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setBorderPainted(false);
        btnCerrar.addActionListener(e -> dialog.dispose());
        
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(darkBg);
        btnPanel.add(btnCerrar);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    // Crea el panel de información personal
    private JPanel crearPanelInfoPersonal(Paciente p) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(darkBg);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        
        JLabel nombreLabel = new JLabel(" " + p.getNombreCompleto());
        nombreLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        nombreLabel.setForeground(textLight);
        panel.add(nombreLabel, gbc);
        
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        
        String[][] datos = {
            {" Documento:", p.getNumeroDocumento()},
            {" Teléfono:", p.getTelefono()},
            {" Email:", p.getEmail() != null ? p.getEmail() : "No registrado"},
            {" Dirección:", p.getDireccion() != null ? p.getDireccion() : "No registrada"},
            {" Género:", p.getGenero() != null ? p.getGenero() : "No registrado"},
            {" Edad:", p.getEdad() > 0 ? p.getEdad() + " años" : "No registrada"},
            {" Estado Civil:", p.getEstadoCivil() != null ? p.getEstadoCivil() : "No registrado"},
            {" Ocupación:", p.getOcupacion() != null ? p.getOcupacion() : "No registrada"},
            {" Registro:", p.getFechaRegistro() != null ? p.getFechaRegistro() : "No registrada"}
        };
        
        int y = 1;
        for (String[] dato : datos) {
            gbc.gridy = y;
            gbc.gridx = 0;
            gbc.weightx = 0.3;
            
            JLabel label = new JLabel(dato[0]);
            label.setFont(new Font("Segoe UI", Font.BOLD, 13));
            label.setForeground(textGray);
            panel.add(label, gbc);
            
            gbc.gridx = 1;
            gbc.weightx = 0.7;
            
            JLabel value = new JLabel(dato[1]);
            value.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            value.setForeground(textLight);
            panel.add(value, gbc);
            
            y++;
        }
        
        return panel;
    }
    
    // Crea el panel de datos médicos
    private JPanel crearPanelDatosMedicos(Paciente p) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(darkBg);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        
        // Alergias
        JLabel lblAlergias = new JLabel("️ Alergias:");
        lblAlergias.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblAlergias.setForeground(textGray);
        panel.add(lblAlergias, gbc);
        gbc.gridy++;
        
        JTextArea alergias = new JTextArea(p.getAlergias() != null && !p.getAlergias().isEmpty() ? p.getAlergias() : "Ninguna");
        alergias.setEditable(false);
        alergias.setBackground(new Color(50, 50, 55));
        alergias.setForeground(textLight);
        alergias.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 65)));
        JScrollPane scrollAlergias = new JScrollPane(alergias);
        scrollAlergias.setPreferredSize(new Dimension(300, 60));
        panel.add(scrollAlergias, gbc);
        
        // Enfermedades
        gbc.gridy++;
        JLabel lblEnfermedades = new JLabel(" Enfermedades:");
        lblEnfermedades.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblEnfermedades.setForeground(textGray);
        panel.add(lblEnfermedades, gbc);
        gbc.gridy++;
        
        JTextArea enfermedades = new JTextArea(p.getEnfermedadesSistema() != null && !p.getEnfermedadesSistema().isEmpty() ? p.getEnfermedadesSistema() : "Ninguna");
        enfermedades.setEditable(false);
        enfermedades.setBackground(new Color(50, 50, 55));
        enfermedades.setForeground(textLight);
        enfermedades.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 65)));
        JScrollPane scrollEnfermedades = new JScrollPane(enfermedades);
        scrollEnfermedades.setPreferredSize(new Dimension(300, 60));
        panel.add(scrollEnfermedades, gbc);
        
        // Medicamentos
        gbc.gridy++;
        JLabel lblMedicamentos = new JLabel(" Medicamentos:");
        lblMedicamentos.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblMedicamentos.setForeground(textGray);
        panel.add(lblMedicamentos, gbc);
        gbc.gridy++;
        
        JTextArea medicamentos = new JTextArea(p.getMedicamentos() != null && !p.getMedicamentos().isEmpty() ? p.getMedicamentos() : "Ninguno");
        medicamentos.setEditable(false);
        medicamentos.setBackground(new Color(50, 50, 55));
        medicamentos.setForeground(textLight);
        medicamentos.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 65)));
        JScrollPane scrollMedicamentos = new JScrollPane(medicamentos);
        scrollMedicamentos.setPreferredSize(new Dimension(300, 60));
        panel.add(scrollMedicamentos, gbc);
        
        // Contacto de emergencia
        gbc.gridy++;
        JLabel lblContacto = new JLabel(" Contacto Emergencia:");
        lblContacto.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblContacto.setForeground(textGray);
        panel.add(lblContacto, gbc);
        gbc.gridy++;
        
        JPanel contactoPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        contactoPanel.setBackground(darkBg);
        
        JLabel lblContactoNombre = new JLabel("Nombre: " + (p.getContactoEmergenciaNombre() != null ? p.getContactoEmergenciaNombre() : "No registrado"));
        lblContactoNombre.setForeground(textLight);
        contactoPanel.add(lblContactoNombre);
        
        JLabel lblContactoTelefono = new JLabel("Teléfono: " + (p.getContactoEmergenciaTelefono() != null ? p.getContactoEmergenciaTelefono() : "No registrado"));
        lblContactoTelefono.setForeground(textLight);
        contactoPanel.add(lblContactoTelefono);
        
        panel.add(contactoPanel, gbc);
        
        return panel;
    }
}