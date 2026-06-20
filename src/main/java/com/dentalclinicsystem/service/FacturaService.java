package com.dentalclinicsystem.service;

import com.dentalclinicsystem.model.DetalleVenta;
import com.dentalclinicsystem.model.Paciente;
import com.dentalclinicsystem.model.Venta;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FacturaService {

    private ConfiguracionService configService;
    private EmailService emailService;
    
    public FacturaService() {
        this.configService = ConfiguracionService.getInstance();
        this.emailService = EmailService.getInstance();
    }

    public String generarFacturaHTML(Venta venta, Paciente paciente) {
        StringBuilder html = new StringBuilder();
        
        double ivaPorcentaje = configService.getIvaPorcentaje();
        double ivaPorcentajeNumerico = configService.getIvaPorcentajeNumerico();
        
        double subtotal = venta.getSubtotal();
        double ivaCalculado = subtotal * ivaPorcentaje;
        double totalCalculado = subtotal + ivaCalculado;
        
        String empresaNombre = configService.getEmpresaNombre();
        String empresaTelefono = configService.getEmpresaTelefono();
        String emailInfo = configService.getEmailInfo();
        
        html.append("<!DOCTYPE html>");
        html.append("<html>");
        html.append("<head>");
        html.append("<meta charset='UTF-8'>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px; }");
        html.append(".container { max-width: 700px; margin: auto; background-color: #ffffff; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }");
        html.append(".header { text-align: center; border-bottom: 2px solid #2c3e50; padding-bottom: 15px; margin-bottom: 20px; }");
        html.append(".header h1 { color: #2c3e50; margin: 0; font-size: 24px; }");
        html.append(".header p { color: #7f8c8d; margin: 5px 0; }");
        html.append(".header .titulo { font-size: 18px; font-weight: bold; color: #2c3e50; }");
        html.append(".info { display: flex; justify-content: space-between; margin-bottom: 15px; padding: 10px; background: #f8f9fa; border-radius: 5px; flex-wrap: wrap; }");
        html.append(".info-item { font-size: 13px; color: #555; padding: 3px 0; }");
        html.append(".info-item strong { color: #2c3e50; }");
        html.append("table { width: 100%; border-collapse: collapse; margin: 20px 0; }");
        html.append("th { background-color: #2c3e50; color: white; padding: 10px; text-align: left; }");
        html.append("td { padding: 10px; border-bottom: 1px solid #ddd; }");
        html.append("tr:hover { background-color: #f5f5f5; }");
        html.append(".totales { text-align: right; margin-top: 20px; padding-top: 10px; border-top: 2px solid #2c3e50; }");
        html.append(".totales p { margin: 5px 0; font-size: 14px; }");
        html.append(".totales .total { font-size: 20px; font-weight: bold; color: #27ae60; }");
        html.append(".footer { text-align: center; margin-top: 30px; padding-top: 15px; border-top: 1px solid #ddd; color: #95a5a6; font-size: 12px; }");
        html.append(".footer p { margin: 3px 0; }");
        html.append(".badge { display: inline-block; padding: 3px 10px; border-radius: 15px; font-size: 12px; font-weight: bold; }");
        html.append(".badge-pagada { background-color: #27ae60; color: white; }");
        html.append(".badge-pendiente { background-color: #f39c12; color: white; }");
        html.append(".badge-anulada { background-color: #e74c3c; color: white; }");
        html.append(".precio { text-align: right; }");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");
        html.append("<div class='container'>");
        
        // HEADER
        html.append("<div class='header'>");
        html.append("<h1>🦷 ").append(empresaNombre).append("</h1>");
        html.append("<p class='titulo'>").append(venta.getTipoComprobanteTexto()).append("</p>");
        html.append("<p><strong>N° Documento:</strong> ").append(venta.getNumeroComprobante() != null ? venta.getNumeroComprobante() : "S/N").append("</p>");
        
        String badgeClass = "badge-pendiente";
        if ("PAGADA".equals(venta.getEstado())) badgeClass = "badge-pagada";
        if ("ANULADA".equals(venta.getEstado())) badgeClass = "badge-anulada";
        html.append("<p><span class='badge ").append(badgeClass).append("'>").append(venta.getEstadoTexto()).append("</span></p>");
        html.append("</div>");
        
        // INFORMACIÓN
        html.append("<div class='info'>");
        html.append("<div class='info-item'><strong>📅 Fecha:</strong> ").append(venta.getFecha()).append("</div>");
        html.append("<div class='info-item'><strong>👤 Paciente:</strong> ").append(paciente.getNombreCompleto()).append("</div>");
        html.append("<div class='info-item'><strong>📱 Teléfono:</strong> ").append(paciente.getTelefono()).append("</div>");
        html.append("</div>");
        
        html.append("<div class='info'>");
        html.append("<div class='info-item'><strong>📧 Email:</strong> ").append(paciente.getEmail() != null ? paciente.getEmail() : "No registrado").append("</div>");
        html.append("<div class='info-item'><strong>💳 Pago:</strong> ").append(venta.getMetodoPagoTexto()).append("</div>");
        html.append("<div class='info-item'><strong>📋 Tipo:</strong> ").append(venta.getTipoComprobanteTexto()).append("</div>");
        html.append("</div>");
        
        // TABLA DE DETALLES
        html.append("<table>");
        html.append("<thead>");
        html.append("<tr>");
        html.append("<th style='width: 50%;'>Servicio</th>");
        html.append("<th style='width: 15%; text-align: center;'>Cantidad</th>");
        html.append("<th style='width: 17%; text-align: right;'>Precio</th>");
        html.append("<th style='width: 18%; text-align: right;'>Subtotal</th>");
        html.append("</tr>");
        html.append("</thead>");
        html.append("<tbody>");
        
        if (venta.getDetalles() != null && !venta.getDetalles().isEmpty()) {
            for (DetalleVenta detalle : venta.getDetalles()) {
                html.append("<tr>");
                html.append("<td>").append(detalle.getNombre()).append("</td>");
                html.append("<td style='text-align: center;'>").append(detalle.getCantidad()).append("</td>");
                html.append("<td style='text-align: right;'>").append(detalle.getPrecioUnitarioFormateado()).append("</td>");
                html.append("<td style='text-align: right;'>").append(detalle.getSubtotalFormateado()).append("</td>");
                html.append("</tr>");
            }
        } else {
            html.append("<tr><td colspan='4' style='text-align: center; color: #95a5a6;'>No hay detalles</td></tr>");
        }
        
        html.append("</tbody>");
        html.append("</table>");
        
        // TOTALES
        html.append("<div class='totales'>");
        html.append("<p><strong>Subtotal:</strong> ").append(String.format("$%.2f", subtotal)).append("</p>");
        html.append("<p><strong>IVA (").append(String.format("%.0f", ivaPorcentajeNumerico)).append("%):</strong> ").append(String.format("$%.2f", ivaCalculado)).append("</p>");
        if (venta.getDescuento() > 0) {
            html.append("<p><strong>Descuento:</strong> -$").append(String.format("%.2f", venta.getDescuento())).append("</p>");
        }
        html.append("<p class='total'><strong>TOTAL:</strong> ").append(String.format("$%.2f", totalCalculado)).append("</p>");
        html.append("</div>");
        
        // FOOTER
        html.append("<div class='footer'>");
        html.append("<p>Este documento es un comprobante de pago generado automáticamente.</p>");
        html.append("<p>© ").append(new SimpleDateFormat("yyyy").format(new Date())).append(" ").append(empresaNombre).append(" - Todos los derechos reservados</p>");
        html.append("<p>📞 Teléfono: ").append(empresaTelefono).append(" | 📧 Email: ").append(emailInfo).append("</p>");
        html.append("</div>");
        
        html.append("</div>");
        html.append("</body>");
        html.append("</html>");
        
        return html.toString();
    }
    
    public String generarFacturaTexto(Venta venta, Paciente paciente) {
        double ivaPorcentaje = configService.getIvaPorcentaje();
        double ivaPorcentajeNumerico = configService.getIvaPorcentajeNumerico();
        
        double subtotal = venta.getSubtotal();
        double ivaCalculado = subtotal * ivaPorcentaje;
        double totalCalculado = subtotal + ivaCalculado;
        
        String empresaNombre = configService.getEmpresaNombre();
        String empresaTelefono = configService.getEmpresaTelefono();
        String emailInfo = configService.getEmailInfo();
        
        StringBuilder sb = new StringBuilder();
        
        sb.append("============================================\n");
        sb.append("        ").append(empresaNombre).append("\n");
        sb.append("============================================\n\n");
        sb.append(venta.getTipoComprobanteTexto()).append("\n");
        sb.append("N°: ").append(venta.getNumeroComprobante() != null ? venta.getNumeroComprobante() : "S/N").append("\n");
        sb.append("Fecha: ").append(venta.getFecha()).append("\n");
        sb.append("Estado: ").append(venta.getEstadoTexto()).append("\n\n");
        sb.append("--------------------------------------------\n");
        sb.append("Paciente: ").append(paciente.getNombreCompleto()).append("\n");
        sb.append("Teléfono: ").append(paciente.getTelefono()).append("\n");
        sb.append("Email: ").append(paciente.getEmail() != null ? paciente.getEmail() : "No registrado").append("\n");
        sb.append("Método de pago: ").append(venta.getMetodoPagoTexto()).append("\n\n");
        sb.append("--------------------------------------------\n");
        sb.append("DETALLES:\n");
        sb.append("--------------------------------------------\n");
        
        if (venta.getDetalles() != null && !venta.getDetalles().isEmpty()) {
            for (DetalleVenta detalle : venta.getDetalles()) {
                sb.append(String.format("%-30s x%-3d %10s\n", 
                    detalle.getNombre(), 
                    detalle.getCantidad(), 
                    detalle.getSubtotalFormateado()));
            }
        }
        
        sb.append("--------------------------------------------\n");
        sb.append(String.format("%-30s %10s\n", "Subtotal:", String.format("$%.2f", subtotal)));
        sb.append(String.format("%-30s %10s\n", "IVA (" + String.format("%.0f", ivaPorcentajeNumerico) + "%):", String.format("$%.2f", ivaCalculado)));
        if (venta.getDescuento() > 0) {
            sb.append(String.format("%-30s %10s\n", "Descuento:", "-$" + String.format("%.2f", venta.getDescuento())));
        }
        sb.append("--------------------------------------------\n");
        sb.append(String.format("%-30s %10s\n", "TOTAL:", String.format("$%.2f", totalCalculado)));
        sb.append("============================================\n");
        sb.append("Gracias por su preferencia.\n");
        sb.append("Teléfono: ").append(empresaTelefono).append("\n");
        sb.append("Email: ").append(emailInfo).append("\n");
        sb.append("============================================\n");
        
        return sb.toString();
    }
    
    public boolean enviarFacturaPorEmail(Venta venta, Paciente paciente) {
        if (paciente.getEmail() == null || paciente.getEmail().isEmpty()) {
            System.err.println("❌ El paciente no tiene email: " + paciente.getNombreCompleto());
            return false;
        }
        
        String facturaHTML = generarFacturaHTML(venta, paciente);
        return emailService.enviarFactura(venta, paciente, facturaHTML);
    }
    
    public boolean enviarFacturaAdministrador(Venta venta, Paciente paciente) {
        String facturaHTML = generarFacturaHTML(venta, paciente);
        String asunto = "📋 Nueva Factura Generada - " + venta.getNumeroComprobante();
        
        return emailService.enviarEmail(
            configService.getEmailDestino(), 
            asunto, 
            facturaHTML
        );
    }
}