package com.dentalclinicsystem.model;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Venta {
    private int id;
    private int pacienteId;
    private String usuario;
    private int odontologoId;
    private String tipoComprobante; // FACTURA, BOLETA, NOTA_CREDITO
    private String serie;
    private String numeroComprobante;
    private double subtotal;
    private double impuesto;
    private double descuento;
    private double total;
    private String metodoPago; // EFECTIVO, TARJETA, TRANSFERENCIA, SEGURO
    private String seguroNombre;
    private double seguroCobertura;
    private String estado; // ACTIVA, PAGADA, CANCELADA, ANULADA
    private String fecha;
    private String notas;
    
    // Campos adicionales para UI
    private String pacienteNombre;
    private String odontologoNombre;
    private List<DetalleVenta> detalles;
    
    // Constantes
    public static final String TIPO_FACTURA = "FACTURA";
    public static final String TIPO_BOLETA = "BOLETA";
    public static final String TIPO_NOTA_CREDITO = "NOTA_CREDITO";
    
    public static final String METODO_EFECTIVO = "EFECTIVO";
    public static final String METODO_TARJETA = "TARJETA";
    public static final String METODO_TRANSFERENCIA = "TRANSFERENCIA";
    public static final String METODO_SEGURO = "SEGURO";
    
    public static final String ESTADO_ACTIVA = "ACTIVA";
    public static final String ESTADO_PAGADA = "PAGADA";
    public static final String ESTADO_CANCELADA = "CANCELADA";
    public static final String ESTADO_ANULADA = "ANULADA";
    
    // Constructor vacío
    public Venta() {
        
         this.detalles = new ArrayList<>();
        // 🔥 GUARDAR SOLO LA FECHA SIN HORA
        this.fecha = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        this.estado = ESTADO_ACTIVA;
        this.impuesto = 0.0;
        this.descuento = 0.0;
    }
    
    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getPacienteId() { return pacienteId; }
    public void setPacienteId(int pacienteId) { this.pacienteId = pacienteId; }
    
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    
    public int getOdontologoId() { return odontologoId; }
    public void setOdontologoId(int odontologoId) { this.odontologoId = odontologoId; }
    
    public String getTipoComprobante() { return tipoComprobante; }
    public void setTipoComprobante(String tipoComprobante) { this.tipoComprobante = tipoComprobante; }
    
    public String getSerie() { return serie; }
    public void setSerie(String serie) { this.serie = serie; }
    
    public String getNumeroComprobante() { return numeroComprobante; }
    public void setNumeroComprobante(String numeroComprobante) { this.numeroComprobante = numeroComprobante; }
    
    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
    
    public double getImpuesto() { return impuesto; }
    public void setImpuesto(double impuesto) { this.impuesto = impuesto; }
    
    public double getDescuento() { return descuento; }
    public void setDescuento(double descuento) { this.descuento = descuento; }
    
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    
    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
    
    public String getSeguroNombre() { return seguroNombre; }
    public void setSeguroNombre(String seguroNombre) { this.seguroNombre = seguroNombre; }
    
    public double getSeguroCobertura() { return seguroCobertura; }
    public void setSeguroCobertura(double seguroCobertura) { this.seguroCobertura = seguroCobertura; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    
    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
    
    public String getPacienteNombre() { return pacienteNombre; }
    public void setPacienteNombre(String pacienteNombre) { this.pacienteNombre = pacienteNombre; }
    
    public String getOdontologoNombre() { return odontologoNombre; }
    public void setOdontologoNombre(String odontologoNombre) { this.odontologoNombre = odontologoNombre; }
    
    public List<DetalleVenta> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleVenta> detalles) { this.detalles = detalles; }
    
    // Métodos útiles
    public void agregarDetalle(DetalleVenta detalle) {
        this.detalles.add(detalle);
        recalcularTotales();
    }
    
  public void recalcularTotales() {
    this.subtotal = 0;
    for (DetalleVenta d : detalles) {
        this.subtotal += d.getSubtotal();
    }
    // 🔥 CORREGIDO: Calcular IVA correctamente
    this.impuesto = this.subtotal * 0.19; // 19% IVA
    this.total = this.subtotal + this.impuesto - this.descuento;
}
    
   public String getTotalFormateado() {
    return String.format("$%.2f", total);
}

public String getSubtotalFormateado() {
    return String.format("$%.2f", subtotal);
}   
    
    public String getEstadoTexto() {
        switch (estado) {
            case ESTADO_ACTIVA: return "Activa";
            case ESTADO_PAGADA: return "Pagada";
            case ESTADO_CANCELADA: return "Cancelada";
            case ESTADO_ANULADA: return "Anulada";
            default: return estado;
        }
    }
    
    public String getMetodoPagoTexto() {
        switch (metodoPago) {
            case METODO_EFECTIVO: return "Efectivo";
            case METODO_TARJETA: return "Tarjeta";
            case METODO_TRANSFERENCIA: return "Transferencia";
            case METODO_SEGURO: return "Seguro";
            default: return metodoPago;
        }
    }
    
    public String getTipoComprobanteTexto() {
        switch (tipoComprobante) {
            case TIPO_FACTURA: return "Factura";
            case TIPO_BOLETA: return "Boleta";
            case TIPO_NOTA_CREDITO: return "Nota Crédito";
            default: return tipoComprobante;
        }
    }
    
    @Override
    public String toString() {
        return "Venta #" + id + " - " + pacienteNombre + " - " + getTotalFormateado();
    }
}