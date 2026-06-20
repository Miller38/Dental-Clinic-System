package com.dentalclinicsystem.controller;

import com.dentalclinicsystem.dao.AuditoriaDAO;
import com.dentalclinicsystem.dao.Session;
import com.dentalclinicsystem.dao.UsuarioDAO;
import com.dentalclinicsystem.model.Usuario;
import com.dentalclinicsystem.view.DashboardModerno2;
import com.dentalclinicsystem.view.LoginView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginController {

    private LoginView view;
    private UsuarioDAO usuarioDAO;
    private AuditoriaDAO auditoriaDAO;

    public LoginController(LoginView view) {
        this.view = view;
        this.usuarioDAO = new UsuarioDAO();
        this.auditoriaDAO = new AuditoriaDAO();

        this.view.btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                autenticar();
            }
        });

        // Enter key para login
        this.view.txtPassword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    autenticar();
                }
            }
        });

        this.view.txtUsuario.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    view.txtPassword.requestFocus();
                }
            }
        });

        // Mostrar/ocultar password
        this.view.chekMostrarPassword.addItemListener(e -> {
            if (view.chekMostrarPassword.getState()) {
                view.txtPassword.setEchoChar((char) 0);
            } else {
                view.txtPassword.setEchoChar('*');
            }
        });
    }

    private void autenticar() {
        String usuario = view.txtUsuario.getText().trim();
        String password = new String(view.txtPassword.getPassword());

        if (usuario.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Por favor ingrese usuario y contraseña", 
                    "Campos vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Usuario usuarioAutenticado = usuarioDAO.login(usuario, password);

        if (usuarioAutenticado != null) {
            // Login exitoso
            auditoriaDAO.registrar(usuario, "LOGIN_EXITOSO");
            
            // Iniciar sesión en Session
            Session.setUsuarioActual(usuarioAutenticado);
            
            JOptionPane.showMessageDialog(view, "Bienvenido " + usuarioAutenticado.getNombre(), 
                    "Login exitoso", JOptionPane.INFORMATION_MESSAGE);
            
            // Cerrar login y abrir ventana principal
            view.dispose();
           // CORREGIDO: Crear e iniciar el Dashboard
            DashboardModerno2 dashboard = new DashboardModerno2();
            new DashboardModerno2Controller(dashboard);  // Inicializar controlador
            dashboard.setVisible(true);

        } else {
            // Login fallido
            auditoriaDAO.registrar(usuario, "LOGIN_FALLIDO");
            JOptionPane.showMessageDialog(view, "Usuario o contraseña incorrectos", 
                    "Error de autenticación", JOptionPane.ERROR_MESSAGE);
            view.txtPassword.setText("");
            view.txtPassword.requestFocus();
        }
    }

    
}