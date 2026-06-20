package com.dentalclinicsystem.view;

import javax.swing.*;

import java.awt.*;

public class SplashScreen
        extends JWindow {

    private JProgressBar progress;
    //private JLabel lblVersion;

    public SplashScreen() {

        initComponents();

        cargar();
    }

    private void initComponents() {

        setSize(700, 500);
        setLocationRelativeTo(null);
        

        JPanel panel =  new JPanel(new BorderLayout());
        JLabel logo =  new JLabel("Dental Clinic System",SwingConstants.CENTER);
        logo.setFont(new Font("Segoe UI",Font.BOLD,40));
        
      // Crear el JLabel para la versión
//        lblVersion = new JLabel("", SwingConstants.CENTER);
//        lblVersion.setFont(new Font("Segoe UI", Font.PLAIN, 12));
//        lblVersion.setBackground(Color.LIGHT_GRAY);
        
        // 🔑 AQUÍ se asigna la versión dinámica
       // lblVersion.setText("      Versión " + ActualizacionConfig.getVersion());

        progress =
                new JProgressBar();

        progress.setStringPainted(true);

//         // Panel superior para la versión
//        JPanel northPanel = new JPanel();
//        northPanel.add(lblVersion);
        
      // panel.add(northPanel, BorderLayout.NORTH);  // Versión arriba
        panel.add(
                logo,
                BorderLayout.CENTER);

        panel.add(
                progress,
                BorderLayout.SOUTH);

        add(panel);
    }

    private void cargar() {

        new Thread(() -> {

            try {

                for (int i = 0;
                     i <= 100;
                     i++) {

                    progress.setValue(i);

                    Thread.sleep(20);
                }

                dispose();

            } catch (Exception e) {

                System.out.println(
                        e.getMessage());
            }

        }).start();
    }
}