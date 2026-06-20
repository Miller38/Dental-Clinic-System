package com.dentalclinicsystem.model;

public class Insumo {
    private int id;
    private String nombre;
    private String codigo;
    private String categoria;
    private String tipoInsumo; // CONSUMIBLE, NO_CONSUMIBLE, CADUCO, MEDICAMENTO
    private String presentacion; // Unidad, Caja, Kit, etc.
    private int stock;
    private int stockMinimo;
    private int stockMaximo;
    private String ubicacion;
    private double precioCompra;
    private double precioVenta;
    private String fechaCompra;
    private String fechaVencimiento;
    private String lote;
    private String proveedor;
    private int estado; // 1: Activo, 0: Inactivo
    
    // Constructor vacío
    public Insumo() {
        this.estado = 1;
        this.stock = 0;
        this.stockMinimo = 5;
    }
    
    // Constructor básico
    public Insumo(String nombre, String codigo, String categoria, int stock, double precioVenta) {
        this.nombre = nombre;
        this.codigo = codigo;
        this.categoria = categoria;
        this.stock = stock;
        this.precioVenta = precioVenta;
        this.estado = 1;
        this.stockMinimo = 5;
    }
    
    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    
    public String getTipoInsumo() { return tipoInsumo; }
    public void setTipoInsumo(String tipoInsumo) { this.tipoInsumo = tipoInsumo; }
    
    public String getPresentacion() { return presentacion; }
    public void setPresentacion(String presentacion) { this.presentacion = presentacion; }
    
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    
    public int getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(int stockMinimo) { this.stockMinimo = stockMinimo; }
    
    public int getStockMaximo() { return stockMaximo; }
    public void setStockMaximo(int stockMaximo) { this.stockMaximo = stockMaximo; }
    
    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    
    public double getPrecioCompra() { return precioCompra; }
    public void setPrecioCompra(double precioCompra) { this.precioCompra = precioCompra; }
    
    public double getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(double precioVenta) { this.precioVenta = precioVenta; }
    
    public String getFechaCompra() { return fechaCompra; }
    public void setFechaCompra(String fechaCompra) { this.fechaCompra = fechaCompra; }
    
    public String getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(String fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
    
    public String getLote() { return lote; }
    public void setLote(String lote) { this.lote = lote; }
    
    public String getProveedor() { return proveedor; }
    public void setProveedor(String proveedor) { this.proveedor = proveedor; }
    
    public int getEstado() { return estado; }
    public void setEstado(int estado) { this.estado = estado; }
    
    // Métodos útiles
    public String getEstadoTexto() {
        return estado == 1 ? "Activo" : "Inactivo";
    }
    
    public String getStockTexto() {
        if (stock <= 0) return "Sin stock";
        if (stock <= stockMinimo) return "⚠️ Stock bajo: " + stock;
        return "Stock: " + stock;
    }
    
    public boolean isStockBajo() {
        return stock <= stockMinimo;
    }
    
    public boolean isSinStock() {
        return stock <= 0;
    }
    
    public String getPrecioCompraFormateado() {
        return String.format("$%.2f", precioCompra);
    }
    
    public String getPrecioVentaFormateado() {
        return String.format("$%.2f", precioVenta);
    }
    
    @Override
    public String toString() {
        return nombre + " (Cód: " + codigo + ") - Stock: " + stock;
    }
}