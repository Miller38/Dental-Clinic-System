package com.dentalclinicsystem.view;

import com.dentalclinicsystem.controller.UsuariosController;
import com.dentalclinicsystem.model.Usuario;

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

public class PanelUsuarios extends JPanel {
    
    private UsuariosController controller;
    private JTable tablaUsuarios;
    private DefaultTableModel model;
    private JTextField txtBuscar;
    private JButton btnNuevo, btnEditar, btnEliminar, btnBuscar, btnRefrescar;
    private JButton btnBloquear, btnDesbloquear, btnResetPassword;
    private JLabel lblTotalRegistros;
    private List<Usuario> listaUsuariosActual;
    
    private Color darkBg = new Color(30, 30, 35);
    private Color darkCard = new Color(40, 40, 45);
    private Color textLight = new Color(220, 220, 230);
    private Color textGray = new Color(150, 150, 165);
    private Color accentBlue = new Color(70, 130, 200);
    private Color accentGreen = new Color(60, 180, 110);
    private Color accentRed = new Color(210, 80, 80);
    private Color accentOrange = new Color(230, 160, 50);
    private Color accentPurple = new Color(150, 80, 200);
    
    public PanelUsuarios() {
        this.controller = new UsuariosController();
        initComponents();
        cargarUsuarios();
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
        
        // 🔥 TÍTULO - Arriba a la izquierda (no tapado)
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setBackground(darkBg);
        titlePanel.setPreferredSize(new Dimension(0, 40));
        
        JLabel titleLabel = new JLabel(" Gestión de Usuarios");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(textLight);
        titlePanel.add(titleLabel);
        panel.add(titlePanel, BorderLayout.NORTH);
        
        // 🔥 BOTONES - Abajo del título
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        actionPanel.setBackground(darkBg);
        
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
        txtBuscar.addActionListener(e -> buscarUsuarios());
        txtBuscar.addKeyListener(new KeyAdapter() {
            private javax.swing.Timer timer;
            @Override
            public void keyReleased(KeyEvent e) {
                if (txtBuscar.getText().trim().isEmpty()) {
                    cargarUsuarios();
                    return;
                }
                if (timer != null && timer.isRunning()) {
                    timer.stop();
                }
                timer = new javax.swing.Timer(300, evt -> buscarUsuarios());
                timer.setRepeats(false);
                timer.start();
            }
        });
        actionPanel.add(txtBuscar);
        
        btnBuscar = createActionButton(" Buscar", accentBlue);
        btnBuscar.addActionListener(e -> buscarUsuarios());
        actionPanel.add(btnBuscar);
        
//        btnRefrescar = createActionButton("", accentPurple);
//        btnRefrescar.setPreferredSize(new Dimension(40, 32));
//        btnRefrescar.addActionListener(e -> {
//            txtBuscar.setText("");
//            cargarUsuarios();
//        });
//        actionPanel.add(btnRefrescar);
        
        // Separador
        JSeparator sep1 = new JSeparator(SwingConstants.VERTICAL);
        sep1.setPreferredSize(new Dimension(2, 30));
        sep1.setBackground(new Color(60, 60, 65));
        actionPanel.add(sep1);
        
        // CRUD
        btnNuevo = createActionButton(" Nuevo", accentGreen);
        btnNuevo.addActionListener(e -> abrirFormularioUsuario(null));
        actionPanel.add(btnNuevo);
        
        btnEditar = createActionButton("️ Editar", accentBlue);
        btnEditar.addActionListener(e -> editarUsuario());
        actionPanel.add(btnEditar);
        
        btnEliminar = createActionButton("️ Eliminar", accentRed);
        btnEliminar.addActionListener(e -> eliminarUsuario());
        actionPanel.add(btnEliminar);
        
        // Separador
        JSeparator sep2 = new JSeparator(SwingConstants.VERTICAL);
        sep2.setPreferredSize(new Dimension(2, 30));
        sep2.setBackground(new Color(60, 60, 65));
        actionPanel.add(sep2);
        
        // Acciones
        btnBloquear = createActionButton(" Bloquear", accentOrange);
        btnBloquear.addActionListener(e -> bloquearUsuario());
        actionPanel.add(btnBloquear);
        
        btnDesbloquear = createActionButton(" Desbloquear", accentGreen);
        btnDesbloquear.addActionListener(e -> desbloquearUsuario());
        actionPanel.add(btnDesbloquear);
        
        btnResetPassword = createActionButton(" Reset", accentPurple);
        btnResetPassword.setPreferredSize(new Dimension(80, 32));
        btnResetPassword.addActionListener(e -> resetearPassword());
        actionPanel.add(btnResetPassword);
        
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
        
        String[] columnas = {"ID", "Nombre", "Usuario", "Rol", "Email", "Teléfono", "Estado", "Bloqueado", "Último Acceso"};
        model = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaUsuarios = new JTable(model);
        tablaUsuarios.setBackground(new Color(45, 45, 50));
        tablaUsuarios.setForeground(textLight);
        tablaUsuarios.setGridColor(new Color(55, 55, 60));
        tablaUsuarios.setRowHeight(30);
        tablaUsuarios.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaUsuarios.getTableHeader().setBackground(new Color(50, 50, 55));
        tablaUsuarios.getTableHeader().setForeground(textLight);
        tablaUsuarios.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaUsuarios.setSelectionBackground(new Color(60, 60, 70));
        tablaUsuarios.setSelectionForeground(textLight);
        tablaUsuarios.setShowGrid(false);
        tablaUsuarios.setIntercellSpacing(new Dimension(0, 0));
        tablaUsuarios.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        // Renderer para colores de estado
        tablaUsuarios.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (column == 6) { // Estado
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
                } else if (column == 7) { // Bloqueado
                    String bloqueado = (String) value;
                    if (bloqueado != null) {
                        if (bloqueado.contains(" ")) {
                            c.setBackground(new Color(210, 80, 80));
                            c.setForeground(Color.WHITE);
                        } else {
                            c.setBackground(new Color(60, 180, 110));
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
        
        tablaUsuarios.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editarUsuario();
                }
            }
        });
        
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem itemEditar = new JMenuItem("️ Editar");
        itemEditar.addActionListener(e -> editarUsuario());
        popupMenu.add(itemEditar);
        
        JMenuItem itemEliminar = new JMenuItem("️ Eliminar");
        itemEliminar.addActionListener(e -> eliminarUsuario());
        popupMenu.add(itemEliminar);
        
        popupMenu.addSeparator();
        
        JMenuItem itemBloquear = new JMenuItem(" Bloquear");
        itemBloquear.addActionListener(e -> bloquearUsuario());
        popupMenu.add(itemBloquear);
        
        JMenuItem itemDesbloquear = new JMenuItem(" Desbloquear");
        itemDesbloquear.addActionListener(e -> desbloquearUsuario());
        popupMenu.add(itemDesbloquear);
        
        JMenuItem itemResetPassword = new JMenuItem(" Reset Password");
        itemResetPassword.addActionListener(e -> resetearPassword());
        popupMenu.add(itemResetPassword);
        
        tablaUsuarios.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = tablaUsuarios.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        tablaUsuarios.setRowSelectionInterval(row, row);
                        popupMenu.show(tablaUsuarios, e.getX(), e.getY());
                    }
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = tablaUsuarios.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        tablaUsuarios.setRowSelectionInterval(row, row);
                        popupMenu.show(tablaUsuarios, e.getX(), e.getY());
                    }
                }
            }
        });
        
        JScrollPane scroll = new JScrollPane(tablaUsuarios);
        scroll.setBackground(darkCard);
        scroll.getViewport().setBackground(darkCard);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        
        panel.add(scroll, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(darkBg);
        panel.setPreferredSize(new Dimension(0, 35));
        panel.setBorder(new EmptyBorder(5, 10, 5, 10));
        
        lblTotalRegistros = new JLabel("Total: 0 usuarios");
        lblTotalRegistros.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTotalRegistros.setForeground(textGray);
        panel.add(lblTotalRegistros, BorderLayout.WEST);
        
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
        btn.setPreferredSize(new Dimension(85, 32));
        
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
        InputMap inputMap = tablaUsuarios.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap actionMap = tablaUsuarios.getActionMap();
        
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK), "nuevo");
        actionMap.put("nuevo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirFormularioUsuario(null);
            }
        });
        
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_DOWN_MASK), "editar");
        actionMap.put("editar", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editarUsuario();
            }
        });
        
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.CTRL_DOWN_MASK), "eliminar");
        actionMap.put("eliminar", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarUsuario();
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
                cargarUsuarios();
            }
        });
    }
    
    private void cargarUsuarios() {
        try {
            listaUsuariosActual = controller.listarTodos();
            controller.cargarTabla(model, listaUsuariosActual);
            actualizarContador();
        } catch (Exception e) {
            System.err.println("Error al cargar usuarios: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error al cargar usuarios: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void buscarUsuarios() {
        String texto = txtBuscar.getText().trim();
        
        if (texto.isEmpty()) {
            cargarUsuarios();
            return;
        }
        
        try {
            List<Usuario> resultados = controller.buscar(texto);
            controller.cargarTabla(model, resultados);
            actualizarContador();
            
            if (resultados == null || resultados.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No se encontraron usuarios con: '" + texto + "'",
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
        int totalGeneral = controller.contarUsuarios();
        lblTotalRegistros.setText("Mostrando " + total + " de " + totalGeneral + " usuarios");
    }
    
    private void abrirFormularioUsuario(Usuario usuario) {
        DialogUsuario dialog = new DialogUsuario((JFrame) SwingUtilities.getWindowAncestor(this), usuario != null);
        dialog.setUsuario(usuario);
        dialog.setVisible(true);
        
        if (dialog.isGuardado()) {
            cargarUsuarios();
        }
    }
    
    private void editarUsuario() {
        int row = tablaUsuarios.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione un usuario para editar", 
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (int) model.getValueAt(row, 0);
        Usuario usuario = controller.buscarPorId(id);
        if (usuario != null) {
            abrirFormularioUsuario(usuario);
        }
    }
    
    private void eliminarUsuario() {
        int row = tablaUsuarios.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione un usuario para eliminar", 
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String nombre = (String) model.getValueAt(row, 1);
        int id = (int) model.getValueAt(row, 0);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de eliminar el usuario?\n\n" +
            "Usuario: " + nombre + "\n\n" +
            "⚠️ Esta acción no se puede deshacer",
            "Confirmar Eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (controller.eliminarUsuario(id)) {
                cargarUsuarios();
            }
        }
    }
    
    private void bloquearUsuario() {
        int row = tablaUsuarios.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione un usuario para bloquear", 
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (int) model.getValueAt(row, 0);
        String nombre = (String) model.getValueAt(row, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Bloquear al usuario?\n\n" +
            "Usuario: " + nombre + "\n\n" +
            "El usuario no podrá iniciar sesión",
            "Confirmar bloqueo",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (controller.bloquearUsuario(id)) {
                cargarUsuarios();
            }
        }
    }
    
    private void desbloquearUsuario() {
        int row = tablaUsuarios.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione un usuario para desbloquear", 
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (int) model.getValueAt(row, 0);
        String nombre = (String) model.getValueAt(row, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Desbloquear al usuario?\n\n" +
            "Usuario: " + nombre,
            "Confirmar desbloqueo",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (controller.desbloquearUsuario(id)) {
                cargarUsuarios();
            }
        }
    }
    
    private void resetearPassword() {
        int row = tablaUsuarios.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione un usuario para resetear contraseña", 
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (int) model.getValueAt(row, 0);
        String nombre = (String) model.getValueAt(row, 1);
        
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.add(new JLabel("Nueva contraseña:"));
        JPasswordField pfPassword = new JPasswordField(15);
        panel.add(pfPassword);
        panel.add(new JLabel("Confirmar:"));
        JPasswordField pfConfirm = new JPasswordField(15);
        panel.add(pfConfirm);
        
        int result = JOptionPane.showConfirmDialog(this, panel, 
            "Resetear contraseña de: " + nombre,
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String password = new String(pfPassword.getPassword());
            String confirm = new String(pfConfirm.getPassword());
            
            if (controller.cambiarPassword(id, password, confirm)) {
                cargarUsuarios();
            }
        }
    }
}