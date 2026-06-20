package com.dentalclinicsystem.controller;

import com.dentalclinicsystem.dao.AuditoriaDAO;
import com.dentalclinicsystem.dao.PacienteDAO;
import com.dentalclinicsystem.dao.Session;
import com.dentalclinicsystem.dao.VentaDAO;
import com.dentalclinicsystem.model.DetalleVenta;
import com.dentalclinicsystem.model.Paciente;
import com.dentalclinicsystem.model.Usuario;
import com.dentalclinicsystem.model.Venta;
import com.dentalclinicsystem.service.EmailService;
import com.dentalclinicsystem.service.FacturaService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class VentaController {
    private VentaDAO ventaDAO;
    private PacienteDAO pacienteDAO;
    private AuditoriaDAO auditoriaDAO;
    private EmailService emailService;
    private FacturaService facturaService;

    public VentaController() {
        this.ventaDAO = new VentaDAO();
        this.pacienteDAO = new PacienteDAO();
        this.auditoriaDAO = new AuditoriaDAO();
        this.emailService = EmailService.getInstance();
        this.facturaService = new FacturaService();
    }

    // ========== VALIDACIONES ==========
    public String validarVenta(Venta venta) {
        if (venta == null) {
            return "Datos de venta inválidos";
        }

        if (venta.getPacienteId() <= 0) {
            return "Seleccione un paciente";
        }

        if (venta.getDetalles() == null || venta.getDetalles().isEmpty()) {
            return "Agregue al menos un detalle a la venta";
        }

        if (venta.getTotal() <= 0) {
            return "El total debe ser mayor a 0";
        }

        if (venta.getMetodoPago() == null || venta.getMetodoPago().isEmpty()) {
            return "Seleccione un método de pago";
        }

        return null;
    }

    // ========== GUARDAR VENTA ==========
    
    /**
     * Guarda una venta (con envío de factura por defecto)
     */
    public boolean guardarVenta(Venta venta) {
        return guardarVenta(venta, true);
    }

    /**
     * Guarda una venta con opción de enviar factura por email
     */
   public boolean guardarVenta(Venta venta, boolean enviarFactura) {
    if (venta == null) {
        return false;
    }

    String error = validarVenta(venta);
    if (error != null) {
        JOptionPane.showMessageDialog(null, error, "Error de validación", JOptionPane.WARNING_MESSAGE);
        return false;
    }

    String usuarioActual = "SISTEMA";
    if (Session.getInstance().isSesionActiva()) {
        Usuario usuario = Session.getUsuarioActual();
        if (usuario != null) {
            usuarioActual = usuario.getUsuario();
        }
    }
    venta.setUsuario(usuarioActual);

    // 🔥 RECALCULAR TOTALES CON IVA
    venta.recalcularTotales();

    boolean exito = ventaDAO.insertar(venta);
    if (exito) {
        auditoriaDAO.registrar("INSERT", "ventas", venta.getId());
        
        // 🔥 ENVIAR FACTURA POR EMAIL
        if (enviarFactura) {
            enviarFacturaEmail(venta);
        }
        
        JOptionPane.showMessageDialog(null, "✅ Venta registrada correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }
    return exito;
}

    /**
     * Envía la factura por email al paciente
     */
    private void enviarFacturaEmail(Venta venta) {
        try {
            Paciente paciente = pacienteDAO.obtenerPorId(venta.getPacienteId());
            if (paciente == null) {
                System.err.println("❌ Paciente no encontrado para enviar factura");
                return;
            }

            if (paciente.getEmail() == null || paciente.getEmail().isEmpty()) {
                System.err.println("❌ El paciente no tiene email: " + paciente.getNombreCompleto());
                return;
            }

            // Generar factura HTML
            String facturaHTML = facturaService.generarFacturaHTML(venta, paciente);
            
            // Enviar email
            boolean enviado = emailService.enviarFactura(venta, paciente, facturaHTML);
            
            if (enviado) {
                System.out.println("✅ Factura enviada a: " + paciente.getEmail());
            } else {
                System.err.println("❌ Error al enviar factura a: " + paciente.getEmail());
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error al enviar factura: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Reenvía una factura manualmente
     */
    public boolean reenviarFactura(int ventaId) {
        Venta venta = ventaDAO.obtenerPorId(ventaId);
        if (venta == null) {
            JOptionPane.showMessageDialog(null, "Venta no encontrada", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        Paciente paciente = pacienteDAO.obtenerPorId(venta.getPacienteId());
        if (paciente == null) {
            JOptionPane.showMessageDialog(null, "Paciente no encontrado", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (paciente.getEmail() == null || paciente.getEmail().isEmpty()) {
            JOptionPane.showMessageDialog(null, 
                "El paciente no tiene email registrado", 
                "Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        String facturaHTML = facturaService.generarFacturaHTML(venta, paciente);
        boolean enviado = emailService.enviarFactura(venta, paciente, facturaHTML);

        if (enviado) {
            JOptionPane.showMessageDialog(null, 
                "✅ Factura reenviada a: " + paciente.getEmail(),
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, 
                "Error al reenviar factura",
                "Error", JOptionPane.ERROR_MESSAGE);
        }

        return enviado;
    }

    // ========== MÉTODOS EXISTENTES ==========
    
    public boolean anularVenta(int id) {
        int confirm = JOptionPane.showConfirmDialog(null, 
            "¿Está seguro de anular esta venta?", 
            "Confirmar anulación", 
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean exito = ventaDAO.actualizarEstado(id, Venta.ESTADO_ANULADA);
            if (exito) {
                auditoriaDAO.registrar("ANULAR", "ventas", id);
                JOptionPane.showMessageDialog(null, "✅ Venta anulada correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
            return exito;
        }
        return false;
    }

    public Venta buscarPorId(int id) {
        return ventaDAO.obtenerPorId(id);
    }

    public List<Venta> listarTodos() {
        return ventaDAO.obtenerTodos();
    }

    public List<Venta> listarPorPaciente(int pacienteId) {
        return ventaDAO.obtenerPorPaciente(pacienteId);
    }

    public List<Venta> listarPorRango(String fechaInicio, String fechaFin) {
        return ventaDAO.obtenerPorRango(fechaInicio, fechaFin);
    }

    // ========== REPORTES ==========
    public double obtenerTotalMes() {
        String inicio = LocalDate.now().withDayOfMonth(1).toString();
        String fin = LocalDate.now().toString();
        return ventaDAO.obtenerTotalPorRango(inicio, fin);
    }

    public double obtenerTotalHoy() {
        String hoy = LocalDate.now().toString();
        return ventaDAO.obtenerTotalPorRango(hoy, hoy);
    }

    public int contarVentasHoy() {
        String hoy = LocalDate.now().toString();
        return ventaDAO.contarVentasPorRango(hoy, hoy);
    }

    public String[] getTiposComprobante() {
        return new String[]{Venta.TIPO_FACTURA, Venta.TIPO_BOLETA, Venta.TIPO_NOTA_CREDITO};
    }

    public String[] getMetodosPago() {
        return new String[]{Venta.METODO_EFECTIVO, Venta.METODO_TARJETA, 
                           Venta.METODO_TRANSFERENCIA, Venta.METODO_SEGURO};
    }

    public String[] getEstados() {
        return new String[]{Venta.ESTADO_ACTIVA, Venta.ESTADO_PAGADA, 
                           Venta.ESTADO_CANCELADA, Venta.ESTADO_ANULADA};
    }

    // ========== TABLA ==========
    public void cargarTabla(DefaultTableModel model, List<Venta> ventas) {
        model.setRowCount(0);
        
        if (ventas == null || ventas.isEmpty()) {
            return;
        }

        for (Venta v : ventas) {
            model.addRow(new Object[]{
                v.getId(),
                v.getFecha() != null ? v.getFecha().substring(0, 10) : "",
                v.getPacienteNombre() != null ? v.getPacienteNombre() : "N/A",
                v.getTipoComprobanteTexto(),
                v.getTotalFormateado(),
                v.getMetodoPagoTexto(),
                v.getEstadoTexto()
            });
        }
    }

    public void cargarDetalles(DefaultTableModel model, List<DetalleVenta> detalles) {
        model.setRowCount(0);
        
        if (detalles == null || detalles.isEmpty()) {
            return;
        }

        for (DetalleVenta d : detalles) {
            model.addRow(new Object[]{
                d.getNombre(),
                d.getCantidad(),
                d.getPrecioUnitarioFormateado(),
                d.getSubtotalFormateado()
            });
        }
    }

    public String getFechaActual() {
        return LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public String getFechaHoraActual() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}