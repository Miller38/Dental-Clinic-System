package com.dentalclinicsystem.model;

public class DetalleVenta {
    private int id;
    private int ventaId;
    private String tipoItem; // SERVICIO, INSUMO, TRATAMIENTO
    private int itemId;
    private String nombre;
    private int cantidad;
    private double precioUnitario;
    private double subtotal;
    
    // Constructor vacío
    public DetalleVenta() {
        this.cantidad = 1;
        this.tipoItem = "SERVICIO";
    }
    
    // Constructor completo
    public DetalleVenta(String tipoItem, int itemId, String nombre, int cantidad, double precioUnitario) {
        this.tipoItem = tipoItem;
        this.itemId = itemId;
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = cantidad * precioUnitario;
    }
    
    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getVentaId() { return ventaId; }
    public void setVentaId(int ventaId) { this.ventaId = ventaId; }
    
    public String getTipoItem() { return tipoItem; }
    public void setTipoItem(String tipoItem) { this.tipoItem = tipoItem; }
    
    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    
    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }
    
    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
    
    // Métodos útiles
    public String getPrecioUnitarioFormateado() {
        return String.format("$%.2f", precioUnitario);
    }
    
    public String getSubtotalFormateado() {
        return String.format("$%.2f", subtotal);
    }
    
    public void recalcularSubtotal() {
        this.subtotal = cantidad * precioUnitario;
    }
    
    @Override
    public String toString() {
        return nombre + " x" + cantidad + " = " + getSubtotalFormateado();
    }
}