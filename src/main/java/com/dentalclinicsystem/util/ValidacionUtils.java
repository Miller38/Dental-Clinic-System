package com.dentalclinicsystem.utils;

import java.util.regex.Pattern;

/**
 * Utilidad para validaciones
 * 
 * @author Miller
 * @version 1.0
 */
public class ValidacionUtils {
    
    // Patrones de validación
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    
    private static final Pattern TELEFONO_PATTERN = 
        Pattern.compile("^[0-9]{7,15}$");
    
    private static final Pattern CEDULA_PATTERN = 
        Pattern.compile("^[0-9]{10}$");
    
    private static final Pattern USUARIO_PATTERN = 
        Pattern.compile("^[a-zA-Z0-9_-]{3,20}$");
    
    /**
     * Valida un email
     */
    public static boolean validarEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * Valida un teléfono
     */
    public static boolean validarTelefono(String telefono) {
        if (telefono == null || telefono.trim().isEmpty()) {
            return false;
        }
        return TELEFONO_PATTERN.matcher(telefono).matches();
    }
    
    /**
     * Valida una cédula ecuatoriana
     */
    public static boolean validarCedula(String cedula) {
        if (cedula == null || !CEDULA_PATTERN.matcher(cedula).matches()) {
            return false;
        }
        
        // Algoritmo de validación de cédula ecuatoriana
        int[] coeficientes = {2, 1, 2, 1, 2, 1, 2, 1, 2};
        int suma = 0;
        
        for (int i = 0; i < 9; i++) {
            int digito = Integer.parseInt(String.valueOf(cedula.charAt(i)));
            int producto = digito * coeficientes[i];
            if (producto >= 10) {
                producto -= 9;
            }
            suma += producto;
        }
        
        int digitoVerificador = Integer.parseInt(String.valueOf(cedula.charAt(9)));
        int residuo = suma % 10;
        int resultado = (residuo == 0) ? 0 : 10 - residuo;
        
        return digitoVerificador == resultado;
    }
    
    /**
     * Valida un nombre de usuario
     */
    public static boolean validarUsuario(String usuario) {
        if (usuario == null || usuario.trim().isEmpty()) {
            return false;
        }
        return USUARIO_PATTERN.matcher(usuario).matches();
    }
    
    /**
     * Valida que un campo no esté vacío
     */
    public static boolean validarCampoVacio(String valor) {
        return valor != null && !valor.trim().isEmpty();
    }
    
    /**
     * Valida un rol
     */
    public static boolean validarRol(String rol) {
        if (rol == null || rol.trim().isEmpty()) {
            return false;
        }
        String rolUpper = rol.toUpperCase();
        return rolUpper.equals("ADMIN") || 
               rolUpper.equals("ADMINISTRADOR") ||
               rolUpper.equals("USUARIO") ||
               rolUpper.equals("RECEPCIONISTA") ||
               rolUpper.equals("ODONTOLOGO");
    }
}