package com.dentalclinicsystem.model;

public class Servicio {
    private int id;
    private String nombre;
    private String categoria;
    private String subcategoria;
    private int duracionMinutos;
    private double precio;
    private double precioParticular;
    private double precioAseguradora;
    private String codigoProcedimiento;
    private int requiereConsultaPrevia;
    private String materialesNecesarios;
    private int estado;
    
    // Constructor vacío
    public Servicio() {
        this.estado = 1;
        this.duracionMinutos = 30;
    }
    
    // Constructor básico
    public Servicio(String nombre, String categoria, int duracionMinutos, double precio) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.duracionMinutos = duracionMinutos;
        this.precio = precio;
        this.estado = 1;
    }
    
    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    
    public String getSubcategoria() { return subcategoria; }
    public void setSubcategoria(String subcategoria) { this.subcategoria = subcategoria; }
    
    public int getDuracionMinutos() { return duracionMinutos; }
    public void setDuracionMinutos(int duracionMinutos) { this.duracionMinutos = duracionMinutos; }
    
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
    
    public double getPrecioParticular() { return precioParticular; }
    public void setPrecioParticular(double precioParticular) { this.precioParticular = precioParticular; }
    
    public double getPrecioAseguradora() { return precioAseguradora; }
    public void setPrecioAseguradora(double precioAseguradora) { this.precioAseguradora = precioAseguradora; }
    
    public String getCodigoProcedimiento() { return codigoProcedimiento; }
    public void setCodigoProcedimiento(String codigoProcedimiento) { this.codigoProcedimiento = codigoProcedimiento; }
    
    public int getRequiereConsultaPrevia() { return requiereConsultaPrevia; }
    public void setRequiereConsultaPrevia(int requiereConsultaPrevia) { this.requiereConsultaPrevia = requiereConsultaPrevia; }
    
    public String getMaterialesNecesarios() { return materialesNecesarios; }
    public void setMaterialesNecesarios(String materialesNecesarios) { this.materialesNecesarios = materialesNecesarios; }
    
    public int getEstado() { return estado; }
    public void setEstado(int estado) { this.estado = estado; }
    
    // ===== MÉTODOS ÚTILES =====
    
    /**
     * Obtiene el texto del estado (Activo/Inactivo)
     */
    public String getEstadoTexto() {
        return estado == 1 ? "Activo" : "Inactivo";
    }
    
    /**
     * Obtiene la duración formateada (ej: 30 min, 1h 30min)
     */
    public String getDuracionTexto() {
        if (duracionMinutos < 60) {
            return duracionMinutos + " min";
        } else {
            int horas = duracionMinutos / 60;
            int minutos = duracionMinutos % 60;
            if (minutos == 0) {
                return horas + " h";
            } else {
                return horas + "h " + minutos + "min";
            }
        }
    }
    
    /**
     * Obtiene el precio formateado (ej: $50.00)
     */
    public String getPrecioFormateado() {
        return String.format("$%.2f", precio);
    }
    
    /**
     * Obtiene el precio particular formateado
     */
    public String getPrecioParticularFormateado() {
        if (precioParticular > 0) {
            return String.format("$%.2f", precioParticular);
        }
        return "N/A";
    }
    
    /**
     * Obtiene el precio aseguradora formateado
     */
    public String getPrecioAseguradoraFormateado() {
        if (precioAseguradora > 0) {
            return String.format("$%.2f", precioAseguradora);
        }
        return "N/A";
    }
    
    /**
     * Obtiene el texto de requiere consulta previa
     */
    public String getRequiereConsultaPreviaTexto() {
        return requiereConsultaPrevia == 1 ? "Sí" : "No";
    }
    
    @Override
    public String toString() {
        return nombre + " (" + getDuracionTexto() + ") - " + getPrecioFormateado();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Servicio servicio = (Servicio) obj;
        return id == servicio.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}