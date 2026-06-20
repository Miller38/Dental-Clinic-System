package com.dentalclinicsystem.config;

import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.util.Enumeration;
import javax.swing.JTable;
import javax.swing.UIManager;

public class FontManager {

    public static void aplicarFuente(int size) {
        Font nuevaFuente = new Font("Segoe UI", Font.PLAIN, size);
        
        Enumeration<Object> keys = UIManager.getDefaults().keys();
        
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            
            if (value instanceof Font) {
                UIManager.put(key, nuevaFuente);
            }
        }
        
        System.out.println("✅ Fuente aplicada: " + size + "px");
    }

    public static void actualizarFuente(Component component, Font font) {
        component.setFont(font);
        
        if (component instanceof JTable tabla) {
            tabla.setFont(font);
            tabla.getTableHeader().setFont(font);
            tabla.setRowHeight(font.getSize() + 20);
        }
        
        if (component instanceof Container contenedor) {
            for (Component child : contenedor.getComponents()) {
                actualizarFuente(child, font);
            }
        }
    }
}