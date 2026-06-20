package com.dentalclinicsystem.model;

/**
 * Modelo de Paciente
 */
public class Paciente {
    private int id;
    private String nombre;
    private String apellido;
    private String fechaNacimiento;
    private int edad;
    private String genero;
    private String tipoDocumento;
    private String numeroDocumento;
    private String telefono;
    private String telefonoAlternativo;
    private String email;
    private String direccion;
    private String ocupacion;
    private String estadoCivil;
    private int estado;
    private String fechaRegistro;
    private String foto;
    private String alergias;
    private String enfermedadesSistema;
    private String medicamentos;
    private String contactoEmergenciaNombre;
    private String contactoEmergenciaTelefono;
    
    public Paciente() {}
    
    // Constructor básico
    public Paciente(String nombre, String apellido, String numeroDocumento, String telefono) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.numeroDocumento = numeroDocumento;
        this.telefono = telefono;
        this.estado = 1;
    }
    
    // Método para obtener nombre completo
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }
    
    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    
    public String getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(String fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    
    public int getEdad() { return edad; }
    public void setEdad(int edad) { this.edad = edad; }
    
    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }
    
    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }
    
    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }
    
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    
    public String getTelefonoAlternativo() { return telefonoAlternativo; }
    public void setTelefonoAlternativo(String telefonoAlternativo) { this.telefonoAlternativo = telefonoAlternativo; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    
    public String getOcupacion() { return ocupacion; }
    public void setOcupacion(String ocupacion) { this.ocupacion = ocupacion; }
    
    public String getEstadoCivil() { return estadoCivil; }
    public void setEstadoCivil(String estadoCivil) { this.estadoCivil = estadoCivil; }
    
    public int getEstado() { return estado; }
    public void setEstado(int estado) { this.estado = estado; }
    
    public String getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(String fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    
    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }
    
    public String getAlergias() { return alergias; }
    public void setAlergias(String alergias) { this.alergias = alergias; }
    
    public String getEnfermedadesSistema() { return enfermedadesSistema; }
    public void setEnfermedadesSistema(String enfermedadesSistema) { this.enfermedadesSistema = enfermedadesSistema; }
    
    public String getMedicamentos() { return medicamentos; }
    public void setMedicamentos(String medicamentos) { this.medicamentos = medicamentos; }
    
    public String getContactoEmergenciaNombre() { return contactoEmergenciaNombre; }
    public void setContactoEmergenciaNombre(String contactoEmergenciaNombre) { this.contactoEmergenciaNombre = contactoEmergenciaNombre; }
    
    public String getContactoEmergenciaTelefono() { return contactoEmergenciaTelefono; }
    public void setContactoEmergenciaTelefono(String contactoEmergenciaTelefono) { this.contactoEmergenciaTelefono = contactoEmergenciaTelefono; }
    
    @Override
    public String toString() {
        return getNombreCompleto() + " - " + numeroDocumento;
    }
    
    /**
 * Obtiene la ruta completa de la foto del paciente
 */
public String getRutaFoto() {
    if (foto == null || foto.isEmpty()) {
        return null;
    }
    return "uploads/pacientes/" + foto;
}

/**
 * Obtiene el nombre del archivo de la foto
 */
public String getNombreFoto() {
    if (foto == null || foto.isEmpty()) {
        return "default-avatar.png";
    }
    return foto;
}

/**
 * Verifica si el paciente tiene foto
 */
public boolean tieneFoto() {
    return foto != null && !foto.isEmpty();
}
}