package com.dentalclinicsystem.controller;

import com.dentalclinicsystem.dao.CitaDAO;
import com.dentalclinicsystem.dao.PacienteDAO;
import com.dentalclinicsystem.model.Cita;
import com.dentalclinicsystem.model.Paciente;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;

public class NotificacionController {
    
    private CitaDAO citaDAO;
    private PacienteDAO pacienteDAO;
    
    //  CREDENCIALES - CAMBIAR AQUÍ
    private static final String EMAIL_USER = "millergutierrez38@gmail.com";
    private static final String EMAIL_PASSWORD = "ykjx shpv fxgt oabs"; // Tu contraseña
    
    public NotificacionController() {
        this.citaDAO = new CitaDAO();
        this.pacienteDAO = new PacienteDAO();
    }
    
    /**
     * Envía recordatorios a todos los pacientes con citas mañana
     */
    public void enviarRecordatoriosManana(JFrame parent) {
        // Confirmar
        int confirm = JOptionPane.showConfirmDialog(parent,
                "📧 ¿Enviar recordatorios a los pacientes con citas para mañana?",
                "Confirmar envío",
                JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        // Obtener citas de mañana
        String manana = LocalDate.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
        List<Cita> citas = citaDAO.obtenerPorFecha(manana);
        
        if (citas.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "No hay citas para mañana");
            return;
        }
        
        int enviados = 0;
        int sinEmail = 0;
        
        for (Cita c : citas) {
            // Solo citas programadas o confirmadas
            if (!c.getEstado().equals("PROGRAMADA") && !c.getEstado().equals("CONFIRMADA")) {
                continue;
            }
            
            // Obtener email del paciente
            Paciente p = pacienteDAO.obtenerPorId(c.getPacienteId());
            if (p != null && p.getEmail() != null && !p.getEmail().isEmpty()) {
                enviarEmail(p.getEmail(), 
                    "🔔 Recordatorio de cita - Dental Clinic",
                    generarMensaje(c, p)
                );
                enviados++;
            } else {
                sinEmail++;
            }
        }
        
        JOptionPane.showMessageDialog(parent, 
            "Enviados: " + enviados + "\n️ Sin email: " + sinEmail);
    }
    
    /**
     * Envía un email simple
     */
    private void enviarEmail(String destino, String asunto, String mensaje) {
        if (destino == null || destino.isEmpty()) return;
        
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_USER, EMAIL_PASSWORD);
            }
        });
        
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_USER));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destino));
            message.setSubject(asunto);
            message.setContent(mensaje, "text/html; charset=utf-8");
            Transport.send(message);
            System.out.println("Email enviado a: " + destino);
        } catch (MessagingException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
    
    /**
     * Genera mensaje HTML sencillo
     */
    private String generarMensaje(Cita c, Paciente p) {
        return "<html>" +
               "<body style='font-family: Arial, sans-serif;'>" +
               "<h2>🦷 Recordatorio de Cita</h2>" +
               "<p>Hola <strong>" + p.getNombreCompleto() + "</strong>,</p>" +
               "<p>Te recordamos tu cita para <strong>mañana</strong>:</p>" +
               "<ul>" +
               "  <li><strong>Fecha:</strong> " + c.getFecha() + "</li>" +
               "  <li><strong>Hora:</strong> " + c.getHora() + "</li>" +
               "  <li><strong>Servicio:</strong> " + c.getServicioNombre() + "</li>" +
               "</ul>" +
               "<p>¡Te esperamos!</p>" +
               "<hr>" +
               "<p style='font-size: 12px; color: gray;'>Dental Clinic - " + LocalDate.now().getYear() + "</p>" +
               "</body>" +
               "</html>";
    }
}