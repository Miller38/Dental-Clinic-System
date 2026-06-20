package com.dentalclinicsystem.main;

import com.dentalclinicsystem.config.ConexionSQLite;
import com.dentalclinicsystem.config.DataSeeder;
import com.dentalclinicsystem.config.FontManager;
import com.dentalclinicsystem.config.InitDatabase;
import com.dentalclinicsystem.controller.LoginController;
import com.dentalclinicsystem.dao.ConfiguracionDAO;
import com.dentalclinicsystem.service.ConfiguracionService;
import com.dentalclinicsystem.service.RecordatorioService;
import com.dentalclinicsystem.util.LicenseManager;
import com.dentalclinicsystem.view.ActivationDialog;
import com.dentalclinicsystem.view.LoginView;
import com.dentalclinicsystem.view.SplashScreen;
import com.formdev.flatlaf.FlatDarkLaf;
import java.io.File;
import javax.swing.UIManager;

/**
 *
 * @author Miller
 */
public class Main {

    public static void main(String[] args) {
        
        File testDir = new File("data");
        if (testDir.exists()) {
            System.out.println("La carpeta 'data' EXISTE");
        } else {
            System.out.println("La carpeta 'data' NO EXISTE, creándola...");
            boolean creado = testDir.mkdirs();
            System.out.println("Creación exitosa: " + creado);
        }
        System.out.println("=== FIN DE PRUEBA ===");
        
        try {
            //-------------------------------crear carpeta para año jar bd---------------------------//
            // Crear carpeta data/ si no existe
            File dataDir = new File("data");
            if (!dataDir.exists()) {
                dataDir.mkdirs();
            }
            // ------------------------------------Tema Moderno-------------------------------------//
            FlatDarkLaf.setup();

            UIManager.put("Button.arc", 15);
            UIManager.put("Component.arc", 15);
            UIManager.put("TextComponent.arc", 15);
            UIManager.put("ProgressBar.arc", 15);
            UIManager.put("Table.showHorizontalLines", true);
            UIManager.put("Table.showVerticalLines", false);
            UIManager.put("ScrollBar.width", 12);
            UIManager.put("Button.focusWidth", 0);
            // ------------------------------------Probar Conexion------------------------------------//
            ConexionSQLite.conectar();
            // ---------------------------------- Creamos las tablas----------------------------------//
            InitDatabase.crearTablas();
            //-----------------------------------creamos el usuario inicial----------------------------//
            DataSeeder dataSeeder = new DataSeeder();
            dataSeeder.crearAdmin();
            //---------------------------------------------valida licencia -------------------------------//
            if (!LicenseManager.licenciaValida()) {
                ActivationDialog dialog = new ActivationDialog(null);
                dialog.setVisible(true);

                if (!LicenseManager.licenciaValida()) {
                    System.exit(0);
                }
            }
            //---------------------------------------- abre splashScreen--------------------------------//
            SplashScreen splash = new SplashScreen();
            splash.setVisible(true);
            Thread.sleep(2500);
            splash.dispose();
            //--------------------------------cargamos la configuarcion de la fuente----------------//
            ConfiguracionDAO confiDAO = new ConfiguracionDAO();
            String fontSize = confiDAO.obtener("font_size");    

            if (fontSize == null || fontSize.isEmpty()) {
                fontSize = "14";
            }

            FontManager.aplicarFuente(Integer.parseInt(fontSize));
            System.out.println("Sistema Iniciado.");
            //------------------------------Inicializar Servicio de Recordatorios--------------------//          
            iniciarServicioRecordatorios();
            //-------------------------------------Abrimos el login----------------------------------//
            LoginView loginView = new LoginView();
            new LoginController(loginView);
            loginView.setVisible(true);

        } catch (Exception e) {
            System.out.println("Error : " + e.getMessage());
            e.printStackTrace();
        }
    }    
     // --------------------Inicializa el servicio de recordatorios automáticos --------------------//
     
    private static void iniciarServicioRecordatorios() {
        System.out.println("==========================================");
        System.out.println("🔔 INICIANDO SERVICIO DE RECORDATORIOS");
        System.out.println("==========================================");
        
        try {
      //------------------------------Verificar configuración de email-----------------------------//
            ConfiguracionService config = ConfiguracionService.getInstance();
            
            if (!config.credencialesConfiguradas()) {
                System.err.println("CREDENCIALES DE EMAIL NO CONFIGURADAS");
                System.err.println("Crea el archivo: src/config.properties");
                System.err.println("Con el siguiente contenido:");
                System.err.println("   email.user=tu_correo@gmail.com");
                System.err.println("   email.password=tu_contraseña_app");
                System.err.println("   email.destino=destino@gmail.com");
                System.err.println("   smtp.host=smtp.gmail.com");
                System.err.println("   smtp.port=587");
                System.err.println("   iva.porcentaje=19");
                System.err.println("");
                System.err.println("️ Los recordatorios NO funcionarán hasta que configures el email");
                System.out.println("==========================================");
                return;
            }
            
            System.out.println(" Configuración de email encontrada:");
            System.out.println("    Usuario: " + config.getEmailUser());
            System.out.println("    Destino: " + config.getEmailDestino());
            
            //----------------------------- Iniciar el servicio de recordatorios-------------------------//
            RecordatorioService recordatorioService = RecordatorioService.getInstance();
            recordatorioService.iniciarServicio();
            
            System.out.println("Servicio de recordatorios iniciado correctamente");
            System.out.println("Se ejecutará automáticamente cada 5 minutos");
            System.out.println("==========================================");
            
        } catch (Exception e) {
            System.err.println("Error al iniciar servicio de recordatorios: " + e.getMessage());
            e.printStackTrace();
        }
    }
}