package com.dentalclinicsystem.model;

public class Usuario {
    private int id;
    private String nombre;
    private String usuario;
    private String password;
    private String rol; // ADMIN, DENTISTA, ASISTENTE, RECEPCIONISTA
    private String especialidad;
    private String numeroLicencia;
    private String email;
    private String telefono;
    private String foto;
    private int estado; // 1: Activo, 0: Inactivo
    private int intentos;
    private int bloqueado; // 1: Bloqueado, 0: No bloqueado
    private String bloqueadoHasta;
    private String ultimoAcceso;
    private String fechaCreacion;
    
    // Constantes de roles
    public static final String ROL_ADMIN = "ADMIN";
    public static final String ROL_DENTISTA = "DENTISTA";
    public static final String ROL_ASISTENTE = "ASISTENTE";
    public static final String ROL_RECEPCIONISTA = "RECEPCIONISTA";
    
    // Constructor vacío
    public Usuario() {
        this.estado = 1;
        this.intentos = 0;
        this.bloqueado = 0;
    }
    
    // Constructor básico
    public Usuario(String nombre, String usuario, String password, String rol) {
        this();
        this.nombre = nombre;
        this.usuario = usuario;
        this.password = password;
        this.rol = rol;
    }
    
    // Constructor completo
    public Usuario(String nombre, String usuario, String password, String rol, 
                   String especialidad, String numeroLicencia, String email, String telefono) {
        this(nombre, usuario, password, rol);
        this.especialidad = especialidad;
        this.numeroLicencia = numeroLicencia;
        this.email = email;
        this.telefono = telefono;
    }
    
    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    
    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }
    
    public String getNumeroLicencia() { return numeroLicencia; }
    public void setNumeroLicencia(String numeroLicencia) { this.numeroLicencia = numeroLicencia; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    
    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }
    
    public int getEstado() { return estado; }
    public void setEstado(int estado) { this.estado = estado; }
    
    public int getIntentos() { return intentos; }
    public void setIntentos(int intentos) { this.intentos = intentos; }
    
    public int getBloqueado() { return bloqueado; }
    public void setBloqueado(int bloqueado) { this.bloqueado = bloqueado; }
    
    public String getBloqueadoHasta() { return bloqueadoHasta; }
    public void setBloqueadoHasta(String bloqueadoHasta) { this.bloqueadoHasta = bloqueadoHasta; }
    
    public String getUltimoAcceso() { return ultimoAcceso; }
    public void setUltimoAcceso(String ultimoAcceso) { this.ultimoAcceso = ultimoAcceso; }
    
    public String getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(String fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    
    // Métodos útiles
    public String getEstadoTexto() {
        return estado == 1 ? "Activo" : "Inactivo";
    }
    
    public String getRolTexto() {
        switch (rol) {
            case ROL_ADMIN: return "Administrador";
            case ROL_DENTISTA: return "Odontólogo";
            case ROL_ASISTENTE: return "Asistente";
            case ROL_RECEPCIONISTA: return "Recepcionista";
            default: return rol;
        }
    }
    
    public String getBloqueadoTexto() {
        return bloqueado == 1 ? "🔒 Bloqueado" : "✅ Activo";
    }
    
    public boolean isAdmin() {
        return ROL_ADMIN.equals(rol);
    }
    
    public boolean isDentista() {
        return ROL_DENTISTA.equals(rol);
    }
    
    @Override
    public String toString() {
        return nombre + " (" + usuario + ") - " + getRolTexto();
    }
}