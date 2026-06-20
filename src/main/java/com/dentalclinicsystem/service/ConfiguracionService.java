package com.dentalclinicsystem.service;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class ConfiguracionService {
    private static ConfiguracionService instancia;
    private Properties props;
    private boolean configuracionCargada = false;
    
    private ConfiguracionService() {
        props = new Properties();
        cargarConfiguracion();
    }
    
    public static ConfiguracionService getInstance() {
        if (instancia == null) {
            instancia = new ConfiguracionService();
        }
        return instancia;
    }
    
    private void cargarConfiguracion() {
        System.out.println("🔍 Buscando config.properties...");
        
        // ============================================
        // OPCIÓN 1: Cargar desde ruta ABSOLUTA
        // ============================================
        String[] rutasPosibles = {
            "D:\\DentalClinicSystem\\src\\config.properties",
            "C:\\DentalClinicSystem\\src\\config.properties",
            System.getProperty("user.dir") + "\\src\\config.properties",
            System.getProperty("user.dir") + "\\config.properties"
        };
        
        for (String ruta : rutasPosibles) {
            try {
                System.out.println("📁 Intentando cargar desde: " + ruta);
                try (FileInputStream input = new FileInputStream(ruta)) {
                    props.load(input);
                    configuracionCargada = true;
                    System.out.println("✅ Configuración cargada desde: " + ruta);
                    imprimirConfiguracion();
                    return;
                }
            } catch (Exception e) {
                // Continuar con la siguiente ruta
            }
        }
        
        // ============================================
        // OPCIÓN 2: Cargar desde CLASSPATH
        // ============================================
        try {
            java.net.URL url = getClass().getClassLoader().getResource("config.properties");
            System.out.println("📁 Buscando en classpath: " + url);
            
            try (InputStream input = getClass().getClassLoader()
                    .getResourceAsStream("config.properties")) {
                
                if (input != null) {
                    props.load(input);
                    configuracionCargada = true;
                    System.out.println("✅ Configuración cargada desde classpath");
                    imprimirConfiguracion();
                    return;
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Error cargando desde classpath: " + e.getMessage());
        }
        
        // ============================================
        // OPCIÓN 3: Cargar desde VARIABLES DE ENTORNO
        // ============================================
        cargarDesdeVariablesEntorno();
        
        // ============================================
        // OPCIÓN 4: Mostrar ERROR si no hay configuración
        // ============================================
        if (!configuracionCargada) {
            System.err.println("❌ ERROR CRÍTICO: No se pudo cargar la configuración");
            System.err.println("📁 Crea el archivo config.properties en:");
            System.err.println("   D:\\DentalClinicSystem\\src\\config.properties");
            System.err.println("");
            System.err.println("📝 Contenido del archivo:");
            System.err.println("   email.user=tu_correo@gmail.com");
            System.err.println("   email.password=tu_contraseña_app");
            System.err.println("   email.destino=destino@gmail.com");
            System.err.println("   smtp.host=smtp.gmail.com");
            System.err.println("   smtp.port=587");
            System.err.println("   iva.porcentaje=19");
            System.err.println("   empresa.nombre=Mi Clinica");
            System.err.println("");
            System.err.println("⚠️ O configura variables de entorno:");
            System.err.println("   EMAIL_USER");
            System.err.println("   EMAIL_PASSWORD");
            System.err.println("   EMAIL_DESTINO");
            
            // 🔥 NO PONER CREDENCIALES EN EL CÓDIGO
            // En lugar de credenciales fijas, lanzar excepción o mostrar error
            configuracionCargada = false;
        }
    }
    
    private void cargarDesdeVariablesEntorno() {
        String emailUser = System.getenv("EMAIL_USER");
        if (emailUser != null && !emailUser.isEmpty()) {
            props.setProperty("email.user", emailUser);
            configuracionCargada = true;
            System.out.println("✅ EMAIL_USER cargado desde variable de entorno");
        }
        
        String emailPassword = System.getenv("EMAIL_PASSWORD");
        if (emailPassword != null && !emailPassword.isEmpty()) {
            props.setProperty("email.password", emailPassword);
            configuracionCargada = true;
            System.out.println("✅ EMAIL_PASSWORD cargado desde variable de entorno");
        }
        
        String emailDestino = System.getenv("EMAIL_DESTINO");
        if (emailDestino != null && !emailDestino.isEmpty()) {
            props.setProperty("email.destino", emailDestino);
        }
        
        String smtpHost = System.getenv("SMTP_HOST");
        if (smtpHost != null && !smtpHost.isEmpty()) {
            props.setProperty("smtp.host", smtpHost);
        }
        
        String smtpPort = System.getenv("SMTP_PORT");
        if (smtpPort != null && !smtpPort.isEmpty()) {
            props.setProperty("smtp.port", smtpPort);
        }
    }
    
    private void imprimirConfiguracion() {
        System.out.println("📧 EMAIL_USER: " + props.getProperty("email.user"));
        System.out.println("📧 EMAIL_PASSWORD: " + 
            (props.getProperty("email.password") != null ? "✅ Cargada" : "❌ No cargada"));
        System.out.println("📧 EMAIL_DESTINO: " + props.getProperty("email.destino"));
        System.out.println("🏢 EMPRESA: " + props.getProperty("empresa.nombre"));
        System.out.println("📊 IVA: " + props.getProperty("iva.porcentaje") + "%");
    }
    
    // ========== MÉTODOS GET ==========
    
    public String getEmailUser() {
        return props.getProperty("email.user");
    }
    
    public String getEmailPassword() {
        return props.getProperty("email.password");
    }
    
    public String getEmailDestino() {
        return props.getProperty("email.destino", "admin@dentalclinic.com");
    }
    
    public String getEmailInfo() {
        return props.getProperty("email.info", "info@dentalclinic.com");
    }
    
    public String getEmpresaNombre() {
        return props.getProperty("empresa.nombre", "Dental Clinic System");
    }
    
    public String getEmpresaTelefono() {
        return props.getProperty("empresa.telefono", "300-000-0000");
    }
    
    public String getEncuestasDirectorio() {
        return props.getProperty("encuestas.directorio", "encuestas");
    }
    
    public String getFacturasDirectorio() {
        return props.getProperty("facturas.directorio", "facturas");
    }
    
    public String getSmtpHost() {
        return props.getProperty("smtp.host", "smtp.gmail.com");
    }
    
    public String getSmtpPort() {
        return props.getProperty("smtp.port", "587");
    }
    
    public double getIvaPorcentaje() {
        try {
            String valor = props.getProperty("iva.porcentaje", "19");
            return Double.parseDouble(valor) / 100.0;
        } catch (NumberFormatException e) {
            return 0.19;
        }
    }
    
    public double getIvaPorcentajeNumerico() {
        try {
            String valor = props.getProperty("iva.porcentaje", "19");
            return Double.parseDouble(valor);
        } catch (NumberFormatException e) {
            return 19.0;
        }
    }
    
    public boolean isConfiguracionCargada() {
        return configuracionCargada;
    }
    
    public boolean credencialesConfiguradas() {
        String user = getEmailUser();
        String pass = getEmailPassword();
        return user != null && !user.isEmpty() && 
               pass != null && !pass.isEmpty();
    }
    
    /**
     * Verifica si la configuración es válida (no son valores por defecto)
     */
    public boolean esConfiguracionValida() {
        if (!configuracionCargada) {
            return false;
        }
        String user = getEmailUser();
        return user != null && !user.isEmpty() && 
               !user.equals("tu_correo@gmail.com") &&
               credencialesConfiguradas();
    }
}