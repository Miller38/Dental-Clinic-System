package com.dentalclinicsystem.controller;

import com.dentalclinicsystem.dao.*;
import com.dentalclinicsystem.model.Cita;
import com.dentalclinicsystem.model.Paciente;
import com.dentalclinicsystem.model.Usuario;
//import com.dentalclinicsystem.model.Servicio;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class CitaController {
    private CitaDAO citaDAO;
    private PacienteDAO pacienteDAO;
    private UsuarioDAO usuarioDAO;
   // private ServicioDAO servicioDAO;
    private AuditoriaDAO auditoriaDAO;

    // ===== CONSTANTES DE CONFIGURACIÓN =====
    private static final String HORA_INICIO = "08:00";
    private static final String HORA_FIN = "18:00";
    private static final String HORA_ALMUERZO_INICIO = "12:00";
    private static final String HORA_ALMUERZO_FIN = "14:00";
    private static final int MAX_CITAS_POR_DIA = 10;
    private static final int MAX_CITAS_POR_PACIENTE_DIA = 2;
    private static final int ANTICIPACION_MINIMA_HORAS = 2;
    private static final int MAX_REAGENDAMIENTOS = 3;

    public CitaController() {
        this.citaDAO = new CitaDAO();
        this.pacienteDAO = new PacienteDAO();
        this.usuarioDAO = new UsuarioDAO();
        //this.servicioDAO = new ServicioDAO();
        this.auditoriaDAO = new AuditoriaDAO();
    }

    // ================================================================
    // ============== VALIDACIÓN COMPLETA DE CITA =====================
    // ================================================================

    public String validarCita(Cita cita) {
        if (cita == null) {
            return "Datos de cita inválidos";
        }

        // ===== 1. VALIDACIONES BÁSICAS =====
        if (cita.getPacienteId() <= 0) {
            return "Seleccione un paciente válido";
        }

        if (cita.getOdontologoId() <= 0) {
            return "Seleccione un odontólogo válido";
        }

        if (cita.getServicioId() <= 0) {
            return "Seleccione un servicio válido";
        }

        if (cita.getFecha() == null || cita.getFecha().isEmpty()) {
            return "Seleccione una fecha";
        }

        if (cita.getHora() == null || cita.getHora().isEmpty()) {
            return "Seleccione una hora";
        }

        // ===== 2. VALIDACIÓN DE FECHA =====
        LocalDate fechaCita;
        try {
            fechaCita = LocalDate.parse(cita.getFecha());
        } catch (Exception e) {
            return "Formato de fecha inválido. Use YYYY-MM-DD";
        }

        LocalDate hoy = LocalDate.now();
        
        // Solo para citas nuevas: validar que no sea en el pasado
        if (cita.getId() == 0 && fechaCita.isBefore(hoy)) {
            return "No se pueden agendar citas en fechas pasadas";
        }

        // ===== 3. VALIDACIÓN DE DÍA (Sábado, Domingo y Festivos) =====
        int diaSemana = fechaCita.getDayOfWeek().getValue(); // 1=Lunes, 7=Domingo
        
        // 3.1 No se trabaja los domingos
        if (diaSemana == 7) {
            return "❌ La clínica no atiende los domingos. Seleccione otro día";
        }

        // 3.2 Los sábados se trabaja medio día (8:00 - 12:00)
        if (diaSemana == 6) {
            String hora = cita.getHora();
            if (hora.compareTo("08:00") < 0 || hora.compareTo("12:00") >= 0) {
                return "❌ Los sábados el horario es de 8:00 AM a 12:00 PM (medio día)";
            }
        }

        // 3.3 Validar días festivos
        if (esDiaFestivo(fechaCita)) {
            return "🔴 Día festivo - La clínica está cerrada";
        }

        // ===== 4. VALIDACIÓN DE HORARIO LABORAL (Lunes a Viernes) =====
        if (diaSemana >= 1 && diaSemana <= 5) {
            // 4.1 Horario normal: 8:00 - 18:00
            if (cita.getHora().compareTo(HORA_INICIO) < 0 || cita.getHora().compareTo(HORA_FIN) >= 0) {
                return "❌ El horario de atención es de " + HORA_INICIO + " a " + HORA_FIN;
            }

            // 4.2 Validar hora de almuerzo (12:00 - 14:00)
            if (cita.getHora().compareTo(HORA_ALMUERZO_INICIO) >= 0 && 
                cita.getHora().compareTo(HORA_ALMUERZO_FIN) < 0) {
                return "❌ Horario de almuerzo de " + HORA_ALMUERZO_INICIO + 
                       " a " + HORA_ALMUERZO_FIN + ". Seleccione otra hora";
            }
        }

        // ===== 5. VALIDACIÓN DE ANTICIPACIÓN MÍNIMA (2 horas) =====
        // Solo aplicar para citas nuevas, no para ediciones
        if (cita.getId() == 0) {
            try {
                LocalTime horaCita = LocalTime.parse(cita.getHora());
                LocalDateTime fechaHoraCita = LocalDateTime.of(fechaCita, horaCita);
                LocalDateTime ahora = LocalDateTime.now();
                
                // Si la cita es hoy, validar anticipación
                if (fechaCita.isEqual(hoy)) {
                    if (fechaHoraCita.isBefore(ahora.plusHours(ANTICIPACION_MINIMA_HORAS))) {
                        return "⚠️ Las citas deben agendarse con al menos " + 
                               ANTICIPACION_MINIMA_HORAS + " horas de anticipación";
                    }
                }
            } catch (Exception e) {
                return "Formato de hora inválido";
            }
        }

        // ===== 6. VALIDACIÓN DE DURACIÓN =====
        if (cita.getDuracion() < 15 || cita.getDuracion() > 240) {
            return "La duración debe estar entre 15 y 240 minutos";
        }

        // ===== 7. VALIDACIÓN DEL PACIENTE =====
        Paciente paciente = pacienteDAO.obtenerPorId(cita.getPacienteId());
        if (paciente == null) {
            return "Paciente no encontrado";
        }
        if (paciente.getEstado() != 1) {
            return "El paciente está inactivo. No se pueden agendar citas";
        }

        // ===== 8. VALIDACIÓN DEL ODONTÓLOGO =====
        Usuario odontologo = usuarioDAO.obtenerPorId(cita.getOdontologoId());
        if (odontologo == null) {
            return "Odontólogo no encontrado";
        }
        if (odontologo.getEstado() != 1) {
            return "El odontólogo está inactivo";
        }
        if (odontologo.getBloqueado() == 1) {
            return "El odontólogo está bloqueado temporalmente";
        }

        // ===== 9. VALIDACIÓN DEL SERVICIO =====
//        Servicio servicio = servicioDAO.obtenerPorId(cita.getServicioId());
//        if (servicio == null) {
//            return "Servicio no encontrado";
//        }
//        if (servicio.getEstado() != 1) {
//            return "El servicio no está disponible";
//        }

        // ===== 10. CONFLICTO DE HORARIOS =====
        if (citaDAO.existeConflicto(cita.getOdontologoId(), cita.getFecha(), 
                                    cita.getHora(), cita.getDuracion(), cita.getId())) {
            return "❌ El odontólogo ya tiene una cita en ese horario";
        }

        // ===== 11. LÍMITE DE CITAS POR ODONTÓLOGO (10 por día) =====
        int citasOdontologoHoy = citaDAO.contarCitasPorOdontologoYFecha(
            cita.getOdontologoId(), cita.getFecha());
        if (citasOdontologoHoy >= MAX_CITAS_POR_DIA) {
            return "⚠️ El odontólogo ya tiene el máximo de citas para este día (" + 
                   MAX_CITAS_POR_DIA + "). Seleccione otro día";
        }

        // ===== 12. LÍMITE DE CITAS POR PACIENTE (2 por día) =====
        // Excluir la cita actual si es edición
        List<Cita> citasPaciente = citaDAO.obtenerPorPaciente(cita.getPacienteId());
        int citasPacienteHoy = 0;
        for (Cita c : citasPaciente) {
            if (c.getFecha().equals(cita.getFecha()) && 
                !c.getEstado().equals("CANCELADA") && 
                !c.getEstado().equals("COMPLETADA") &&
                c.getId() != cita.getId()) {
                citasPacienteHoy++;
            }
        }
        if (citasPacienteHoy >= MAX_CITAS_POR_PACIENTE_DIA) {
            return "⚠️ El paciente ya tiene " + MAX_CITAS_POR_PACIENTE_DIA + 
                   " citas para este día. Máximo permitido";
        }

        // ===== 13. VALIDAR REAGENDAMIENTOS (máximo 3) =====
        if (cita.getId() > 0) {
            int reagendamientos = contarReagendamientos(cita.getId());
            if (reagendamientos >= MAX_REAGENDAMIENTOS) {
                return "⚠️ Esta cita ya ha sido reagendada " + MAX_REAGENDAMIENTOS + 
                       " veces. No se permite más reagendamientos";
            }
        }

        return null; // Todo válido
    }

    // ================================================================
    // ============== MÉTODOS PARA VALIDAR FESTIVOS ===================
    // ================================================================

    /**
     * Verifica si una fecha es día festivo
     */
    private boolean esDiaFestivo(LocalDate fecha) {
        String mesDia = String.format("%02d-%02d", fecha.getMonthValue(), fecha.getDayOfMonth());
        
        // Festivos fijos de Ecuador
        String[] festivosFijos = {
            "01-01", // Año Nuevo
            "05-01", // Día del Trabajo
            "05-24", // Batalla de Pichincha
            "07-24", // Nacimiento de Simón Bolívar
            "08-10", // Primer Grito de Independencia
            "10-09", // Independencia de Guayaquil
            "11-02", // Día de los Difuntos
            "11-03", // Independencia de Cuenca
            "12-25"  // Navidad
        };
        
        for (String festivo : festivosFijos) {
            if (mesDia.equals(festivo)) {
                return true;
            }
        }

        // Festivos variables
        if (esCarnaval(fecha)) {
            return true;
        }

        if (esSemanaSanta(fecha)) {
            return true;
        }

        return false;
    }

    /**
     * Calcula si una fecha es Carnaval (Lunes y Martes)
     */
    private boolean esCarnaval(LocalDate fecha) {
        int año = fecha.getYear();
        LocalDate pascua = calcularPascua(año);
        LocalDate lunesCarnaval = pascua.minusDays(48);
        LocalDate martesCarnaval = pascua.minusDays(47);
        
        return fecha.equals(lunesCarnaval) || fecha.equals(martesCarnaval);
    }

    /**
     * Calcula si una fecha es Semana Santa (Jueves y Viernes Santo)
     */
    private boolean esSemanaSanta(LocalDate fecha) {
        int año = fecha.getYear();
        LocalDate pascua = calcularPascua(año);
        LocalDate juevesSanto = pascua.minusDays(3);
        LocalDate viernesSanto = pascua.minusDays(2);
        
        return fecha.equals(juevesSanto) || fecha.equals(viernesSanto);
    }

    /**
     * Calcula la fecha de Pascua (algoritmo de Gauss)
     */
    private LocalDate calcularPascua(int año) {
        int a = año % 19;
        int b = año % 4;
        int c = año % 7;
        int d = (19 * a + 24) % 30;
        int e = (2 * b + 4 * c + 6 * d + 5) % 7;
        int dia = 22 + d + e;
        
        if (dia <= 31) {
            return LocalDate.of(año, 3, dia);
        } else {
            return LocalDate.of(año, 4, dia - 31);
        }
    }

    // ================================================================
    // ============== MÉTODOS DE CONSULTA Y UTILIDADES ================
    // ================================================================

    /**
     * Cuenta cuántas veces se ha reagendado una cita
     */
    private int contarReagendamientos(int citaId) {
        // Se puede implementar con una tabla de historial de cambios
        return 0;
    }

    /**
     * Verifica si un paciente tiene deudas pendientes
     */
    private boolean tieneDeudaPendiente(int pacienteId) {
        // Se implementa con el módulo de finanzas
        return false;
    }

    /**
     * Obtiene las horas disponibles para un odontólogo en una fecha
     */
    public List<String> getHorasDisponibles(int odontologoId, String fecha, int duracion) {
        List<String> horasDisponibles = new ArrayList<>();
        
        LocalDate fechaCita;
        try {
            fechaCita = LocalDate.parse(fecha);
        } catch (Exception e) {
            return horasDisponibles;
        }

        int diaSemana = fechaCita.getDayOfWeek().getValue();

        // SÁBADOS: Solo 8:00 AM - 12:00 PM
        if (diaSemana == 6) {
            String[] horasSabado = {
                "08:00", "08:30", "09:00", "09:30", 
                "10:00", "10:30", "11:00", "11:30"
            };
            return filtrarHorasDisponibles(odontologoId, fecha, duracion, horasSabado);
        }

        // DOMINGOS: Sin atención
        if (diaSemana == 7) {
            return horasDisponibles;
        }

        // DÍAS FESTIVOS: Sin atención
        if (esDiaFestivo(fechaCita)) {
            return horasDisponibles;
        }

        // LUNES A VIERNES: Horario completo con almuerzo
        String[] horasLaborales = {
            "08:00", "08:30", "09:00", "09:30", "10:00", "10:30",
            "11:00", "11:30", 
            // 12:00 - 14:00 ALMUERZO (excluido)
            "14:00", "14:30", "15:00", "15:30", "16:00", "16:30",
            "17:00", "17:30"
        };

        return filtrarHorasDisponibles(odontologoId, fecha, duracion, horasLaborales);
    }

    /**
     * Filtra las horas disponibles excluyendo las ocupadas
     */
    private List<String> filtrarHorasDisponibles(int odontologoId, String fecha, 
                                                  int duracion, String[] horasBase) {
        List<String> horasDisponibles = new ArrayList<>();
        
        if (odontologoId <= 0 || fecha == null || fecha.isEmpty()) {
            for (String hora : horasBase) {
                horasDisponibles.add(hora);
            }
            return horasDisponibles;
        }

        List<Cita> citasExistentes = citaDAO.obtenerPorOdontologo(odontologoId, fecha);

        for (String hora : horasBase) {
            boolean ocupada = false;
            for (Cita cita : citasExistentes) {
                String horaCita = cita.getHora();
                int duracionCita = cita.getDuracion();
                String horaFinCita = calcularHoraFin(horaCita, duracionCita);

                if (hora.compareTo(horaCita) >= 0 && hora.compareTo(horaFinCita) < 0) {
                    ocupada = true;
                    break;
                }
                if (horaCita.compareTo(hora) >= 0 && 
                    horaCita.compareTo(calcularHoraFin(hora, duracion)) < 0) {
                    ocupada = true;
                    break;
                }
            }
            if (!ocupada) {
                horasDisponibles.add(hora);
            }
        }
        return horasDisponibles;
    }

    /**
     * Calcula la hora de fin sumando la duración
     */
    public String calcularHoraFin(String horaInicio, int duracion) {
        try {
            String[] partes = horaInicio.split(":");
            int horas = Integer.parseInt(partes[0]);
            int minutos = Integer.parseInt(partes[1]);
            minutos += duracion;
            while (minutos >= 60) {
                minutos -= 60;
                horas++;
            }
            if (horas >= 24) horas -= 24;
            return String.format("%02d:%02d", horas, minutos);
        } catch (Exception e) {
            return horaInicio;
        }
    }

    /**
     * Verifica si una fecha tiene atención (no es sábado tarde, domingo o festivo)
     */
    public boolean tieneAtencion(String fecha) {
        try {
            LocalDate fechaCita = LocalDate.parse(fecha);
            int diaSemana = fechaCita.getDayOfWeek().getValue();
            
            if (diaSemana == 7) {
                return false;
            }
            
            if (esDiaFestivo(fechaCita)) {
                return false;
            }
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Obtiene el mensaje de horario según el día
     */
    public String getMensajeHorario(String fecha) {
        try {
            LocalDate fechaCita = LocalDate.parse(fecha);
            int diaSemana = fechaCita.getDayOfWeek().getValue();
            
            if (diaSemana == 7) {
                return "🔴 Los domingos la clínica está cerrada";
            }
            
            if (diaSemana == 6) {
                return "🟡 Los sábados atención de 8:00 AM a 12:00 PM (medio día)";
            }
            
            if (esDiaFestivo(fechaCita)) {
                return "🔴 Día festivo - La clínica está cerrada";
            }
            
            return "🟢 Atención de " + HORA_INICIO + " a " + HORA_FIN + 
                   " (Almuerzo de " + HORA_ALMUERZO_INICIO + " a " + HORA_ALMUERZO_FIN + ")";
            
        } catch (Exception e) {
            return "📌 Seleccione una fecha válida";
        }
    }

    /**
     * Verifica si un odontólogo tiene disponibilidad en un horario
     */
    public boolean tieneDisponibilidad(int odontologoId, String fecha, String hora, int duracion) {
        return !citaDAO.existeConflicto(odontologoId, fecha, hora, duracion, 0);
    }

    // ================================================================
    // ============== MÉTODOS CRUD ====================================
    // ================================================================

    public boolean guardarCita(Cita cita) {
        if (cita == null) {
            return false;
        }

        String error = validarCita(cita);
        if (error != null) {
            JOptionPane.showMessageDialog(null, "❌ " + error, "Error de validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        String usuarioActual = "SISTEMA";
        if (Session.getInstance().isSesionActiva()) {
            Usuario usuario = Session.getUsuarioActual();
            if (usuario != null) {
                usuarioActual = usuario.getUsuario();
            }
        }
        cita.setModificadaPor(usuarioActual);

        boolean exito;
        if (cita.getId() > 0) {
            exito = citaDAO.actualizar(cita);
            if (exito) {
                auditoriaDAO.registrar("UPDATE", "citas", cita.getId());
                JOptionPane.showMessageDialog(null, "✅ Cita actualizada correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            exito = citaDAO.insertar(cita);
            if (exito) {
                auditoriaDAO.registrar("INSERT", "citas", cita.getId());
                JOptionPane.showMessageDialog(null, "✅ Cita registrada correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
        }

        return exito;
    }

    public boolean eliminarCita(int id) {
        int confirm = JOptionPane.showConfirmDialog(null, 
            "¿Está seguro de eliminar esta cita?", 
            "Confirmar eliminación", 
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean exito = citaDAO.eliminar(id);
            if (exito) {
                auditoriaDAO.registrar("DELETE", "citas", id);
                JOptionPane.showMessageDialog(null, "Cita eliminada correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
            return exito;
        }
        return false;
    }

    public boolean cambiarEstadoCita(int id, String nuevoEstado) {
        Cita cita = citaDAO.obtenerPorId(id);
        if (cita == null) {
            JOptionPane.showMessageDialog(null, "Cita no encontrada", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        String estadoActual = cita.getEstado();
        
        // No permitir cambios desde estados finales
        if (estadoActual.equals("COMPLETADA") || estadoActual.equals("CANCELADA")) {
            JOptionPane.showMessageDialog(null, 
                "No se puede cambiar el estado de una cita " + estadoActual.toLowerCase(),
                "Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Si la cita ya pasó y no está completada
        try {
            LocalDate fechaCita = LocalDate.parse(cita.getFecha());
            if (fechaCita.isBefore(LocalDate.now()) && !estadoActual.equals("COMPLETADA")) {
                if (!nuevoEstado.equals("CANCELADA") && !nuevoEstado.equals("NO_ASISTIO")) {
                    JOptionPane.showMessageDialog(null, 
                        "⚠️ La cita ya pasó. Solo puede marcarla como CANCELADA o NO_ASISTIO",
                        "Error", JOptionPane.WARNING_MESSAGE);
                    return false;
                }
            }
        } catch (Exception e) {
            // Ignorar
        }

        boolean exito = citaDAO.actualizarEstado(id, nuevoEstado);
        if (exito) {
            auditoriaDAO.registrar("ESTADO_CAMBIO", "citas", id);
            JOptionPane.showMessageDialog(null, 
                "✅ Cita actualizada a: " + nuevoEstado, 
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }
        return exito;
    }

    // ================================================================
    // ============== MÉTODOS DE CONSULTA =============================
    // ================================================================

    public Cita buscarPorId(int id) {
        return citaDAO.obtenerPorId(id);
    }

    public List<Cita> listarPorPaciente(int pacienteId) {
        return citaDAO.obtenerPorPaciente(pacienteId);
    }

    public List<Cita> listarPorOdontologo(int odontologoId, String fecha) {
        return citaDAO.obtenerPorOdontologo(odontologoId, fecha);
    }

    public List<Cita> listarPorFecha(String fecha) {
        return citaDAO.obtenerPorFecha(fecha);
    }

    public List<Cita> listarPorRango(String fechaInicio, String fechaFin) {
        return citaDAO.obtenerPorRango(fechaInicio, fechaFin);
    }

    public List<Cita> listarProximas(int limite) {
        return citaDAO.obtenerProximas(limite);
    }

    public int contarCitasHoy() {
        return citaDAO.contarCitasHoy();
    }

    public int contarCitasPorEstado(String estado) {
        return citaDAO.contarCitasPorEstado(estado);
    }

    public void cargarTabla(DefaultTableModel model, List<Cita> citas) {
        model.setRowCount(0);
        if (citas == null || citas.isEmpty()) {
            return;
        }
        for (Cita c : citas) {
            model.addRow(new Object[]{
                c.getId(),
                c.getFecha(),
                c.getHora(),
                c.getPacienteNombre() != null ? c.getPacienteNombre() : "N/A",
                c.getOdontologoNombre() != null ? c.getOdontologoNombre() : "N/A",
                c.getServicioNombre() != null ? c.getServicioNombre() : "N/A",
                c.getEstado(),
                c.getNota() != null ? c.getNota() : ""
            });
        }
    }

    public String getFechaActual() {
        return LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public String[] getEstadosCita() {
        return new String[]{"PROGRAMADA", "CONFIRMADA", "EN_PROCESO", "COMPLETADA", "CANCELADA", "NO_ASISTIO"};
    }

    public String getColorEstado(String estado) {
        switch (estado) {
            case "PROGRAMADA": return "#64B4FF";
            case "CONFIRMADA": return "#4BC878";
            case "EN_PROCESO": return "#FFAA32";
            case "COMPLETADA": return "#32A852";
            case "CANCELADA": return "#FF6464";
            case "NO_ASISTIO": return "#FF6B6B";
            default: return "#888888";
        }
    }

    public CitaResumen getResumenCitas() {
        CitaResumen resumen = new CitaResumen();
        resumen.totalHoy = contarCitasHoy();
        resumen.programadas = contarCitasPorEstado("PROGRAMADA");
        resumen.confirmadas = contarCitasPorEstado("CONFIRMADA");
        resumen.enProceso = contarCitasPorEstado("EN_PROCESO");
        resumen.completadas = contarCitasPorEstado("COMPLETADA");
        resumen.canceladas = contarCitasPorEstado("CANCELADA");
        resumen.noAsistio = contarCitasPorEstado("NO_ASISTIO");
        resumen.totalPendientes = resumen.programadas + resumen.confirmadas + resumen.enProceso;
        return resumen;
    }

    // ================================================================
    // ============== CLASE INTERNA PARA RESUMEN ======================
    // ================================================================

    public static class CitaResumen {
        public int totalHoy;
        public int programadas;
        public int confirmadas;
        public int enProceso;
        public int completadas;
        public int canceladas;
        public int noAsistio;
        public int totalPendientes;
        
        @Override
        public String toString() {
            return "Citas hoy: " + totalHoy + 
                   " | Pendientes: " + totalPendientes +
                   " | Completadas: " + completadas;
        }
    }
}