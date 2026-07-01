package com.dentalclinicsystem.view;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TermsAndConditionsDialog extends JDialog {
    
    private boolean accepted = false;
    private JCheckBox chkTerms;
    private JCheckBox chkPrivacy;
    private JCheckBox chkNoShowAgain;
    private JButton btnAccept;
    private JButton btnDecline;
    private static final String TERMS_VERSION = "1.0";
    
    // Colores para Dark Mode
    private static final Color BG_DARK = new Color(43, 43, 43);
    private static final Color BG_PANEL = new Color(60, 60, 60);
    private static final Color TEXT_LIGHT = new Color(220, 220, 220);
    private static final Color TEXT_WHITE = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(80, 80, 80);
    private static final Color ACCENT_BLUE = new Color(52, 152, 219);
    private static final Color ACCENT_GREEN = new Color(46, 204, 113);
    private static final Color ACCENT_RED = new Color(231, 76, 60);
    
    public TermsAndConditionsDialog(JFrame parent) {
        super(parent, "Términos y Condiciones", true);
        initComponents();
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    }
    
    private void initComponents() {
        setSize(800, 700);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_DARK);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 20, 25));
        mainPanel.setBackground(BG_DARK);
        
        // ================================================================
        // TÍTULO
        // ================================================================
        JLabel title = new JLabel("ACUERDO DE USUARIO", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(TEXT_WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JLabel subtitle = new JLabel("Por favor lea detenidamente antes de continuar", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitle.setForeground(TEXT_LIGHT);
        subtitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // ================================================================
        // PANEL DE PESTAÑAS
        // ================================================================
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        tabbedPane.setBackground(BG_DARK);
        tabbedPane.setForeground(TEXT_WHITE);
        
        // Pestaña 1: Términos y Condiciones
        JPanel termsPanel = createTextPanel("TERMINOS_Y_CONDICIONES.txt");
        tabbedPane.addTab("Términos y Condiciones", termsPanel);
        
        // Pestaña 2: Políticas de Privacidad
        JPanel privacyPanel = createTextPanel("POLITICAS_DE_PRIVACIDAD.txt");
        tabbedPane.addTab("Políticas de Privacidad", privacyPanel);
        
        // ================================================================
        // CHECKBOXES
        // ================================================================
        JPanel checkPanel = new JPanel();
        checkPanel.setLayout(new BoxLayout(checkPanel, BoxLayout.Y_AXIS));
        checkPanel.setBackground(BG_DARK);
        checkPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        
        chkTerms = new JCheckBox("Acepto los Términos y Condiciones");
        chkTerms.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        chkTerms.setBackground(BG_DARK);
        chkTerms.setForeground(TEXT_WHITE);
        chkTerms.setFocusPainted(false);
        
        chkPrivacy = new JCheckBox("Acepto las Políticas de Privacidad");
        chkPrivacy.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        chkPrivacy.setBackground(BG_DARK);
        chkPrivacy.setForeground(TEXT_WHITE);
        chkPrivacy.setFocusPainted(false);
        
        chkNoShowAgain = new JCheckBox("No volver a mostrar este mensaje");
        chkNoShowAgain.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        chkNoShowAgain.setForeground(TEXT_LIGHT);
        chkNoShowAgain.setBackground(BG_DARK);
        chkNoShowAgain.setFocusPainted(false);
        chkNoShowAgain.setSelected(false);
        
        // Actualizar estado del botón aceptar
        chkTerms.addActionListener(e -> updateAcceptButton());
        chkPrivacy.addActionListener(e -> updateAcceptButton());
        
        checkPanel.add(chkTerms);
        checkPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        checkPanel.add(chkPrivacy);
        checkPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        checkPanel.add(chkNoShowAgain);
        
        // ================================================================
        // BOTONES
        // ================================================================
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 15));
        buttonPanel.setBackground(BG_DARK);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        
        btnAccept = new JButton("ACEPTAR Y CONTINUAR");
        btnAccept.setBackground(ACCENT_GREEN);
        btnAccept.setForeground(Color.WHITE);
        btnAccept.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnAccept.setFocusPainted(false);
        btnAccept.setPreferredSize(new Dimension(200, 50));
        btnAccept.setEnabled(false);
        btnAccept.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        btnDecline = new JButton("RECHAZAR Y SALIR");
        btnDecline.setBackground(ACCENT_RED);
        btnDecline.setForeground(Color.WHITE);
        btnDecline.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnDecline.setFocusPainted(false);
        btnDecline.setPreferredSize(new Dimension(200, 50));
        btnDecline.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        btnAccept.addActionListener(e -> acceptTerms());
        btnDecline.addActionListener(e -> declineTerms());
        
        buttonPanel.add(btnAccept);
        buttonPanel.add(btnDecline);
        
        // ================================================================
        // ENSAMBLAR
        // ================================================================
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setBackground(BG_DARK);
        northPanel.add(title, BorderLayout.NORTH);
        northPanel.add(subtitle, BorderLayout.SOUTH);
        
        mainPanel.add(northPanel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.add(checkPanel, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    // ================================================================
    // CREAR PANEL DE TEXTO CON DARK MODE
    // ================================================================
    private JPanel createTextPanel(String resourceName) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_DARK);
        
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBackground(BG_PANEL);
        textArea.setForeground(TEXT_WHITE);
        textArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        textArea.setCaretPosition(0);
        
        // Cargar contenido
        String content = loadTextFromFile(resourceName);
        textArea.setText(content);
        
        // Personalizar scroll pane para dark mode
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        scrollPane.setBackground(BG_DARK);
        scrollPane.getViewport().setBackground(BG_PANEL);
        scrollPane.setPreferredSize(new Dimension(650, 320));
        
        // Personalizar barra de scroll para dark mode
        scrollPane.getVerticalScrollBar().setBackground(BG_DARK);
        scrollPane.getVerticalScrollBar().setForeground(TEXT_LIGHT);
        scrollPane.getHorizontalScrollBar().setBackground(BG_DARK);
        scrollPane.getHorizontalScrollBar().setForeground(TEXT_LIGHT);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
    
    // ================================================================
    // CARGAR TEXTO DESDE ARCHIVO O USAR DEFAULT
    // ================================================================
    private String loadTextFromFile(String resourceName) {
        try {
            InputStream inputStream = getClass().getResourceAsStream("/textos/" + resourceName);
            if (inputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                reader.close();
                return content.toString();
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error cargando archivo: " + e.getMessage());
        }
        return getDefaultText(resourceName);
    }
    
    // ================================================================
    // TEXTO POR DEFECTO
    // ================================================================
    private String getDefaultText(String resourceName) {
        if (resourceName.contains("TERMINOS")) {
            return "TERMINOS Y CONDICIONES\n" +
                   "\n\n" +
                   "1. ACEPTACIÓN DE LOS TÉRMINOS\n" +
                   "Al utilizar Dental Clinic System, usted acepta estos términos y condiciones.\n" +
                   "Si no está de acuerdo, no debe utilizar el Sistema.\n\n" +
                   "2. USO DEL SISTEMA\n" +
                   "El sistema está diseñado para la gestión de clínicas dentales.\n" +
                   "El usuario se compromete a utilizarlo únicamente para fines profesionales.\n\n" +
                   "3. RESPONSABILIDAD DEL USUARIO\n" +
                   "El usuario es responsable de la seguridad de sus credenciales.\n" +
                   "El usuario debe mantener la confidencialidad de los datos de pacientes.\n\n" +
                   "4. PROPIEDAD INTELECTUAL\n" +
                   "El sistema y su código son propiedad del desarrollador.\n" +
                   "No está permitida la copia o distribución no autorizada.\n\n" +
                   "5. ACTUALIZACIONES\n" +
                   "El sistema puede recibir actualizaciones periódicas.\n" +
                   "El usuario acepta las actualizaciones automáticas.\n\n" +
                   "6. SOPORTE TÉCNICO\n" +
                   "El soporte técnico se proporciona según el plan de licencia contratado.\n\n" +
                   "7. MODIFICACIONES\n" +
                   "Estos términos pueden ser modificados previo aviso al usuario.\n\n" +
                   "8. LEY APLICABLE\n" +
                   "Estos términos se rigen por las leyes aplicables en Colombia.\n\n" +
                   "\n" +
                   "Última actualización: 30 de junio de 2026";
        } else {
            return "POLÍTICAS DE PRIVACIDAD\n" +
                   "\n\n" +
                   "1. RECOPILACIÓN DE DATOS\n" +
                   "Dental Clinic System recopila y almacena datos de pacientes y usuarios.\n" +
                   "Los datos incluyen: nombre, identificación, contactos, historial médico.\n\n" +
                   "2. USO DE LA INFORMACIÓN\n" +
                   "Los datos se utilizan para la gestión de la clínica dental.\n" +
                   "No se comparten con terceros sin consentimiento expreso.\n\n" +
                   "3. SEGURIDAD DE DATOS\n" +
                   "Se implementan medidas de seguridad para proteger la información.\n" +
                   "Los datos se almacenan en base de datos local encriptada.\n\n" +
                   "4. DERECHOS DEL USUARIO\n" +
                   "El usuario tiene derecho a: acceder, rectificar, cancelar sus datos.\n" +
                   "Puede solicitar la eliminación de su información personal.\n\n" +
                   "5. COOKIES\n" +
                   "El sistema no utiliza cookies para rastrear usuarios.\n\n" +
                   "6. CAMBIOS EN LA POLÍTICA\n" +
                   "La política de privacidad puede actualizarse periódicamente.\n\n" +
                   //"7. CONTACTO\n" +
                   //"Para consultas sobre privacidad: soporte@dentalclinic.com\n\n" +
                   "\n" +
                   "Última actualización: 30 de junio de 2026";
        }
    }
    
    // ================================================================
    // MÉTODOS DE CONTROL
    // ================================================================
    private void updateAcceptButton() {
        btnAccept.setEnabled(chkTerms.isSelected() && chkPrivacy.isSelected());
        if (btnAccept.isEnabled()) {
            btnAccept.setBackground(new Color(46, 204, 113));
        } else {
            btnAccept.setBackground(new Color(60, 60, 60));
        }
    }
    
    private void acceptTerms() {
        if (chkTerms.isSelected() && chkPrivacy.isSelected()) {
            accepted = true;
            if (chkNoShowAgain.isSelected()) {
                saveAcceptance();
            }
            dispose();
        }
    }
    
    private void declineTerms() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Debe aceptar los términos y condiciones para usar el sistema.\n\n¿Desea salir de la aplicación?",
            "Confirmar salida",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
    
    private void saveAcceptance() {
        try {
            java.util.prefs.Preferences prefs = 
                java.util.prefs.Preferences.userNodeForPackage(TermsAndConditionsDialog.class);
            prefs.putBoolean("terms_accepted", true);
            prefs.putLong("terms_acceptance_date", System.currentTimeMillis());
            prefs.put("terms_version", TERMS_VERSION);
            prefs.flush();
            System.out.println("✅ Términos aceptados y guardados");
        } catch (Exception e) {
            System.err.println("⚠️ No se pudo guardar: " + e.getMessage());
        }
    }
    
    public boolean isAccepted() {
        return accepted;
    }
    
    // ================================================================
    // MÉTODOS ESTÁTICOS
    // ================================================================
    
    public static boolean hasAcceptedTerms() {
        try {
            java.util.prefs.Preferences prefs = 
                java.util.prefs.Preferences.userNodeForPackage(TermsAndConditionsDialog.class);
            return prefs.getBoolean("terms_accepted", false);
        } catch (Exception e) {
            return false;
        }
    }
    
    public static boolean isFirstExecution() {
        try {
            java.util.prefs.Preferences prefs = 
                java.util.prefs.Preferences.userNodeForPackage(TermsAndConditionsDialog.class);
            boolean firstRun = prefs.getBoolean("first_execution_terms", true);
            if (firstRun) {
                prefs.putBoolean("first_execution_terms", false);
                prefs.flush();
                System.out.println("🆕 Primera ejecución detectada");
                return true;
            }
            System.out.println("🔄 Ejecución posterior");
            return false;
        } catch (Exception e) {
            return true;
        }
    }
    
    public static void resetTerms() {
        try {
            java.util.prefs.Preferences prefs = 
                java.util.prefs.Preferences.userNodeForPackage(TermsAndConditionsDialog.class);
            prefs.remove("terms_accepted");
            prefs.remove("first_execution_terms");
            prefs.flush();
            System.out.println("🔄 Reset completado");
        } catch (Exception e) {
            System.err.println("⚠️ Error en reset: " + e.getMessage());
        }
    }
}