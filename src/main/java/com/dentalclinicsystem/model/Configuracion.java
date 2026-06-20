package com.dentalclinicsystem.model;

public class Configuracion {
    private int id;
    private String clave;
    private String valor;
    private String descripcion;
    private String categoria;
    
    public Configuracion() {}
    
    public Configuracion(String clave, String valor) {
        this.clave = clave;
        this.valor = valor;
    }
    
    public Configuracion(String clave, String valor, String descripcion, String categoria) {
        this.clave = clave;
        this.valor = valor;
        this.descripcion = descripcion;
        this.categoria = categoria;
    }
    
    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }
    
    public String getValor() { return valor; }
    public void setValor(String valor) { this.valor = valor; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    
    @Override
    public String toString() {
        return clave + " = " + valor;
    }
}