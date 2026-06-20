package com.dentalclinicsystem.view;

import com.dentalclinicsystem.controller.ServicioController;
import com.dentalclinicsystem.model.Servicio;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class PanelServicios extends JPanel {
    
    private ServicioController controller;
    private JTable tablaServicios;
    private DefaultTableModel model;
    private JTextField txtBuscar;
    private JButton btnNuevo, btnEditar, btnEliminar, btnBuscar, btnRefrescar;
    private JLabel lblTotalRegistros;
    private JComboBox<String> cbFiltro;
    private List<Servicio> listaServiciosActual;
    
    // Colores del tema oscuro
    private Color darkBg = new Color(30, 30, 35);
    private Color darkCard = new Color(40, 40, 45);
    private Color textLight = new Color(220, 220, 230);
    private Color textGray = new Color(150, 150, 165);
    private Color accentBlue = new Color(70, 130, 200);
    private Color accentGreen = new Color(60, 180, 110);
    private Color accentRed = new Color(210, 80, 80);
    private Color accentOrange = new Color(230, 160, 50);
    private Color accentPurple = new Color(150, 80, 200);
    
    public PanelServicios() {
        this.controller = new ServicioController();
        initComponents();
        cargarServicios();
        actualizarContador();
    }
    
    private void initComponents() {
        setBackground(darkBg);
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // -------------------------------Panel superior con título y botones-----------------------------//
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // ------------------------------------Panel central con la tabla---------------------------------//
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);
        
        // ---------------------------------Panel inferior con el contador-------------------------------//
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
        
        configurarTeclasRapidas();
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(darkBg);
        panel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        //----------------------------------------TÍTULO ------------------------------------------------//
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setBackground(darkBg);
        
        JLabel titleLabel = new JLabel("Servicios Odontológicos");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(textLight);
        titlePanel.add(titleLabel);
        
        panel.add(titlePanel, BorderLayout.NORTH); // Título arriba
        
        // -----------------------------PANEL DE BÚSQUEDA Y BOTONES----------------------------//
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8)); 
        actionPanel.setBackground(darkBg);
        actionPanel.setBorder(new EmptyBorder(5, 0, 0, 0));
        
        // -------------------------------------Filtro busaueda----------------------------------------//
        cbFiltro = new JComboBox<>(new String[]{"Todos", "Nombre", "Categoría"});
        cbFiltro.setBackground(new Color(50, 50, 55));
        cbFiltro.setForeground(textLight);
        cbFiltro.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbFiltro.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 65)));
        cbFiltro.setPreferredSize(new Dimension(100, 32));
        actionPanel.add(cbFiltro);
        
        // -------------------------------------Campo de búsqueda---------------------------------//
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
        txtBuscar.addActionListener(e -> buscarServicios());
        txtBuscar.addKeyListener(new KeyAdapter() {
            private javax.swing.Timer timer;
            @Override
            public void keyReleased(KeyEvent e) {
                if (txtBuscar.getText().trim().isEmpty()) {
                    cargarServicios();
                    return;
                }
                if (timer != null && timer.isRunning()) {
                    timer.stop();
                }
                timer = new javax.swing.Timer(300, evt -> buscarServicios());
                timer.setRepeats(false);
                timer.start();
            }
        });
        actionPanel.add(txtBuscar);
        
        // ------------------------------------------Botón Buscar----------------------------------//
        btnBuscar = createActionButton("Buscar", accentBlue);
        btnBuscar.addActionListener(e -> buscarServicios());
        actionPanel.add(btnBuscar);
        
        // ----------------------------------------Botón Refrescar---------------------------------//
        btnRefrescar = createActionButton("Refrescar", accentPurple);
        btnRefrescar.addActionListener(e -> {
            txtBuscar.setText("");
            cargarServicios();
        });
        actionPanel.add(btnRefrescar);
        
        // ------------------------------------------Separador------------------------------------//
        JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
        separator.setPreferredSize(new Dimension(1, 30));
        separator.setBackground(new Color(60, 60, 65));
        actionPanel.add(separator);
        
        // --------------------------------------Botón Nuevo-------------------------------------//
        btnNuevo = createActionButton("Nuevo", accentGreen);
        btnNuevo.addActionListener(e -> abrirFormularioServicio(null));
        actionPanel.add(btnNuevo);
        
        //-------------------------------------- Botón Editar-------------------------------------//
        btnEditar = createActionButton("Editar", accentBlue);
        btnEditar.addActionListener(e -> editarServicio());
        actionPanel.add(btnEditar);
        
        // -------------------------------------Botón Eliminar-----------------------------------//
        btnEliminar = createActionButton("🗑️ Eliminar", accentRed);
        btnEliminar.addActionListener(e -> eliminarServicio());
        actionPanel.add(btnEliminar);
        //--------------------------------- Botones debajo del titulo ---------------------------//
        panel.add(actionPanel, BorderLayout.CENTER); 
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(darkCard);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(50, 50, 55), 1),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        // ------------------------------------Columnas de la tabla---------------------------//
        String[] columnas = {"ID", "Nombre", "Categoría", "Subcategoría", "Duración", "Precio", "Estado", "Código"};
        model = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaServicios = new JTable(model);
        tablaServicios.setBackground(new Color(45, 45, 50));
        tablaServicios.setForeground(textLight);
        tablaServicios.setGridColor(new Color(55, 55, 60));
        tablaServicios.setRowHeight(32);
        tablaServicios.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaServicios.getTableHeader().setBackground(new Color(50, 50, 55));
        tablaServicios.getTableHeader().setForeground(textLight);
        tablaServicios.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tablaServicios.setSelectionBackground(new Color(60, 60, 70));
        tablaServicios.setSelectionForeground(textLight);
        tablaServicios.setShowGrid(false);
        tablaServicios.setIntercellSpacing(new Dimension(0, 0));
        
        // --------------------------Renderer para colores de estado-------------------------------------//
        tablaServicios.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (column == 6) {
                    String estado = (String) value;
                    if (estado != null) {
                        if (estado.equals("Activo")) {
                            c.setBackground(new Color(60, 180, 110));
                            c.setForeground(Color.WHITE);
                        } else {
                            c.setBackground(new Color(210, 80, 80));
                            c.setForeground(Color.WHITE);
                        }
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
        
        // --------------------------------------Doble click para editar----------------------------------//
        tablaServicios.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editarServicio();
                }
            }
        });
        
        // -----------------------------------------Menú contextual------------------------------------//
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem itemEditar = new JMenuItem("Editar");
        itemEditar.addActionListener(e -> editarServicio());
        popupMenu.add(itemEditar);
        
        JMenuItem itemEliminar = new JMenuItem("Eliminar");
        itemEliminar.addActionListener(e -> eliminarServicio());
        popupMenu.add(itemEliminar);
        
        tablaServicios.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = tablaServicios.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        tablaServicios.setRowSelectionInterval(row, row);
                        popupMenu.show(tablaServicios, e.getX(), e.getY());
                    }
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = tablaServicios.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        tablaServicios.setRowSelectionInterval(row, row);
                        popupMenu.show(tablaServicios, e.getX(), e.getY());
                    }
                }
            }
        });
        
        JScrollPane scroll = new JScrollPane(tablaServicios);
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
        
        lblTotalRegistros = new JLabel("Total: 0 servicios");
        lblTotalRegistros.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTotalRegistros.setForeground(textGray);
        panel.add(lblTotalRegistros, BorderLayout.WEST);
        
        JLabel lblInfo = new JLabel("Doble click para editar | Click derecho para opciones.");
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblInfo.setForeground(textGray);
        panel.add(lblInfo, BorderLayout.EAST);
        
        return panel;
    }
    
    // ------------------------------Crea un botón con estilo consistente--------------------------------//
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
    
    // -----------------------------------Configura las teclas rápidas del sistema----------------------//
    private void configurarTeclasRapidas() {
        InputMap inputMap = tablaServicios.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap actionMap = tablaServicios.getActionMap();
        
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK), "nuevo");
        actionMap.put("nuevo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirFormularioServicio(null);
            }
        });
        
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_DOWN_MASK), "editar");
        actionMap.put("editar", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editarServicio();
            }
        });
        
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.CTRL_DOWN_MASK), "eliminar");
        actionMap.put("eliminar", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarServicio();
            }
        });
        
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK), "buscar");
        actionMap.put("buscar", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtBuscar.requestFocus();
                txtBuscar.selectAll();
            }
        });
        
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "refrescar");
        actionMap.put("refrescar", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtBuscar.setText("");
                cargarServicios();
            }
        });
    }
    
    // -------------------Carga todos los servicios desde la base de datos---------------------------//
    private void cargarServicios() {
        try {
            listaServiciosActual = controller.listarTodos();
            controller.cargarTabla(model, listaServiciosActual);
            actualizarContador();
        } catch (Exception e) {
            System.err.println("Error al cargar servicios: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error al cargar servicios: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // -------------------------------Busca servicios por texto----------------------------------------//
    private void buscarServicios() {
        String texto = txtBuscar.getText().trim();
        
        if (texto.isEmpty()) {
            cargarServicios();
            return;
        }
        
        try {
            List<Servicio> resultados = controller.buscar(texto);
            controller.cargarTabla(model, resultados);
            actualizarContador();
            
            if (resultados == null || resultados.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No se encontraron servicios con: '" + texto + "'",
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
    
    // -----------------------------Actualiza el contador de registros------------------------------------//
    private void actualizarContador() {
        int total = model.getRowCount();
        int totalGeneral = controller.contarServicios();
        lblTotalRegistros.setText("Mostrando " + total + " de " + totalGeneral + " servicios");
    }
    
    // ------------------Abre el formulario para crear o editar un servicio-----------------------------//
    private void abrirFormularioServicio(Servicio servicio) {
        DialogServicio dialog = new DialogServicio((JFrame) SwingUtilities.getWindowAncestor(this), servicio != null);
        dialog.setServicio(servicio);
        dialog.setVisible(true);
        
        if (dialog.isGuardado()) {
            cargarServicios();
        }
    }
    
    // --------------------------Obtiene el servicio seleccionado y abre para editar-------------------//
    private void editarServicio() {
        int row = tablaServicios.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione un servicio para editar", 
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (int) model.getValueAt(row, 0);
        Servicio servicio = controller.buscarPorId(id);
        if (servicio != null) {
            abrirFormularioServicio(servicio);
        }
    }
    
    // --------------------------------Elimina el servicio seleccionado---------------------------------//
    private void eliminarServicio() {
        int row = tablaServicios.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione un servicio para eliminar", 
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String nombre = (String) model.getValueAt(row, 1);
        int id = (int) model.getValueAt(row, 0);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de eliminar el servicio?\n\n" +
            "Nombre: " + nombre + "\n\n" +
            "⚠️ Esta acción no se puede deshacer",
            "Confirmar Eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (controller.eliminarServicio(id)) {
                cargarServicios();
            }
        }
    }
}