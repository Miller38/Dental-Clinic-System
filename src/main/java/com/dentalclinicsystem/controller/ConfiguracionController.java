package com.dentalclinicsystem.controller;

import com.dentalclinicsystem.dao.ConfiguracionDAO;
import com.dentalclinicsystem.model.Configuracion;
import com.dentalclinicsystem.view.DashboardModerno2;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.util.Enumeration;
import java.util.List;

public class ConfiguracionController {
    private ConfiguracionDAO configuracionDAO;
    
    // Constantes de configuración
    public static final String TEMA = "tema";
    public static final String TEMA_OSCURO = "oscuro";
    public static final String TEMA_CLARO = "claro";
    public static final String TAMAÑO_FUENTE = "font_size";
    
    public ConfiguracionController() {
        this.configuracionDAO = new ConfiguracionDAO();
    }
    
    public boolean guardarConfiguracion(String clave, String valor) {
        return configuracionDAO.guardar(clave, valor);
    }
    
    public boolean guardarConfiguracion(Configuracion config) {
        return configuracionDAO.guardar(config);
    }
    
    public String obtenerConfiguracion(String clave) {
        return configuracionDAO.obtener(clave);
    }
    
    public Configuracion obtenerConfiguracionCompleta(String clave) {
        return configuracionDAO.obtenerConfiguracion(clave);
    }
    
    public List<Configuracion> obtenerPorCategoria(String categoria) {
        return configuracionDAO.obtenerPorCategoria(categoria);
    }
    
    public List<Configuracion> obtenerTodas() {
        return configuracionDAO.obtenerTodas();
    }
    
    // ========== MÉTODOS ESPECÍFICOS DE CONFIGURACIÓN ==========
    
    public String getTema() {
        String tema = obtenerConfiguracion(TEMA);
        return tema != null ? tema : TEMA_OSCURO;
    }
    
    public void setTema(String tema) {
        guardarConfiguracion(TEMA, tema);
        System.out.println("✅ Tema guardado: " + tema);
    }
    
    public int getTamañoFuente() {
        String tamaño = obtenerConfiguracion(TAMAÑO_FUENTE);
        try {
            return tamaño != null ? Integer.parseInt(tamaño) : 14;
        } catch (NumberFormatException e) {
            return 14;
        }
    }
    
    public void setTamañoFuente(int tamaño) {
        guardarConfiguracion(TAMAÑO_FUENTE, String.valueOf(tamaño));
        System.out.println("✅ Tamaño de fuente guardado: " + tamaño);
    }
    
    /**
     * Aplica la fuente a TODOS los componentes de la UI
     */
    public static void aplicarFuenteGlobal(int size) {
        try {
            FontUIResource fontResource = new FontUIResource("Segoe UI", Font.PLAIN, size);
            
            // Obtener todas las claves de UIManager que son Font
            Enumeration<Object> keys = UIManager.getDefaults().keys();
            while (keys.hasMoreElements()) {
                Object key = keys.nextElement();
                Object value = UIManager.get(key);
                if (value instanceof FontUIResource) {
                    UIManager.put(key, fontResource);
                }
            }
            
            // Forzar actualización de todos los componentes
            for (Window window : Window.getWindows()) {
                if (window.isVisible()) {
                    actualizarFuenteComponentes(window, fontResource);
                }
            }
            
            System.out.println("✅ Fuente global aplicada: " + size + "px");
            
        } catch (Exception e) {
            System.err.println("Error al aplicar fuente: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Actualiza recursivamente la fuente de todos los componentes
     */
    private static void actualizarFuenteComponentes(Component comp, Font font) {
        if (comp == null) return;
        
        comp.setFont(font);
        
        if (comp instanceof Container) {
            Container container = (Container) comp;
            for (Component child : container.getComponents()) {
                actualizarFuenteComponentes(child, font);
            }
        }
    }
    
    /**
     * Aplica el tema y la fuente a TODA la interfaz
     */
    public void aplicarConfiguracionGlobal(JFrame frame) {
        try {
            String tema = getTema();
            int size = getTamañoFuente();
            
            System.out.println("🎨 Aplicando tema: " + tema);
            System.out.println("🔤 Aplicando tamaño de fuente: " + size);
            
            // 1. Cambiar el LookAndFeel
            if (TEMA_CLARO.equals(tema)) {
                UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
            } else {
                UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatDarkLaf());
            }
            
            // 2. Aplicar la fuente a toda la UI
            aplicarFuenteGlobal(size);
            
            // 3. Actualizar el frame principal
            if (frame != null) {
                SwingUtilities.updateComponentTreeUI(frame);
                frame.revalidate();
                frame.repaint();
                
                // Reconstruir el contenido si es DashboardModerno2
                if (frame instanceof DashboardModerno2) {
                    ((DashboardModerno2) frame).recrearContenido();
                }
            }
            
            // 4. Actualizar todas las ventanas abiertas
            for (Window window : Window.getWindows()) {
                if (window.isVisible() && window != frame) {
                    SwingUtilities.updateComponentTreeUI(window);
                    window.revalidate();
                    window.repaint();
                }
            }
            
            System.out.println("✅ Configuración aplicada correctamente");
            
        } catch (Exception e) {
            System.err.println("Error al aplicar configuración: " + e.getMessage());
            e.printStackTrace();
        }
    }
}