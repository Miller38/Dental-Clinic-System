package com.dentalclinicsystem.service;

import javax.swing.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EncuestaService {
    
    private ConfiguracionService configService;
    private EmailService emailService;
    private String encuestasDir;
    
    public EncuestaService() {
        this.configService = ConfiguracionService.getInstance();
        this.emailService = EmailService.getInstance();
        this.encuestasDir = configService.getEncuestasDirectorio();
        crearDirectorio();
        
        // Mostrar estado de configuración
        if (!configService.credencialesConfiguradas()) {
            System.err.println("⚠️ ADVERTENCIA: Credenciales de email NO configuradas");
            System.err.println("   Las encuestas se guardarán localmente pero NO se enviarán por email");
        } else {
            System.out.println("✅ EncuestaService inicializado correctamente");
        }
    }
    
    /**
     * Método ESTÁTICO para obtener el email destino
     * Puede ser llamado desde cualquier lugar sin instanciar la clase
     */
    public static String getEmailDestino() {
        try {
            ConfiguracionService config = ConfiguracionService.getInstance();
            String email = config.getEmailDestino();
            return (email != null && !email.isEmpty()) ? email : "admin@dentalclinic.com";
        } catch (Exception e) {
            return "admin@dentalclinic.com"; // Fallback seguro
        }
    }
    
    /**
     * Método de INSTANCIA para obtener el email destino
     */
    public String getEmailDestinoInstancia() {
        try {
            String email = configService.getEmailDestino();
            return (email != null && !email.isEmpty()) ? email : "admin@dentalclinic.com";
        } catch (Exception e) {
            return "admin@dentalclinic.com";
        }
    }
    
    private void crearDirectorio() {
        File dir = new File(encuestasDir);
        if (!dir.exists()) {
            dir.mkdirs();
            System.out.println("📁 Directorio de encuestas creado: " + dir.getAbsolutePath());
        }
    }
    
    /**
     * Envía la encuesta por email y guarda copia local
     */
    public void enviarEncuesta(String nombreCliente, String emailCliente, 
                               String telefonoCliente, String consultorio,
                               String[] respuestas, String[] preguntas,
                               double calificacionGeneral, String comentarios,
                               String mejoras, String competencia, 
                               String recomendacion, String precio) {
        try {
            // 1. Generar el contenido HTML de la encuesta
            String htmlContent = generarHTMLEncuesta(nombreCliente, emailCliente, 
                telefonoCliente, consultorio, respuestas, preguntas, 
                calificacionGeneral, comentarios, mejoras, competencia, 
                recomendacion, precio);
            
            // 2. Guardar copia local (backup)
            guardarCopiaLocal(nombreCliente, emailCliente, telefonoCliente, 
                consultorio, respuestas, preguntas, calificacionGeneral, 
                comentarios, mejoras, competencia, recomendacion, precio);
            
            // 3. Verificar credenciales antes de enviar
            if (!configService.credencialesConfiguradas()) {
                JOptionPane.showMessageDialog(null, 
                    "⚠️ No se puede enviar el email porque las credenciales no están configuradas.\n" +
                    "La encuesta se guardó localmente.\n\n" +
                    "Para enviar por email, configura:\n" +
                    "1. Archivo config.properties en src/\n" +
                    "2. O variables de entorno: EMAIL_USER y EMAIL_PASSWORD",
                    "Configuración incompleta",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // 4. Enviar por email
            String asunto = "📋 Nueva Encuesta - " + nombreCliente + " - " + configService.getEmpresaNombre();
            boolean enviado = emailService.enviarEmail(
                configService.getEmailDestino(), 
                asunto, 
                htmlContent
            );
            
            if (enviado) {
                JOptionPane.showMessageDialog(null, 
                    "✅ ¡Encuesta enviada exitosamente!\n\n" +
                    "Hemos recibido tus respuestas. ¡Gracias por tu participación!\n" +
                    "Tus comentarios nos ayudarán a mejorar " + configService.getEmpresaNombre() + ".",
                    "¡Muchas gracias!",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, 
                    "⚠️ La encuesta se guardó localmente pero no se pudo enviar por email.\n" +
                    "Por favor, contacta al administrador.",
                    "Error de envío",
                    JOptionPane.WARNING_MESSAGE);
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error al enviar encuesta: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Error al enviar la encuesta: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Genera el HTML de la encuesta para el email
     */
    private String generarHTMLEncuesta(String nombre, String email, String telefono, 
                                       String consultorio, String[] respuestas, 
                                       String[] preguntas, double calificacion,
                                       String comentarios, String mejoras, 
                                       String competencia, String recomendacion, 
                                       String precio) {
        
        String empresaNombre = configService.getEmpresaNombre();
        String empresaTelefono = configService.getEmpresaTelefono();
        String emailInfo = configService.getEmailInfo();
        
        StringBuilder html = new StringBuilder();
        
        html.append("<!DOCTYPE html>");
        html.append("<html>");
        html.append("<head>");
        html.append("<meta charset='UTF-8'>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px; }");
        html.append(".container { max-width: 800px; margin: auto; background-color: #ffffff; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }");
        html.append(".header { text-align: center; border-bottom: 2px solid #2c3e50; padding-bottom: 15px; margin-bottom: 20px; }");
        html.append(".header h1 { color: #2c3e50; margin: 0; }");
        html.append(".header p { color: #7f8c8d; margin: 5px 0; }");
        html.append(".section { margin: 20px 0; padding: 15px; background: #f8f9fa; border-radius: 5px; }");
        html.append(".section-title { font-size: 16px; font-weight: bold; color: #2c3e50; margin-bottom: 10px; }");
        html.append(".info-item { padding: 5px 0; border-bottom: 1px solid #eee; }");
        html.append(".info-item strong { color: #2c3e50; }");
        html.append(".respuesta { padding: 5px 15px; margin: 5px 0; background: white; border-radius: 3px; }");
        html.append(".calificacion { font-size: 24px; font-weight: bold; color: #27ae60; }");
        html.append(".footer { text-align: center; margin-top: 30px; padding-top: 15px; border-top: 1px solid #ddd; color: #95a5a6; font-size: 12px; }");
        html.append(".badge { display: inline-block; padding: 3px 10px; border-radius: 15px; font-size: 12px; font-weight: bold; }");
        html.append(".badge-alta { background-color: #27ae60; color: white; }");
        html.append(".badge-media { background-color: #f39c12; color: white; }");
        html.append(".badge-baja { background-color: #e74c3c; color: white; }");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");
        html.append("<div class='container'>");
        
        // Header
        html.append("<div class='header'>");
        html.append("<h1>📋 Nueva Encuesta de Satisfacción</h1>");
        html.append("<p>").append(empresaNombre).append(" - ").append(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date())).append("</p>");
        html.append("</div>");
        
        // Datos del cliente
        html.append("<div class='section'>");
        html.append("<div class='section-title'>👤 Datos del Cliente</div>");
        html.append("<div class='info-item'><strong>Nombre:</strong> ").append(nombre).append("</div>");
        html.append("<div class='info-item'><strong>Email:</strong> ").append(email).append("</div>");
        html.append("<div class='info-item'><strong>Teléfono:</strong> ").append(telefono != null && !telefono.isEmpty() ? telefono : "No registrado").append("</div>");
        html.append("<div class='info-item'><strong>Consultorio:</strong> ").append(consultorio != null && !consultorio.isEmpty() ? consultorio : "No registrado").append("</div>");
        html.append("</div>");
        
        // Respuestas
        html.append("<div class='section'>");
        html.append("<div class='section-title'>📝 Calificaciones</div>");
        
        int maxPreguntas = Math.min(preguntas.length, respuestas.length);
        for (int i = 0; i < maxPreguntas; i++) {
            String respuesta = respuestas[i] != null ? respuestas[i] : "No respondida";
            String badgeClass = "badge-media";
            if (respuesta.contains("Excelente") || respuesta.contains("5")) badgeClass = "badge-alta";
            if (respuesta.contains("Malo") || respuesta.contains("Muy Malo") || respuesta.contains("1") || respuesta.contains("2")) badgeClass = "badge-baja";
            
            html.append("<div class='respuesta'>");
            html.append("<strong>").append(preguntas[i]).append("</strong><br>");
            html.append("<span class='badge ").append(badgeClass).append("'>").append(respuesta).append("</span>");
            html.append("</div>");
        }
        html.append("</div>");
        
        // Calificación General
        html.append("<div class='section'>");
        html.append("<div class='section-title'>⭐ Calificación General</div>");
        String estrellas = "";
        for (int i = 0; i < (int)calificacion; i++) estrellas += "⭐";
        html.append("<div class='calificacion'>").append(estrellas).append(" ").append(String.format("%.1f", calificacion)).append("/5</div>");
        html.append("</div>");
        
        // Recomendación
        html.append("<div class='section'>");
        html.append("<div class='section-title'>📢 Recomendación</div>");
        html.append("<div class='info-item'><strong>¿Recomendaría el software?</strong> ").append(recomendacion != null && !recomendacion.isEmpty() ? recomendacion : "No respondida").append("</div>");
        html.append("</div>");
        
        // Precio
        html.append("<div class='section'>");
        html.append("<div class='section-title'>💰 Disposición a pagar</div>");
        html.append("<div class='info-item'><strong>Precio sugerido:</strong> ").append(precio != null && !precio.equals("Seleccionar...") ? precio : "No respondida").append("</div>");
        html.append("</div>");
        
        // Preguntas abiertas
        html.append("<div class='section'>");
        html.append("<div class='section-title'>💬 Comentarios del Cliente</div>");
        
        html.append("<div class='info-item'><strong>✅ ¿Qué fue lo que más le gustó?</strong><br>").append(comentarios != null && !comentarios.isEmpty() ? comentarios : "No respondido").append("</div>");
        html.append("<div class='info-item'><strong>❌ ¿Qué fue lo que menos le gustó?</strong><br>").append(comentarios != null && !comentarios.isEmpty() ? comentarios : "No respondido").append("</div>");
        html.append("<div class='info-item'><strong>💡 ¿Qué funcionalidad agregaría o mejoraría?</strong><br>").append(mejoras != null && !mejoras.isEmpty() ? mejoras : "No respondido").append("</div>");
        html.append("<div class='info-item'><strong>🏢 Competencia</strong><br>").append(competencia != null && !competencia.isEmpty() ? competencia : "No respondido").append("</div>");
        html.append("</div>");
        
        // Footer
        html.append("<div class='footer'>");
        html.append("<p>Encuesta generada automáticamente por ").append(empresaNombre).append("</p>");
        html.append("<p>📞 Teléfono: ").append(empresaTelefono).append(" | 📧 Email: ").append(emailInfo).append("</p>");
        html.append("<p>© ").append(new SimpleDateFormat("yyyy").format(new Date())).append(" - Todos los derechos reservados</p>");
        html.append("</div>");
        
        html.append("</div>");
        html.append("</body>");
        html.append("</html>");
        
        return html.toString();
    }
    
    /**
     * Guarda una copia local de la encuesta
     */
    private void guardarCopiaLocal(String nombre, String email, String telefono, 
                                   String consultorio, String[] respuestas, 
                                   String[] preguntas, double calificacion,
                                   String comentarios, String mejoras, 
                                   String competencia, String recomendacion, 
                                   String precio) {
        try {
            String fecha = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            String nombreArchivo = encuestasDir + File.separator + 
                                   "encuesta_" + nombre.replace(" ", "_") + "_" + fecha + ".txt";
            
            String empresaNombre = configService.getEmpresaNombre();
            String empresaTelefono = configService.getEmpresaTelefono();
            String emailInfo = configService.getEmailInfo();
            
            try (PrintWriter writer = new PrintWriter(new FileWriter(nombreArchivo))) {
                writer.println("========================================");
                writer.println("      ENCUESTA DE SATISFACCIÓN");
                writer.println("      " + empresaNombre);
                writer.println("========================================\n");
                writer.println("Fecha: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
                writer.println("Cliente: " + nombre);
                writer.println("Email: " + email);
                writer.println("Teléfono: " + (telefono != null && !telefono.isEmpty() ? telefono : "No registrado"));
                writer.println("Consultorio: " + (consultorio != null && !consultorio.isEmpty() ? consultorio : "No registrado"));
                writer.println("\n----------------------------------------");
                writer.println("           RESULTADOS");
                writer.println("----------------------------------------\n");
                
                int maxPreguntas = Math.min(preguntas.length, respuestas.length);
                for (int i = 0; i < maxPreguntas; i++) {
                    writer.println(preguntas[i]);
                    writer.println("   Respuesta: " + (respuestas[i] != null ? respuestas[i] : "No respondida"));
                    writer.println();
                }
                
                writer.println("----------------------------------------");
                writer.println("Calificación General: " + calificacion + " / 5");
                writer.println("Recomendación: " + (recomendacion != null && !recomendacion.isEmpty() ? recomendacion : "No respondida"));
                writer.println("Precio sugerido: " + (precio != null && !precio.equals("Seleccionar...") ? precio : "No respondido"));
                writer.println("\n----------------------------------------");
                writer.println("✅ ¿Qué fue lo que más le gustó?");
                writer.println(comentarios != null && !comentarios.isEmpty() ? comentarios : "No respondido");
                writer.println("\n❌ ¿Qué fue lo que menos le gustó?");
                writer.println(comentarios != null && !comentarios.isEmpty() ? comentarios : "No respondido");
                writer.println("\n💡 ¿Qué funcionalidad agregaría o mejoraría?");
                writer.println(mejoras != null && !mejoras.isEmpty() ? mejoras : "No respondido");
                writer.println("\n🏢 Competencia");
                writer.println(competencia != null && !competencia.isEmpty() ? competencia : "No respondido");
                writer.println("\n========================================");
                writer.println("   GRACIAS POR SU PARTICIPACIÓN");
                writer.println("========================================");
                writer.println("   " + empresaNombre);
                writer.println("   Teléfono: " + empresaTelefono);
                writer.println("   Email: " + emailInfo);
                writer.println("========================================");
            }
            
            System.out.println("✅ Copia local guardada: " + nombreArchivo);
            
        } catch (IOException e) {
            System.err.println("❌ Error al guardar copia local: " + e.getMessage());
        }
    }
}