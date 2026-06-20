package com.dentalclinicsystem.controller;

import com.dentalclinicsystem.dao.Session;
import com.dentalclinicsystem.view.DashboardModerno2;
import com.dentalclinicsystem.view.LoginView;


import javax.swing.*;

public class DashboardModerno2Controller {
    private DashboardModerno2 view;
    
    public DashboardModerno2Controller(DashboardModerno2 view) {
        this.view = view;
        initEvents();
    }
    
    private void initEvents() {
        view.getBtnDashboard().addActionListener(e -> showDashboard());
        view.getBtnPacientes().addActionListener(e -> showPacientes());
        view.getBtnCitas().addActionListener(e -> showCitas());
        view.getBtnServicios().addActionListener(e -> showServicios());
        view.getBtnVentas().addActionListener(e -> showFinanzas());
        view.getBtnInventario().addActionListener(e -> showInventario());
        view.getBtnUsuarios().addActionListener(e -> showUsuarios());
        view.getBtnReportes().addActionListener(e -> showReportes());
        view.getBtnSettings().addActionListener(e -> showSettings());
        view.getBtnBackup().addActionListener(e -> showBackup());
        view.getBtnEncuesta().addActionListener(e -> showEncuesta());
        view.getBtnLogout().addActionListener(e -> logout());
    }
    
    private void showDashboard() { 
        System.out.println("📊 Mostrando Dashboard");
        view.mostrarDashboard();
    }
    
    private void showPacientes() { 
        System.out.println("👤 Click en Pacientes");
        try {
            view.mostrarPanelPacientes();
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, 
                "Error al cargar pacientes: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showCitas() { 
        System.out.println("📅 Abriendo módulo de citas...");
        try {
            view.mostrarPanelCitas();
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, 
                "Error al cargar citas: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showServicios() { 
        System.out.println("💉 Abriendo módulo de servicios...");
        try {
            view.mostrarPanelServicios();
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, 
                "Error al cargar servicios: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showFinanzas() { 
        System.out.println("💰 Abriendo módulo de finanzas...");
        try {
            view.mostrarPanelFinanzas();
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, 
                "Error al cargar finanzas: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showInventario() { 
        System.out.println("📦 Abriendo módulo de inventario...");
        try {
            view.mostrarPanelInventario();
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, 
                "Error al cargar inventario: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showUsuarios() { 
        System.out.println("👥 Abriendo módulo de usuarios...");
        try {
            view.mostrarPanelUsuarios();
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, 
                "Error al cargar usuarios: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showReportes() { 
        System.out.println("📊 Abriendo módulo de reportes...");
        try {
            view.mostrarPanelReportes();
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, 
                "Error al cargar reportes: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Agregar método:
private void showEncuesta() { 
    System.out.println("📋 Abriendo panel de encuesta...");
    try {
        view.mostrarPanelEncuesta();
    } catch (Exception e) {
        System.err.println("❌ Error: " + e.getMessage());
        e.printStackTrace();
        JOptionPane.showMessageDialog(view, 
            "Error al cargar encuesta: " + e.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
    }
}
    
    private void showSettings() { 
        System.out.println("⚙️ Abriendo configuración...");
        try {
            view.mostrarPanelConfiguracion();
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, 
                "Error al abrir configuración: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Agregar método:
private void showBackup() { 
    System.out.println("💾 Abriendo módulo de backup...");
    try {
        view.mostrarPanelBackup();
    } catch (Exception e) {
        System.err.println("❌ Error: " + e.getMessage());
        e.printStackTrace();
        JOptionPane.showMessageDialog(view, 
            "Error al cargar backup: " + e.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
    }
}
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(view, 
            "¿Está seguro que desea cerrar sesión?", 
            "Cerrar Sesión", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            Session.cerrarSession();
            view.dispose();
            LoginView loginView = new LoginView();
            new LoginController(loginView);
            loginView.setVisible(true);
        }
    }
}