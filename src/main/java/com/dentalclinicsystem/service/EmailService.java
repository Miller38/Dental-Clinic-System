package com.dentalclinicsystem.service;

import com.dentalclinicsystem.model.Cita;
import com.dentalclinicsystem.model.Paciente;
import com.dentalclinicsystem.model.Venta;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailService {
    private static EmailService instance;
    private ConfiguracionService configService;
    
    private EmailService() {
        this.configService = ConfiguracionService.getInstance();
        
        if (!configService.credencialesConfiguradas()) {
            System.err.println("⚠️ ADVERTENCIA: Credenciales de email NO configuradas");
            System.err.println("   El sistema USARÁ VALORES POR DEFECTO (puede fallar)");
        } else {
            System.out.println("✅ EmailService inicializado correctamente");
        }
    }
    
    public static EmailService getInstance() {
        if (instance == null) {
            instance = new EmailService();
        }
        return instance;
    }
    
    public boolean enviarEmail(String destino, String asunto, String mensajeHTML) {
        if (destino == null || destino.isEmpty()) {
            System.err.println("❌ Destino de email vacío");
            return false;
        }
        
        try {
            String emailUser = configService.getEmailUser();
            String emailPassword = configService.getEmailPassword();
            String smtpHost = configService.getSmtpHost();
            String smtpPort = configService.getSmtpPort();
            String empresaNombre = configService.getEmpresaNombre();
            
            if (emailUser == null || emailUser.isEmpty() || 
                emailPassword == null || emailPassword.isEmpty()) {
                System.err.println("❌ Credenciales inválidas o vacías");
                return false;
            }
            
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", smtpHost);
            props.put("mail.smtp.port", smtpPort);
            props.put("mail.smtp.ssl.trust", smtpHost);
            
            final String user = emailUser;
            final String pass = emailPassword;
            
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(user, pass);
                }
            });
            
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(user, empresaNombre));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destino));
            message.setSubject(asunto);
            message.setContent(mensajeHTML, "text/html; charset=utf-8");
            
            Transport.send(message);
            System.out.println("✅ Email enviado a: " + destino);
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ Error al enviar email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean enviarFactura(Venta venta, Paciente paciente, String facturaHTML) {
        if (paciente.getEmail() == null || paciente.getEmail().isEmpty()) {
            System.err.println("❌ El paciente no tiene email: " + paciente.getNombreCompleto());
            return false;
        }
        
        String empresaNombre = configService.getEmpresaNombre();
        String asunto = "🧾 Factura " + empresaNombre + " - " + 
                        venta.getTipoComprobanteTexto() + " N° " + 
                        (venta.getNumeroComprobante() != null ? venta.getNumeroComprobante() : "S/N");
        
        String mensaje = generarMensajeFactura(venta, paciente, facturaHTML);
        
        return enviarEmail(paciente.getEmail(), asunto, mensaje);
    }
    
    private String generarMensajeFactura(Venta venta, Paciente paciente, String facturaHTML) {
        String empresaNombre = configService.getEmpresaNombre();
        String empresaTelefono = configService.getEmpresaTelefono();
        String emailInfo = configService.getEmailInfo();
        
        return "<html>" +
               "<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;'>" +
               "<div style='max-width: 700px; margin: auto;'>" +
               "<div style='background-color: #ffffff; padding: 20px; border-radius: 10px; margin-bottom: 15px;'>" +
               "<h2 style='color: #2c3e50; text-align: center;'>📧 Factura Enviada</h2>" +
               "<p>Estimado/a <strong>" + paciente.getNombreCompleto() + "</strong>,</p>" +
               "<p>Le enviamos la factura correspondiente a su servicio en " + empresaNombre + ".</p>" +
               "<p><strong>Total:</strong> " + venta.getTotalFormateado() + "</p>" +
               "<p><strong>Método de pago:</strong> " + venta.getMetodoPagoTexto() + "</p>" +
               "<p style='color: #7f8c8d; font-size: 14px;'>Si tiene alguna duda, no dude en contactarnos.</p>" +
               "</div>" +
               facturaHTML +
               "<div style='text-align: center; color: #95a5a6; font-size: 12px; margin-top: 15px;'>" +
               "<p>Este es un mensaje automático, por favor no responda a este email.</p>" +
               "</div>" +
               "</div>" +
               "</body>" +
               "</html>";
    }
    
    public String generarMensajeRecordatorio(Cita cita, Paciente paciente, int horas) {
        String tiempo = horas == 24 ? "mañana" : "en " + horas + " hora(s)";
        String empresaNombre = configService.getEmpresaNombre();
        String empresaTelefono = configService.getEmpresaTelefono();
        String emailInfo = configService.getEmailInfo();
        
        return "<html>" +
               "<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;'>" +
               "<div style='max-width: 600px; margin: auto; background-color: #ffffff; padding: 30px; border-radius: 10px; box-shadow: 0 2px 5px rgba(0,0,0,0.1);'>" +
               "<h1 style='color: #2c3e50; text-align: center;'>🦷 " + empresaNombre + "</h1>" +
               "<hr style='border: 1px solid #eee;'>" +
               "<h2 style='color: #2c3e50;'>Recordatorio de Cita</h2>" +
               "<p>Estimado/a <strong>" + paciente.getNombreCompleto() + "</strong>,</p>" +
               "<p>Le recordamos que tiene una cita <strong>" + tiempo + "</strong>:</p>" +
               "<div style='background-color: #f8f9fa; padding: 15px; border-radius: 5px; margin: 15px 0;'>" +
               "<p><strong>📅 Fecha:</strong> " + cita.getFecha() + "</p>" +
               "<p><strong>⏰ Hora:</strong> " + cita.getHora() + "</p>" +
               "<p><strong>👨‍⚕️ Odontólogo:</strong> " + cita.getOdontologoNombre() + "</p>" +
               "<p><strong>💉 Servicio:</strong> " + cita.getServicioNombre() + "</p>" +
               "</div>" +
               "<p style='color: #7f8c8d; font-size: 14px;'>Si no puede asistir, por favor comuníquese con nosotros para reagendar.</p>" +
               "<hr style='border: 1px solid #eee;'>" +
               "<p style='text-align: center; color: #95a5a6; font-size: 12px;'>" +
               "📞 Teléfono: " + empresaTelefono + " | 📧 Email: " + emailInfo + "</p>" +
               "<p style='text-align: center; color: #95a5a6; font-size: 12px;'>Este es un mensaje automático, por favor no responda a este email.</p>" +
               "</div>" +
               "</body>" +
               "</html>";
    }
    
    public String generarMensajeConfirmacion(Cita cita, Paciente paciente) {
        String empresaNombre = configService.getEmpresaNombre();
        String empresaTelefono = configService.getEmpresaTelefono();
        String emailInfo = configService.getEmailInfo();
        
        return "<html>" +
               "<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;'>" +
               "<div style='max-width: 600px; margin: auto; background-color: #ffffff; padding: 30px; border-radius: 10px; box-shadow: 0 2px 5px rgba(0,0,0,0.1);'>" +
               "<h1 style='color: #2c3e50; text-align: center;'>🦷 " + empresaNombre + "</h1>" +
               "<hr style='border: 1px solid #eee;'>" +
               "<h2 style='color: #2c3e50;'>✅ Cita Confirmada</h2>" +
               "<p>Estimado/a <strong>" + paciente.getNombreCompleto() + "</strong>,</p>" +
               "<p>Su cita ha sido <strong style='color: #27ae60;'>confirmada</strong> con los siguientes detalles:</p>" +
               "<div style='background-color: #f8f9fa; padding: 15px; border-radius: 5px; margin: 15px 0;'>" +
               "<p><strong>📅 Fecha:</strong> " + cita.getFecha() + "</p>" +
               "<p><strong>⏰ Hora:</strong> " + cita.getHora() + "</p>" +
               "<p><strong>👨‍⚕️ Odontólogo:</strong> " + cita.getOdontologoNombre() + "</p>" +
               "<p><strong>💉 Servicio:</strong> " + cita.getServicioNombre() + "</p>" +
               "</div>" +
               "<p style='color: #7f8c8d; font-size: 14px;'>Le esperamos en nuestra clínica. Si tiene alguna duda, no dude en contactarnos.</p>" +
               "<hr style='border: 1px solid #eee;'>" +
               "<p style='text-align: center; color: #95a5a6; font-size: 12px;'>" +
               "📞 Teléfono: " + empresaTelefono + " | 📧 Email: " + emailInfo + "</p>" +
               "</div>" +
               "</body>" +
               "</html>";
    }
}