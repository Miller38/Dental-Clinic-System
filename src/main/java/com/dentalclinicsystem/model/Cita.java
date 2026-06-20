package com.dentalclinicsystem.model;

public class Cita {
    private int id;
    private int pacienteId;
    private int odontologoId;
    private int servicioId;
    private int tratamientoId;
    private String fecha;
    private String hora;
    private int duracion;
    private String estado;
    private String nota;
    private int recordatorioEnviado;
    private String fechaCreacion;
    private String modificadaPor;
    // Agregar estos campos
private int recordatorio24hEnviado;
private int recordatorio2hEnviado;
    
    // Campos adicionales para mostrar en UI (no se guardan en BD)
    private String pacienteNombre;
    private String odontologoNombre;
    private String servicioNombre;
    
    // Estados disponibles
    public static final String ESTADO_PROGRAMADA = "PROGRAMADA";
    public static final String ESTADO_CONFIRMADA = "CONFIRMADA";
    public static final String ESTADO_EN_PROCESO = "EN_PROCESO";
    public static final String ESTADO_COMPLETADA = "COMPLETADA";
    public static final String ESTADO_CANCELADA = "CANCELADA";
    public static final String ESTADO_NO_ASISTIO = "NO_ASISTIO";
    
    // Constructor vacío
    public Cita() {
        this.duracion = 30;
        this.estado = ESTADO_PROGRAMADA;
        this.recordatorioEnviado = 0;
    }
    
    // Constructor con campos obligatorios
    public Cita(int pacienteId, int odontologoId, int servicioId, String fecha, String hora) {
        this();
        this.pacienteId = pacienteId;
        this.odontologoId = odontologoId;
        this.servicioId = servicioId;
        this.fecha = fecha;
        this.hora = hora;
    }
    
    // Constructor completo
    public Cita(int id, int pacienteId, int odontologoId, int servicioId, 
                String fecha, String hora, int duracion, String estado, 
                String nota, int recordatorioEnviado) {
        this.id = id;
        this.pacienteId = pacienteId;
        this.odontologoId = odontologoId;
        this.servicioId = servicioId;
        this.fecha = fecha;
        this.hora = hora;
        this.duracion = duracion;
        this.estado = estado;
        this.nota = nota;
        this.recordatorioEnviado = recordatorioEnviado;
    }
    
    // ========== GETTERS Y SETTERS ==========
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getPacienteId() { return pacienteId; }
    public void setPacienteId(int pacienteId) { this.pacienteId = pacienteId; }
    
    public int getOdontologoId() { return odontologoId; }
    public void setOdontologoId(int odontologoId) { this.odontologoId = odontologoId; }
    
    public int getServicioId() { return servicioId; }
    public void setServicioId(int servicioId) { this.servicioId = servicioId; }
    
    public int getTratamientoId() { return tratamientoId; }
    public void setTratamientoId(int tratamientoId) { this.tratamientoId = tratamientoId; }
    
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    
    public String getHora() { return hora; }
    public void setHora(String hora) { this.hora = hora; }
    
    public int getDuracion() { return duracion; }
    public void setDuracion(int duracion) { this.duracion = duracion; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    public String getNota() { return nota; }
    public void setNota(String nota) { this.nota = nota; }
    
    public int getRecordatorioEnviado() { return recordatorioEnviado; }
    public void setRecordatorioEnviado(int recordatorioEnviado) { this.recordatorioEnviado = recordatorioEnviado; }
    
    public String getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(String fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    
    public String getModificadaPor() { return modificadaPor; }
    public void setModificadaPor(String modificadaPor) { this.modificadaPor = modificadaPor; }
    
    // ========== CAMPOS PARA UI ==========
    
    public String getPacienteNombre() { return pacienteNombre; }
    public void setPacienteNombre(String pacienteNombre) { this.pacienteNombre = pacienteNombre; }
    
    public String getOdontologoNombre() { return odontologoNombre; }
    public void setOdontologoNombre(String odontologoNombre) { this.odontologoNombre = odontologoNombre; }
    
    public String getServicioNombre() { return servicioNombre; }
    public void setServicioNombre(String servicioNombre) { this.servicioNombre = servicioNombre; }
    
    // Getters y Setters
public int getRecordatorio24hEnviado() { return recordatorio24hEnviado; }
public void setRecordatorio24hEnviado(int recordatorio24hEnviado) { 
    this.recordatorio24hEnviado = recordatorio24hEnviado; 
}

public int getRecordatorio2hEnviado() { return recordatorio2hEnviado; }
public void setRecordatorio2hEnviado(int recordatorio2hEnviado) { 
    this.recordatorio2hEnviado = recordatorio2hEnviado; 
}
    // ========== MÉTODOS ÚTILES ==========
    
    /**
     * Obtiene fecha y hora formateada
     */
    public String getFechaHora() {
        return fecha + " " + hora;
    }
    
    /**
     * Obtiene el color asociado al estado para UI
     */
    public String getEstadoColor() {
        switch (estado) {
            case ESTADO_PROGRAMADA: return "#64B4FF";
            case ESTADO_CONFIRMADA: return "#4BC878";
            case ESTADO_EN_PROCESO: return "#FFAA32";
            case ESTADO_COMPLETADA: return "#32A852";
            case ESTADO_CANCELADA: return "#FF6464";
            case ESTADO_NO_ASISTIO: return "#FF6B6B";
            default: return "#888888";
        }
    }
    
    /**
     * Obtiene el texto del estado en español
     */
    public String getEstadoTexto() {
        switch (estado) {
            case ESTADO_PROGRAMADA: return "Programada";
            case ESTADO_CONFIRMADA: return "Confirmada";
            case ESTADO_EN_PROCESO: return "En Proceso";
            case ESTADO_COMPLETADA: return "Completada";
            case ESTADO_CANCELADA: return "Cancelada";
            case ESTADO_NO_ASISTIO: return "No Asistió";
            default: return estado;
        }
    }
    
    /**
     * Verifica si la cita está activa (no cancelada ni completada)
     */
    public boolean isActiva() {
        return !estado.equals(ESTADO_CANCELADA) && !estado.equals(ESTADO_COMPLETADA);
    }
    
    /**
     * Verifica si la cita es hoy
     */
    public boolean isHoy() {
        if (fecha == null) return false;
        return fecha.equals(java.time.LocalDate.now().toString());
    }
    
    /**
     * Verifica si la cita es futura
     */
    public boolean isFutura() {
        if (fecha == null) return false;
        try {
            java.time.LocalDate fechaCita = java.time.LocalDate.parse(fecha);
            return fechaCita.isAfter(java.time.LocalDate.now());
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Obtiene el estado para mostrar en un combo box
     */
    public static String[] getEstados() {
        return new String[]{
            ESTADO_PROGRAMADA,
            ESTADO_CONFIRMADA,
            ESTADO_EN_PROCESO,
            ESTADO_COMPLETADA,
            ESTADO_CANCELADA,
            ESTADO_NO_ASISTIO
        };
    }
    
    /**
     * Obtiene los estados en español para mostrar
     */
    public static String[] getEstadosTexto() {
        return new String[]{
            "Programada",
            "Confirmada",
            "En Proceso",
            "Completada",
            "Cancelada",
            "No Asistió"
        };
    }
    
    /**
     * Convierte estado en texto a estado en código
     */
    public static String getEstadoFromTexto(String texto) {
        switch (texto) {
            case "Programada": return ESTADO_PROGRAMADA;
            case "Confirmada": return ESTADO_CONFIRMADA;
            case "En Proceso": return ESTADO_EN_PROCESO;
            case "Completada": return ESTADO_COMPLETADA;
            case "Cancelada": return ESTADO_CANCELADA;
            case "No Asistió": return ESTADO_NO_ASISTIO;
            default: return texto;
        }
    }
    
    @Override
    public String toString() {
        return (pacienteNombre != null ? pacienteNombre : "Paciente " + pacienteId) + 
               " - " + fecha + " " + hora + " (" + getEstadoTexto() + ")";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Cita cita = (Cita) obj;
        return id == cita.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}