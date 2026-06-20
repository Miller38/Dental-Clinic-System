package com.dentalclinicsystem.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Movimiento {
    private int id;
    private int insumoId;
    private String tipo; // ENTRADA, SALIDA, AJUSTE, VENCIDO, DEVOLUCION
    private int cantidad;
    private String motivo;
    private String usuario;
    private String fecha;
    
    // Constantes de tipos
    public static final String TIPO_ENTRADA = "ENTRADA";
    public static final String TIPO_SALIDA = "SALIDA";
    public static final String TIPO_AJUSTE = "AJUSTE";
    public static final String TIPO_VENCIDO = "VENCIDO";
    public static final String TIPO_DEVOLUCION = "DEVOLUCION";
    
    // Constructor vacío
    public Movimiento() {
        this.fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }
    
    // Constructor básico
    public Movimiento(int insumoId, String tipo, int cantidad, String motivo) {
        this.insumoId = insumoId;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.motivo = motivo;
        this.fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }
    
    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getInsumoId() { return insumoId; }
    public void setInsumoId(int insumoId) { this.insumoId = insumoId; }
    
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    
    // Métodos útiles
    public String getTipoTexto() {
        switch (tipo) {
            case TIPO_ENTRADA: return "📥 Entrada";
            case TIPO_SALIDA: return "📤 Salida";
            case TIPO_AJUSTE: return "⚙️ Ajuste";
            case TIPO_VENCIDO: return "⏰ Vencido";
            case TIPO_DEVOLUCION: return "🔄 Devolución";
            default: return tipo;
        }
    }
    
    public String getTipoColor() {
        switch (tipo) {
            case TIPO_ENTRADA: return "#4BC878";
            case TIPO_SALIDA: return "#FF6464";
            case TIPO_AJUSTE: return "#FFAA32";
            case TIPO_VENCIDO: return "#FF6B6B";
            case TIPO_DEVOLUCION: return "#64B4FF";
            default: return "#888888";
        }
    }
    
    @Override
    public String toString() {
        return getTipoTexto() + " - " + cantidad + " unidades - " + fecha;
    }
}