package com.dentalclinicsystem.view;

import com.dentalclinicsystem.controller.InsumoController;
import com.dentalclinicsystem.model.Insumo;
import com.dentalclinicsystem.model.Movimiento;

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

public class PanelInventario extends JPanel {
    
    private InsumoController controller;
    private JTable tablaInsumos;
    private DefaultTableModel model;
    private JTextField txtBuscar;
    private JButton btnNuevo, btnEditar, btnEliminar, btnBuscar, btnRefrescar;
    private JButton btnMovimiento, btnVerMovimientos;
    private JLabel lblTotalRegistros, lblStockBajo;
    
    private Color darkBg = new Color(30, 30, 35);
    private Color darkCard = new Color(40, 40, 45);
    private Color textLight = new Color(220, 220, 230);
    private Color textGray = new Color(150, 150, 165);
    private Color accentBlue = new Color(70, 130, 200);
    private Color accentGreen = new Color(60, 180, 110);
    private Color accentRed = new Color(210, 80, 80);
    private Color accentOrange = new Color(230, 160, 50);
    private Color accentPurple = new Color(150, 80, 200);
    
    public PanelInventario() {
        this.controller = new InsumoController();
        initComponents();
        cargarInsumos();
        actualizarContador();
    }
    
    private void initComponents() {
        setBackground(darkBg);
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);
        
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
        
        configurarTeclasRapidas();
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(darkBg);
        panel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setBackground(darkBg);
        titlePanel.setPreferredSize(new Dimension(0, 40));
        
        JLabel titleLabel = new JLabel(" Gestión de Inventario");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(textLight);
        titlePanel.add(titleLabel);
        panel.add(titlePanel, BorderLayout.NORTH);
        
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        actionPanel.setBackground(darkBg);
        
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
        txtBuscar.addActionListener(e -> buscarInsumos());
        txtBuscar.addKeyListener(new KeyAdapter() {
            private javax.swing.Timer timer;
            @Override
            public void keyReleased(KeyEvent e) {
                if (txtBuscar.getText().trim().isEmpty()) {
                    cargarInsumos();
                    return;
                }
                if (timer != null && timer.isRunning()) {
                    timer.stop();
                }
                timer = new javax.swing.Timer(300, evt -> buscarInsumos());
                timer.setRepeats(false);
                timer.start();
            }
        });
        actionPanel.add(txtBuscar);
        
        btnBuscar = createActionButton(" Buscar", accentBlue);
        btnBuscar.addActionListener(e -> buscarInsumos());
        actionPanel.add(btnBuscar);
        
//        btnRefrescar = createActionButton("", accentPurple);
//        btnRefrescar.setPreferredSize(new Dimension(40, 32));
//        btnRefrescar.addActionListener(e -> {
//            txtBuscar.setText("");
//            cargarInsumos();
//        });
//        actionPanel.add(btnRefrescar);
        
        JSeparator sep = new JSeparator(SwingConstants.VERTICAL);
        sep.setPreferredSize(new Dimension(2, 30));
        sep.setBackground(new Color(60, 60, 65));
        actionPanel.add(sep);
        
        btnNuevo = createActionButton(" Nuevo", accentGreen);
        btnNuevo.addActionListener(e -> abrirFormularioInsumo(null));
        actionPanel.add(btnNuevo);
        
        btnEditar = createActionButton("️ Editar", accentBlue);
        btnEditar.addActionListener(e -> editarInsumo());
        actionPanel.add(btnEditar);
        
        btnEliminar = createActionButton("️ Eliminar", accentRed);
        btnEliminar.addActionListener(e -> eliminarInsumo());
        actionPanel.add(btnEliminar);
        
        JSeparator sep2 = new JSeparator(SwingConstants.VERTICAL);
        sep2.setPreferredSize(new Dimension(2, 30));
        sep2.setBackground(new Color(60, 60, 65));
        actionPanel.add(sep2);
        
        btnMovimiento = createActionButton(" Movimiento", accentOrange);
        btnMovimiento.addActionListener(e -> registrarMovimiento());
        actionPanel.add(btnMovimiento);
        
        btnVerMovimientos = createActionButton(" Ver Movimientos", accentPurple);
        btnVerMovimientos.addActionListener(e -> verMovimientos());
        actionPanel.add(btnVerMovimientos);
        
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
        panel.setPreferredSize(new Dimension(0, 350));
        
        String[] columnas = {"ID", "Código", "Nombre", "Categoría", "Stock", "Stock Mín.", "Estado Stock", "Precio Venta", "Estado"};
        model = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaInsumos = new JTable(model);
        tablaInsumos.setBackground(new Color(45, 45, 50));
        tablaInsumos.setForeground(textLight);
        tablaInsumos.setGridColor(new Color(55, 55, 60));
        tablaInsumos.setRowHeight(30);
        tablaInsumos.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaInsumos.getTableHeader().setBackground(new Color(50, 50, 55));
        tablaInsumos.getTableHeader().setForeground(textLight);
        tablaInsumos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaInsumos.setSelectionBackground(new Color(60, 60, 70));
        tablaInsumos.setSelectionForeground(textLight);
        tablaInsumos.setShowGrid(false);
        tablaInsumos.setIntercellSpacing(new Dimension(0, 0));
        
        // Renderer para colores de stock
        tablaInsumos.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (column == 6) {
                    String estado = (String) value;
                    if (estado != null) {
                        if (estado.contains("Sin stock")) {
                            c.setBackground(new Color(210, 80, 80));
                            c.setForeground(Color.WHITE);
                        } else if (estado.contains("Stock bajo")) {
                            c.setBackground(new Color(230, 160, 50));
                            c.setForeground(Color.WHITE);
                        } else {
                            c.setBackground(new Color(60, 180, 110));
                            c.setForeground(Color.WHITE);
                        }
                        c.setFont(c.getFont().deriveFont(Font.BOLD));
                    }
                } else if (column == 8) {
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
        
        tablaInsumos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editarInsumo();
                }
            }
        });
        
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem itemEditar = new JMenuItem("️ Editar");
        itemEditar.addActionListener(e -> editarInsumo());
        popupMenu.add(itemEditar);
        
        JMenuItem itemEliminar = new JMenuItem("?️ Eliminar");
        itemEliminar.addActionListener(e -> eliminarInsumo());
        popupMenu.add(itemEliminar);
        
        popupMenu.addSeparator();
        
        JMenuItem itemMovimiento = new JMenuItem(" Registrar Movimiento");
        itemMovimiento.addActionListener(e -> registrarMovimiento());
        popupMenu.add(itemMovimiento);
        
        JMenuItem itemVerMovimientos = new JMenuItem(" Ver Movimientos");
        itemVerMovimientos.addActionListener(e -> verMovimientos());
        popupMenu.add(itemVerMovimientos);
        
        tablaInsumos.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = tablaInsumos.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        tablaInsumos.setRowSelectionInterval(row, row);
                        popupMenu.show(tablaInsumos, e.getX(), e.getY());
                    }
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = tablaInsumos.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        tablaInsumos.setRowSelectionInterval(row, row);
                        popupMenu.show(tablaInsumos, e.getX(), e.getY());
                    }
                }
            }
        });
        
        JScrollPane scroll = new JScrollPane(tablaInsumos);
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
        
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setBackground(darkBg);
        
        lblTotalRegistros = new JLabel("Total: 0 insumos");
        lblTotalRegistros.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTotalRegistros.setForeground(textGray);
        leftPanel.add(lblTotalRegistros);
        
        lblStockBajo = new JLabel("️ Stock bajo: 0");
        lblStockBajo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStockBajo.setForeground(accentOrange);
        leftPanel.add(lblStockBajo);
        
        panel.add(leftPanel, BorderLayout.WEST);
        
        JLabel lblInfo = new JLabel("Doble click para editar | Click derecho para opciones | Ctrl+N Nuevo | Ctrl+E Editar | Ctrl+D Eliminar | Ctrl+F Buscar | F5 Refrescar");
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblInfo.setForeground(textGray);
        panel.add(lblInfo, BorderLayout.EAST);
        
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
    
    private void configurarTeclasRapidas() {
        InputMap inputMap = tablaInsumos.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap actionMap = tablaInsumos.getActionMap();
        
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK), "nuevo");
        actionMap.put("nuevo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirFormularioInsumo(null);
            }
        });
        
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_DOWN_MASK), "editar");
        actionMap.put("editar", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editarInsumo();
            }
        });
        
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.CTRL_DOWN_MASK), "eliminar");
        actionMap.put("eliminar", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarInsumo();
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
                cargarInsumos();
            }
        });
    }
    
    private void cargarInsumos() {
        try {
            List<Insumo> insumos = controller.listarTodos();
            controller.cargarTabla(model, insumos);
            actualizarContador();
        } catch (Exception e) {
            System.err.println("Error al cargar insumos: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error al cargar insumos: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void buscarInsumos() {
        String texto = txtBuscar.getText().trim();
        
        if (texto.isEmpty()) {
            cargarInsumos();
            return;
        }
        
        try {
            List<Insumo> resultados = controller.buscar(texto);
            controller.cargarTabla(model, resultados);
            actualizarContador();
            
            if (resultados == null || resultados.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No se encontraron insumos con: '" + texto + "'",
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
    
    private void actualizarContador() {
        int total = model.getRowCount();
        int totalGeneral = controller.contarInsumos();
        int stockBajo = controller.contarStockBajo();
        
        lblTotalRegistros.setText("Total: " + total + " insumos (Total BD: " + totalGeneral + ")");
        lblStockBajo.setText("️ Stock bajo: " + stockBajo);
    }
    
    private void abrirFormularioInsumo(Insumo insumo) {
        DialogInsumo dialog = new DialogInsumo((JFrame) SwingUtilities.getWindowAncestor(this), insumo != null);
        dialog.setInsumo(insumo);
        dialog.setVisible(true);
        
        if (dialog.isGuardado()) {
            cargarInsumos();
        }
    }
    
    private void editarInsumo() {
        int row = tablaInsumos.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione un insumo para editar", 
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (int) model.getValueAt(row, 0);
        Insumo insumo = controller.buscarPorId(id);
        if (insumo != null) {
            abrirFormularioInsumo(insumo);
        }
    }
    
    private void eliminarInsumo() {
        int row = tablaInsumos.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione un insumo para eliminar", 
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String nombre = (String) model.getValueAt(row, 2);
        int id = (int) model.getValueAt(row, 0);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de eliminar el insumo?\n\n" +
            "Nombre: " + nombre + "\n\n" +
            "⚠️ Esta acción no se puede deshacer",
            "Confirmar Eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (controller.eliminarInsumo(id)) {
                cargarInsumos();
            }
        }
    }
    
    private void registrarMovimiento() {
        int row = tablaInsumos.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione un insumo para registrar movimiento", 
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (int) model.getValueAt(row, 0);
        String nombre = (String) model.getValueAt(row, 2);
        int stock = (int) model.getValueAt(row, 4);
        
        DialogMovimiento dialog = new DialogMovimiento((JFrame) SwingUtilities.getWindowAncestor(this), id, nombre, stock);
        dialog.setVisible(true);
        
        if (dialog.isGuardado()) {
            cargarInsumos();
        }
    }
    
    private void verMovimientos() {
        int row = tablaInsumos.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione un insumo para ver movimientos", 
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (int) model.getValueAt(row, 0);
        String nombre = (String) model.getValueAt(row, 2);
        
        mostrarMovimientos(id, nombre);
    }
    
    private void mostrarMovimientos(int insumoId, String nombre) {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Movimientos de: " + nombre, true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(darkBg);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        String[] columnas = {"ID", "Tipo", "Cantidad", "Motivo", "Usuario", "Fecha"};
        DefaultTableModel modelMov = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable tablaMov = new JTable(modelMov);
        tablaMov.setBackground(new Color(45, 45, 50));
        tablaMov.setForeground(textLight);
        tablaMov.setGridColor(new Color(55, 55, 60));
        tablaMov.setRowHeight(28);
        tablaMov.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaMov.getTableHeader().setBackground(new Color(50, 50, 55));
        tablaMov.getTableHeader().setForeground(textLight);
        tablaMov.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        List<Movimiento> movimientos = controller.listarMovimientosPorInsumo(insumoId);
        controller.cargarMovimientos(modelMov, movimientos);
        
        JScrollPane scroll = new JScrollPane(tablaMov);
        scroll.setBackground(darkBg);
        scroll.getViewport().setBackground(darkBg);
        panel.add(scroll, BorderLayout.CENTER);
        
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setBackground(accentBlue);
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setBorderPainted(false);
        btnCerrar.addActionListener(e -> dialog.dispose());
        
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(darkBg);
        btnPanel.add(btnCerrar);
        panel.add(btnPanel, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
}