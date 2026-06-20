package com.dentalclinicsystem.view;

import com.dentalclinicsystem.service.BackupService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;

public class PanelBackup extends JPanel {
    
    private BackupService backupService;
    private JList<String> listBackups;
    private JList<String> listBackupsAuto;
    private DefaultListModel<String> listModel;
    private DefaultListModel<String> listModelAuto;
    private JLabel lblInfo, lblInfoAuto;
    
    private Color darkBg = new Color(30, 30, 35);
    private Color darkCard = new Color(40, 40, 45);
    private Color textLight = new Color(220, 220, 230);
    private Color textGray = new Color(150, 150, 165);
    private Color accentGreen = new Color(60, 180, 110);
    private Color accentRed = new Color(210, 80, 80);
    private Color accentBlue = new Color(70, 130, 200);
    private Color accentOrange = new Color(230, 160, 50);
    private Color accentPurple = new Color(150, 80, 200);
    
    public PanelBackup() {
        this.backupService = new BackupService();
        initComponents();
        cargarBackups();
    }
    
    private void initComponents() {
        setBackground(darkBg);
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        JPanel centerPanel = createCenterPanel();
        add(centerPanel, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(darkBg);
        panel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setBackground(darkBg);
        
        JLabel titleLabel = new JLabel(" Backup y Restauración");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(textLight);
        titlePanel.add(titleLabel);
        
        panel.add(titlePanel, BorderLayout.NORTH);
        
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        actionPanel.setBackground(darkBg);
        
        JButton btnCrearBackup = createActionButton(" Crear Backup Manual", accentGreen);
        btnCrearBackup.addActionListener(e -> crearBackup());
        actionPanel.add(btnCrearBackup);
        
        JButton btnRestaurar = createActionButton(" Restaurar Backup", accentOrange);
        btnRestaurar.addActionListener(e -> restaurarBackup());
        actionPanel.add(btnRestaurar);
        
        JButton btnRefrescar = createActionButton(" Refrescar", accentBlue);
        btnRefrescar.addActionListener(e -> cargarBackups());
        actionPanel.add(btnRefrescar);
        
        JButton btnEliminar = createActionButton("️ Eliminar", accentRed);
        btnEliminar.addActionListener(e -> eliminarBackup());
        actionPanel.add(btnEliminar);
        
        JLabel lblAutoInfo = new JLabel(" Los backups automáticos se crean al cerrar sesión");
        lblAutoInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblAutoInfo.setForeground(accentPurple);
        actionPanel.add(lblAutoInfo);
        
        panel.add(actionPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 15, 0));
        panel.setBackground(darkBg);
        
        JPanel manualPanel = createBackupListPanel(" Backups Manuales", 
            listBackups = new JList<>(), listModel = new DefaultListModel<>(), 
            lblInfo = new JLabel());
        panel.add(manualPanel);
        
        JPanel autoPanel = createBackupListPanel(" Backups Automáticos (al cerrar sesión)", 
            listBackupsAuto = new JList<>(), listModelAuto = new DefaultListModel<>(), 
            lblInfoAuto = new JLabel());
        panel.add(autoPanel);
        
        return panel;
    }
    
    private JPanel createBackupListPanel(String titulo, JList<String> list, 
                                         DefaultListModel<String> model, JLabel lblInfo) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(darkCard);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(50, 50, 55), 1),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitulo.setForeground(textLight);
        panel.add(lblTitulo, BorderLayout.NORTH);
        
        list.setModel(model);
        list.setBackground(new Color(45, 45, 50));
        list.setForeground(textLight);
        list.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        list.setSelectionBackground(new Color(60, 60, 70));
        list.setSelectionForeground(textLight);
        list.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 65)));
        
        JScrollPane scroll = new JScrollPane(list);
        scroll.setBackground(darkCard);
        scroll.getViewport().setBackground(darkCard);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        
        panel.add(scroll, BorderLayout.CENTER);
        
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.setBackground(darkCard);
        infoPanel.setBorder(new EmptyBorder(5, 0, 0, 0));
        
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblInfo.setForeground(textGray);
        infoPanel.add(lblInfo);
        
        panel.add(infoPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JButton createActionButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(150, 35));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(color.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(color);
            }
        });
        
        return btn;
    }
    
    private void cargarBackups() {
        listModel.clear();
        File[] backups = backupService.listarBackups();
        
        if (backups == null || backups.length == 0) {
            listModel.addElement(" No hay backups manuales");
        } else {
            for (File f : backups) {
                listModel.addElement(backupService.getInfoBackup(f));
            }
        }
        lblInfo.setText("📋 Total: " + (backups != null ? backups.length : 0) + " backups manuales");
        
        listModelAuto.clear();
        File[] backupsAuto = backupService.listarBackupsAutomaticos();
        
        if (backupsAuto == null || backupsAuto.length == 0) {
            listModelAuto.addElement(" No hay backups automáticos");
        } else {
            for (File f : backupsAuto) {
                listModelAuto.addElement(backupService.getInfoBackup(f));
            }
        }
        lblInfoAuto.setText("🤖 Total: " + (backupsAuto != null ? backupsAuto.length : 0) + " backups automáticos");
    }
    
    private void crearBackup() {
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (backupService.crearBackup(parent)) {
            cargarBackups();
        }
    }
    
    private void restaurarBackup() {
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        backupService.restaurarBackup(parent);
        cargarBackups();
    }
    
    private void eliminarBackup() {
        int index = listBackups.getSelectedIndex();
        if (index == -1) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione un backup para eliminar", 
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String seleccion = listModel.get(index);
        if (seleccion.contains(" No hay backups")) {
            return;
        }
        
        String nombreArchivo = seleccion.substring(seleccion.indexOf(" - ") + 3);
        nombreArchivo = nombreArchivo.substring(0, nombreArchivo.indexOf(" - "));
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de eliminar el backup?\n\n" +
            "Archivo: " + nombreArchivo,
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            File backupDir = new File("backups");
            File archivo = new File(backupDir, nombreArchivo);
            if (archivo.exists() && archivo.delete()) {
                cargarBackups();
                JOptionPane.showMessageDialog(this, 
                    "✅ Backup eliminado correctamente",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
}