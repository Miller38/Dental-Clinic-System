package com.dentalclinicsystem.service;

import com.dentalclinicsystem.dao.CitaDAO;
import com.dentalclinicsystem.dao.PacienteDAO;
import com.dentalclinicsystem.model.Cita;
import com.dentalclinicsystem.model.Paciente;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RecordatorioService {
    
    private CitaDAO citaDAO;
    private PacienteDAO pacienteDAO;
    private EmailService emailService;
    private static RecordatorioService instance;
    private ScheduledExecutorService scheduler;
    private boolean ejecutando = false;
    
    // Constantes de configuración
    private static final int HORAS_ANTICIPACION_24H = 24;
    private static final int HORAS_ANTICIPACION_2H = 2;
    private static final int MARGEN_MINUTOS = 30; // Margen de ±30 minutos para envíos
    
    private RecordatorioService() {
        this.citaDAO = new CitaDAO();
        this.pacienteDAO = new PacienteDAO();
        this.emailService = EmailService.getInstance();
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }
    
    public static RecordatorioService getInstance() {
        if (instance == null) {
            instance = new RecordatorioService();
        }
        return instance;
    }
    
    /**
     * Inicia el servicio de recordatorios automáticos
     */
    public void iniciarServicio() {
        if (ejecutando) {
            System.out.println("⚠️ El servicio de recordatorios ya está ejecutándose");
            return;
        }
        
        ejecutando = true;
        System.out.println("🚀 Iniciando servicio de recordatorios automáticos...");
        
        // Programar ejecución cada 30 minutos para mayor precisión
        scheduler.scheduleAtFixedRate(() -> {
            try {
                System.out.println("⏰ Ejecutando verificación de recordatorios...");
                procesarRecordatorios();
            } catch (Exception e) {
                System.err.println("❌ Error en servicio de recordatorios: " + e.getMessage());
                e.printStackTrace();
            }
        }, 0, 30, TimeUnit.MINUTES);
        
        System.out.println("✅ Servicio de recordatorios iniciado (se ejecuta cada 30 minutos)");
    }
    
    /**
     * Procesa todos los recordatorios pendientes
     */
    private void procesarRecordatorios() {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDate hoy = ahora.toLocalDate();
        LocalDate manana = hoy.plusDays(1);
        LocalDate pasadoManana = hoy.plusDays(2);
        
        System.out.println("🕐 Procesando recordatorios - Hora actual: " + ahora.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        
        // 1. Recordatorios de 24 horas (citas para mañana)
        procesarRecordatorios24Horas(manana, ahora);
        
        // 2. Recordatorios de 2 horas (citas para hoy)
        procesarRecordatorios2Horas(hoy, ahora);
        
        // 3. Verificar citas que se agendaron con poca anticipación
        procesarRecordatoriosUrgentes(hoy, ahora);
    }
    
    /**
     * Procesa recordatorios de 24 horas (citas para mañana)
     * 🔥 CORREGIDO: Calcula exactamente cuándo debe enviarse
     */
    private void procesarRecordatorios24Horas(LocalDate fechaManana, LocalDateTime ahora) {
        try {
            String fechaStr = fechaManana.toString();
            System.out.println("📅 Buscando citas para mañana: " + fechaStr);
            
            List<Cita> citas = citaDAO.obtenerPorFecha(fechaStr);
            
            int enviados = 0;
            int yaEnviados = 0;
            
            for (Cita cita : citas) {
                // Solo enviar si está PROGRAMADA o CONFIRMADA
                if (cita.getEstado().equals(Cita.ESTADO_PROGRAMADA) || 
                    cita.getEstado().equals(Cita.ESTADO_CONFIRMADA)) {
                    
                    // Verificar si ya se envió recordatorio de 24h
                    if (cita.getRecordatorioEnviado() == 0) {
                        // 🔥 CALCULAR SI DEBE ENVIARSE AHORA
                        if (debeEnviarRecordatorio24h(cita, ahora)) {
                            boolean enviado = enviarRecordatorio24Horas(cita);
                            if (enviado) {
                                citaDAO.marcarRecordatorioEnviado(cita.getId());
                                enviados++;
                            }
                        }
                    } else {
                        yaEnviados++;
                    }
                }
            }
            
            if (enviados > 0) {
                System.out.println("📧 Recordatorios 24h enviados: " + enviados);
            }
            if (yaEnviados > 0) {
                System.out.println("📧 Recordatorios 24h ya enviados: " + yaEnviados);
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error procesando recordatorios 24h: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Procesa recordatorios de 2 horas (citas para hoy)
     */
    private void procesarRecordatorios2Horas(LocalDate fechaHoy, LocalDateTime ahora) {
        try {
            String fechaStr = fechaHoy.toString();
            System.out.println("⏰ Buscando citas para hoy que requieren recordatorio 2h");
            
            List<Cita> citas = citaDAO.obtenerPorFecha(fechaStr);
            
            int enviados = 0;
            
            for (Cita cita : citas) {
                // Solo enviar si está PROGRAMADA o CONFIRMADA
                if (cita.getEstado().equals(Cita.ESTADO_PROGRAMADA) || 
                    cita.getEstado().equals(Cita.ESTADO_CONFIRMADA)) {
                    
                    // Verificar si ya se envió recordatorio de 2h
                    if (cita.getRecordatorioEnviado() == 0) {
                        // 🔥 CALCULAR SI DEBE ENVIARSE AHORA
                        if (debeEnviarRecordatorio2h(cita, ahora)) {
                            boolean enviado = enviarRecordatorio2Horas(cita);
                            if (enviado) {
                                citaDAO.marcarRecordatorioEnviado(cita.getId());
                                enviados++;
                            }
                        }
                    }
                }
            }
            
            if (enviados > 0) {
                System.out.println("📧 Recordatorios 2h enviados: " + enviados);
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error procesando recordatorios 2h: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Procesa recordatorios urgentes para citas agendadas con poca anticipación
     */
    private void procesarRecordatoriosUrgentes(LocalDate fechaHoy, LocalDateTime ahora) {
        try {
            String fechaStr = fechaHoy.toString();
            List<Cita> citas = citaDAO.obtenerPorFecha(fechaStr);
            
            for (Cita cita : citas) {
                if (cita.getEstado().equals(Cita.ESTADO_PROGRAMADA) || 
                    cita.getEstado().equals(Cita.ESTADO_CONFIRMADA)) {
                    
                    // Si ya tiene recordatorio enviado, saltar
                    if (cita.getRecordatorioEnviado() > 0) {
                        continue;
                    }
                    
                    LocalDateTime fechaCita = LocalDateTime.of(
                        LocalDate.parse(cita.getFecha()),
                        LocalTime.parse(cita.getHora())
                    );
                    
                    // 🔥 Calcular tiempo restante
                    long minutosRestantes = java.time.Duration.between(ahora, fechaCita).toMinutes();
                    
                    // Si la cita es en menos de 2 horas y no se ha enviado recordatorio
                    if (minutosRestantes > 0 && minutosRestantes <= 120) {
                        System.out.println("⚠️ Cita urgente detectada: " + cita.getId() + 
                                         " | Faltan " + minutosRestantes + " minutos");
                        
                        // Enviar recordatorio urgente
                        boolean enviado = enviarRecordatorioUrgente(cita, (int) minutosRestantes);
                        if (enviado) {
                            citaDAO.marcarRecordatorioEnviado(cita.getId());
                            System.out.println("✅ Recordatorio urgente enviado para cita ID: " + cita.getId());
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error procesando recordatorios urgentes: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 🔥 NUEVO: Determina si debe enviarse el recordatorio de 24h
     * Calcula exactamente cuándo debe enviarse basado en la hora de la cita
     */
    private boolean debeEnviarRecordatorio24h(Cita cita, LocalDateTime ahora) {
        try {
            // Obtener fecha y hora de la cita
            LocalDate fechaCita = LocalDate.parse(cita.getFecha());
            LocalTime horaCita = LocalTime.parse(cita.getHora());
            LocalDateTime fechaHoraCita = LocalDateTime.of(fechaCita, horaCita);
            
            // Calcular cuándo debería enviarse el recordatorio (24 horas antes)
            LocalDateTime momentoEnvio = fechaHoraCita.minusHours(HORAS_ANTICIPACION_24H);
            
            // Calcular diferencia en minutos
            long minutosDiff = java.time.Duration.between(ahora, momentoEnvio).toMinutes();
            
            // Si estamos dentro del margen de ±30 minutos del momento exacto
            boolean debeEnviar = Math.abs(minutosDiff) <= MARGEN_MINUTOS;
            
            if (debeEnviar) {
                System.out.println("✅ Recordatorio 24h debe enviarse AHORA para cita: " + 
                                 cita.getId() + " | Hora cita: " + cita.getHora() + 
                                 " | Momento envío: " + momentoEnvio.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            }
            
            return debeEnviar;
            
        } catch (Exception e) {
            System.err.println("❌ Error calculando recordatorio 24h: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 🔥 NUEVO: Determina si debe enviarse el recordatorio de 2h
     * Calcula exactamente cuándo debe enviarse basado en la hora de la cita
     */
    private boolean debeEnviarRecordatorio2h(Cita cita, LocalDateTime ahora) {
        try {
            // Obtener fecha y hora de la cita
            LocalDate fechaCita = LocalDate.parse(cita.getFecha());
            LocalTime horaCita = LocalTime.parse(cita.getHora());
            LocalDateTime fechaHoraCita = LocalDateTime.of(fechaCita, horaCita);
            
            // Calcular cuándo debería enviarse el recordatorio (2 horas antes)
            LocalDateTime momentoEnvio = fechaHoraCita.minusHours(HORAS_ANTICIPACION_2H);
            
            // Calcular diferencia en minutos
            long minutosDiff = java.time.Duration.between(ahora, momentoEnvio).toMinutes();
            
            // Si estamos dentro del margen de ±30 minutos del momento exacto
            boolean debeEnviar = Math.abs(minutosDiff) <= MARGEN_MINUTOS;
            
            if (debeEnviar) {
                System.out.println("✅ Recordatorio 2h debe enviarse AHORA para cita: " + 
                                 cita.getId() + " | Hora cita: " + cita.getHora() + 
                                 " | Momento envío: " + momentoEnvio.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            }
            
            return debeEnviar;
            
        } catch (Exception e) {
            System.err.println("❌ Error calculando recordatorio 2h: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Envía recordatorio urgente cuando la cita se agendó con poca anticipación
     */
    private boolean enviarRecordatorioUrgente(Cita cita, int minutosRestantes) {
        try {
            Paciente paciente = pacienteDAO.obtenerPorId(cita.getPacienteId());
            if (paciente == null || paciente.getEmail() == null || paciente.getEmail().isEmpty()) {
                return false;
            }
            
            String asunto = "🔔 RECORDATORIO URGENTE - Cita en " + minutosRestantes + " minutos";
            String mensaje = construirMensajeUrgente(cita, paciente, minutosRestantes);
            
            return emailService.enviarEmail(paciente.getEmail(), asunto, mensaje);
            
        } catch (Exception e) {
            System.err.println("❌ Error enviando recordatorio urgente: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Construye mensaje HTML para recordatorio urgente
     */
    private String construirMensajeUrgente(Cita cita, Paciente paciente, int minutosRestantes) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>");
        sb.append("<html>");
        sb.append("<head><meta charset='UTF-8'></head>");
        sb.append("<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;'>");
        sb.append("<div style='max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 10px; padding: 30px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);'>");
        sb.append("<div style='text-align: center; border-bottom: 2px solid #FF6464; padding-bottom: 20px;'>");
        sb.append("<h1 style='color: #2c3e50;'>🦷 Clínica Dental</h1>");
        sb.append("<h2 style='color: #FF6464;'>⚠️ RECORDATORIO URGENTE</h2>");
        sb.append("</div>");
        sb.append("<div style='padding: 20px 0;'>");
        sb.append("<p style='color: #2c3e50; font-size: 16px;'>Estimado/a <strong>" + paciente.getNombreCompleto() + "</strong>,</p>");
        sb.append("<p style='color: #FF6464; font-size: 18px; font-weight: bold;'>¡Su cita es en " + minutosRestantes + " minutos!</p>");
        sb.append("<div style='background-color: #fff3cd; border-left: 4px solid #FF6464; padding: 15px; margin: 20px 0;'>");
        sb.append("<p style='margin: 5px 0;'><strong>📅 Fecha:</strong> " + cita.getFecha() + "</p>");
        sb.append("<p style='margin: 5px 0;'><strong>⏰ Hora:</strong> " + cita.getHora() + "</p>");
        sb.append("<p style='margin: 5px 0;'><strong>🏥 Servicio:</strong> " + cita.getServicioNombre() + "</p>");
        sb.append("<p style='margin: 5px 0;'><strong>👨‍⚕️ Odontólogo:</strong> " + cita.getOdontologoNombre() + "</p>");
        sb.append("</div>");
        sb.append("<p style='color: #7f8c8d; font-size: 14px;'>📍 Dirección: Av. Principal #123, Quito - Ecuador</p>");
        sb.append("<p style='color: #7f8c8d; font-size: 14px;'>📞 Teléfono: (02) 123-4567</p>");
        sb.append("</div>");
        sb.append("<div style='text-align: center; border-top: 1px solid #ecf0f1; padding-top: 20px;'>");
        sb.append("<p style='color: #95a5a6; font-size: 12px;'>Este es un mensaje automático, por favor no responder a este correo.</p>");
        sb.append("</div>");
        sb.append("</div>");
        sb.append("</body>");
        sb.append("</html>");
        return sb.toString();
    }
    
    // ========== MÉTODOS EXISTENTES (con mejoras) ==========
    
    private boolean enviarRecordatorio24Horas(Cita cita) {
        try {
            Paciente paciente = pacienteDAO.obtenerPorId(cita.getPacienteId());
            if (paciente == null || paciente.getEmail() == null || paciente.getEmail().isEmpty()) {
                return false;
            }
            
            String asunto = "🔔 Recordatorio de Cita - Clínica Dental (24 horas)";
            String mensaje = construirMensajeRecordatorio24h(cita, paciente);
            
            return emailService.enviarEmail(paciente.getEmail(), asunto, mensaje);
            
        } catch (Exception e) {
            System.err.println("❌ Error enviando recordatorio 24h: " + e.getMessage());
            return false;
        }
    }
    
    private boolean enviarRecordatorio2Horas(Cita cita) {
        try {
            Paciente paciente = pacienteDAO.obtenerPorId(cita.getPacienteId());
            if (paciente == null || paciente.getEmail() == null || paciente.getEmail().isEmpty()) {
                return false;
            }
            
            String asunto = "🔔 Recordatorio de Cita - Clínica Dental (2 horas)";
            String mensaje = construirMensajeRecordatorio2h(cita, paciente);
            
            return emailService.enviarEmail(paciente.getEmail(), asunto, mensaje);
            
        } catch (Exception e) {
            System.err.println("❌ Error enviando recordatorio 2h: " + e.getMessage());
            return false;
        }
    }
    
    private String construirMensajeRecordatorio24h(Cita cita, Paciente paciente) {
        // ... (código existente)
        return "";
    }
    
    private String construirMensajeRecordatorio2h(Cita cita, Paciente paciente) {
        // ... (código existente)
        return "";
    }
    
    /**
     * Procesa recordatorios manualmente para una fecha específica
     */
    public void procesarRecordatoriosManuales(String fecha) {
        try {
            LocalDate fechaCita = LocalDate.parse(fecha);
            LocalDateTime ahora = LocalDateTime.now();
            
            // Procesar 24h para la fecha especificada
            procesarRecordatorios24Horas(fechaCita, ahora);
            
            // Si es hoy, procesar 2h
            if (fechaCita.equals(LocalDate.now())) {
                procesarRecordatorios2Horas(fechaCita, ahora);
                procesarRecordatoriosUrgentes(fechaCita, ahora);
            }
            
            System.out.println("✅ Recordatorios manuales procesados para: " + fecha);
            
        } catch (Exception e) {
            System.err.println("❌ Error procesando recordatorios manuales: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void detenerServicio() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            ejecutando = false;
            System.out.println("🛑 Servicio de recordatorios detenido");
        }
    }
}