package com.dentalclinicsystem.main;

import com.dentalclinicsystem.config.*;
import com.dentalclinicsystem.controller.LoginController;
import com.dentalclinicsystem.dao.ConfiguracionDAO;
import com.dentalclinicsystem.service.ConfiguracionService;
import com.dentalclinicsystem.service.RecordatorioService;
import com.dentalclinicsystem.util.LicenseManager;
import com.dentalclinicsystem.view.ActivationDialog;
import com.dentalclinicsystem.view.LoginView;
import com.dentalclinicsystem.view.SplashScreen;
import com.dentalclinicsystem.view.TermsAndConditionsDialog;
import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import java.io.File;

/**
 * @author Miller
 */
public class Main {

    public static void main(String[] args) {

        // ================================================================
        // ========== PRUEBA DE DIRECTORIO =================================
        // ================================================================
        File testDir = new File("data");
        if (testDir.exists()) {
            System.out.println("✅ La carpeta 'data' EXISTE");
        } else {
            System.out.println("📁 La carpeta 'data' NO EXISTE, creándola...");
            boolean creado = testDir.mkdirs();
            System.out.println("Creación exitosa: " + creado);
        }
        System.out.println("=== FIN DE PRUEBA ===");

        try {
            // ================================================================
            // ========== CREAR CARPETA DATA ===================================
            // ================================================================
            File dataDir = new File("data");
            if (!dataDir.exists()) {
                dataDir.mkdirs();
            }

            // ================================================================
            // ========== TEMA MODERNO =========================================
            // ================================================================
            FlatDarkLaf.setup();

            UIManager.put("Button.arc", 15);
            UIManager.put("Component.arc", 15);
            UIManager.put("TextComponent.arc", 15);
            UIManager.put("ProgressBar.arc", 15);
            UIManager.put("Table.showHorizontalLines", true);
            UIManager.put("Table.showVerticalLines", false);
            UIManager.put("ScrollBar.width", 12);
            UIManager.put("Button.focusWidth", 0);

            // ================================================================
            // ========== PROBAR CONEXIÓN ======================================
            // ================================================================
            ConexionSQLite.conectar();

            // ================================================================
            // ========== CREAR TABLAS =========================================
            // ================================================================
            InitDatabase.crearTablas();

            // ================================================================
            // ========== CREAR USUARIO ADMIN ==================================
            // ================================================================
            DataSeeder dataSeeder = new DataSeeder();
            dataSeeder.crearAdmin();

            // ================================================================
            // ========== VALIDAR LICENCIA =====================================
            // ================================================================
            if (!LicenseManager.licenciaValida()) {
                ActivationDialog dialog = new ActivationDialog(null);
                dialog.setVisible(true);

                if (!LicenseManager.licenciaValida()) {
                    System.exit(0);
                }
            }
            
            // ================================================================
            // ========== ✅ RESET PARA PRUEBAS ===============================
            // ================================================================            
            //TermsAndConditionsDialog.resetTerms();
            
            // ================================================================
            // ✅ NUEVO: VALIDAR TÉRMINOS Y CONDICIONES (SOLO UNA VEZ)
            // ================================================================
          
            if (TermsAndConditionsDialog.isFirstExecution() || 
                !TermsAndConditionsDialog.hasAcceptedTerms()) {
                
                System.out.println("📋 Mostrando Términos y Condiciones");
                TermsAndConditionsDialog termsDialog = new TermsAndConditionsDialog(null);
                termsDialog.setVisible(true);
                
                if (!termsDialog.isAccepted()) {
                    System.out.println("❌ Usuario rechazó los términos");
                    System.exit(0);
                }
                
                System.out.println("✅ Términos aceptados");
            } else {
                System.out.println("✅ Términos ya aceptados previamente");
            }

            // ================================================================
            // ========== SPLASH SCREEN ========================================
            // ================================================================
            SplashScreen splash = new SplashScreen();
            splash.setVisible(true);
            Thread.sleep(2500);
            splash.dispose();

            // ================================================================
            // ========== CARGAR CONFIGURACIÓN DE FUENTE ======================
            // ================================================================
            ConfiguracionDAO confiDAO = new ConfiguracionDAO();
            String fontSize = confiDAO.obtener("font_size");

            if (fontSize == null || fontSize.isEmpty()) {
                fontSize = "14";
            }

            FontManager.aplicarFuente(Integer.parseInt(fontSize));
            System.out.println("✅ Sistema Iniciado.");

            // ================================================================
            // ========== 🔥 INICIAR VERIFICACIÓN DE ACTUALIZACIONES ==========
            // ================================================================
            // 🔥 Iniciamos el sistema de actualizaciones
            // NOTA: Se pasa null porque LoginView aún no está creada
            // El actualizador maneja el caso de parent = null
            iniciarSistemaActualizaciones();

            // ================================================================
            // ========== INICIALIZAR SERVICIO DE RECORDATORIOS ===============
            // ================================================================
            iniciarServicioRecordatorios();

            // ================================================================
            // ========== ABRIR LOGIN ==========================================
            // ================================================================
            LoginView loginView = new LoginView();
            new LoginController(loginView);
            loginView.setVisible(true);

        } catch (Exception e) {
            System.out.println("❌ Error : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ================================================================
    // ========== 🔥 INICIAR SISTEMA DE ACTUALIZACIONES ===============
    // ================================================================
    
    /**
     * Inicia el sistema de verificación automática de actualizaciones
     * Se ejecuta en segundo plano sin bloquear la aplicación
     */
    private static void iniciarSistemaActualizaciones() {
        System.out.println("==========================================");
        System.out.println("🔄 INICIANDO SISTEMA DE ACTUALIZACIONES");
        System.out.println("==========================================");
        
        try {
            // Verificar que exista el archivo de configuración
            String version = ActualizacionConfig.getVersion();
            String repo = ActualizacionConfig.getRepo();
            
            System.out.println("📌 Versión actual: " + version);
            System.out.println("📦 Repositorio: " + repo);
            
            if (version == null || version.isEmpty() || version.equals("1.0.0")) {
                System.out.println("⚠️ Versión no configurada correctamente en app.properties");
                System.out.println("   Creando archivo app.properties con valores por defecto...");
                ActualizacionConfig.setVersion("1.0.0");
                ActualizacionConfig.setRepo("Miller38/dental-clinic-system");
            }
            
            // 🔥 Iniciar el sistema de actualizaciones automáticas
            // Pasamos null porque aún no tenemos el JFrame padre
            ActualizadorApp.iniciarVerificacionAutomatica(null);
            
            System.out.println("✅ Sistema de actualizaciones iniciado correctamente");
            System.out.println("   📅 Verificación inmediata: en progreso...");
            System.out.println("   ⏰ Próxima verificación: en 24 horas");
            System.out.println("==========================================");
            
        } catch (Exception e) {
            System.err.println("❌ Error al iniciar sistema de actualizaciones: " + e.getMessage());
            e.printStackTrace();
            System.out.println("⚠️ El sistema continuará funcionando sin verificación de actualizaciones");
        }
    }

    // ================================================================
    // ========== INICIALIZAR SERVICIO DE RECORDATORIOS ===============
    // ================================================================

    private static void iniciarServicioRecordatorios() {
        System.out.println("==========================================");
        System.out.println("🔔 INICIANDO SERVICIO DE RECORDATORIOS");
        System.out.println("==========================================");

        try {
            // Verificar configuración de email
            ConfiguracionService config = ConfiguracionService.getInstance();

            if (!config.credencialesConfiguradas()) {
                System.err.println("⚠️ CREDENCIALES DE EMAIL NO CONFIGURADAS");
                System.err.println("📝 Crea el archivo: src/config.properties");
                System.err.println("📝 Con el siguiente contenido:");
                System.err.println("   email.user=tu_correo@gmail.com");
                System.err.println("   email.password=tu_contraseña_app");
                System.err.println("   email.destino=destino@gmail.com");
                System.err.println("   smtp.host=smtp.gmail.com");
                System.err.println("   smtp.port=587");
                System.err.println("   iva.porcentaje=19");
                System.err.println("");
                System.err.println("⚠️ Los recordatorios NO funcionarán hasta que configures el email");
                System.out.println("==========================================");
                return;
            }

            System.out.println("✅ Configuración de email encontrada:");
            System.out.println("    📧 Usuario: " + config.getEmailUser());
            System.out.println("    📧 Destino: " + config.getEmailDestino());

            // Iniciar el servicio de recordatorios
            RecordatorioService recordatorioService = RecordatorioService.getInstance();
            recordatorioService.iniciarServicio();

            System.out.println("✅ Servicio de recordatorios iniciado correctamente");
            System.out.println("   ⏰ Se ejecutará automáticamente cada 30 minutos");
            System.out.println("==========================================");

        } catch (Exception e) {
            System.err.println("❌ Error al iniciar servicio de recordatorios: " + e.getMessage());
            e.printStackTrace();
        }
    }
}