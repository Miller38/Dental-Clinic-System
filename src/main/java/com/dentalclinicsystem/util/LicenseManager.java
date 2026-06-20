package com.dentalclinicsystem.util;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.prefs.Preferences;

public class LicenseManager {
    
    private static final String LICENSE_FILE = "license.key";
    private static final String PREF_NODE = "dentalclinicsystem";
    private static final String PREF_KEY = "activated";
    private static final String PREF_EXPIRY = "expiry_date";
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    // ========== VERIFICAR SI LA APLICACIÓN ESTÁ ACTIVADA ==========
    public static boolean isActivated() {
        Preferences prefs = Preferences.userRoot().node(PREF_NODE);
        boolean activada = prefs.getBoolean(PREF_KEY, false);
        
        if (!activada) {
            File licFile = new File(LICENSE_FILE);
            if (licFile.exists()) {
                return verificarLicenciaArchivo();
            }
            return false;
        }
        
        // Verificar fecha de expiración
        String expiryDateStr = prefs.get(PREF_EXPIRY, null);
        if (expiryDateStr == null) {
            return false;
        }
        
        try {
            LocalDate expiryDate = LocalDate.parse(expiryDateStr, DATE_FORMATTER);
            LocalDate today = LocalDate.now();
            
            if (today.isAfter(expiryDate)) {
                desactivarLicencia();
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        
        return true;
    }
    
    // ========== ACTIVAR LICENCIA CON FECHA DE EXPIRACIÓN ==========
    public static boolean activate(String licenseKey, LocalDate expiryDate) {
        if (licenseKey == null || licenseKey.isEmpty()) {
            return false;
        }
        
        String currentHWID = HardwareUtils.getHWID();
        String validKey = generarKey(currentHWID);
        
        if (licenseKey.equals(validKey)) {
            saveLicense(currentHWID, expiryDate);
            Preferences prefs = Preferences.userRoot().node(PREF_NODE);
            prefs.putBoolean(PREF_KEY, true);
            prefs.put(PREF_EXPIRY, expiryDate.format(DATE_FORMATTER));
            return true;
        }
        
        return false;
    }
    
    // ========== ACTIVAR SOLO CON KEY (SIN FECHA - MANTENER COMPATIBILIDAD) ==========
    public static boolean activar(String key) {
        String hwid = HardwareUtils.getHWID();
        String validKey = generarKey(hwid);
        
        if (key.equals(validKey)) {
            // Si no se especifica fecha, poner 30 días por defecto
            LocalDate expiryDate = LocalDate.now().plusDays(30);
            return activate(key, expiryDate);
        }
        
        return false;
    }
    
    // ========== VERIFICAR LICENCIA DESDE ARCHIVO ==========
    private static boolean verificarLicenciaArchivo() {
        try (BufferedReader br = new BufferedReader(new FileReader(LICENSE_FILE))) {
            String hwidGuardado = br.readLine();
            String expiryDateStr = br.readLine();
            
            if (hwidGuardado == null || expiryDateStr == null) {
                return false;
            }
            
            String currentHWID = HardwareUtils.getHWID();
            LocalDate expiryDate = LocalDate.parse(expiryDateStr, DATE_FORMATTER);
            LocalDate today = LocalDate.now();
            
            if (hwidGuardado.equals(currentHWID) && !today.isAfter(expiryDate)) {
                Preferences prefs = Preferences.userRoot().node(PREF_NODE);
                prefs.putBoolean(PREF_KEY, true);
                prefs.put(PREF_EXPIRY, expiryDateStr);
                return true;
            } else if (today.isAfter(expiryDate)) {
                new File(LICENSE_FILE).delete();
            }
        } catch (Exception e) {}
        return false;
    }
    
    // ========== VALIDAR LICENCIA (MÉTODO ORIGINAL) ==========
    public static boolean licenciaValida() {
        return isActivated();
    }
    
    // ========== GUARDAR LICENCIA EN ARCHIVO ==========
    private static void saveLicense(String hwid, LocalDate expiryDate) {
        try (FileWriter fw = new FileWriter(LICENSE_FILE);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println(hwid);
            pw.println(expiryDate.format(DATE_FORMATTER));
        } catch (Exception e) {}
    }
    
    // ========== DESACTIVAR LICENCIA ==========
    private static void desactivarLicencia() {
        Preferences prefs = Preferences.userRoot().node(PREF_NODE);
        prefs.remove(PREF_KEY);
        prefs.remove(PREF_EXPIRY);
        
        File licFile = new File(LICENSE_FILE);
        if (licFile.exists()) {
            licFile.delete();
        }
    }
    
    // ========== GENERAR KEY (FORMATO LEGIBLE) ==========
    public static String generarKey(String hwid) {
        if (hwid == null || hwid.length() < 16) {
            return "XXXX-XXXX-DENTALCLINICSYSTEM";
        }
        return hwid.substring(0, 8) + "-" + hwid.substring(8, 16) + "-DentalClinicSystem";
    }
    
    // ========== OBTENER FECHA DE EXPIRACIÓN ==========
    public static String getExpiryDate() {
        Preferences prefs = Preferences.userRoot().node(PREF_NODE);
        return prefs.get(PREF_EXPIRY, null);
    }
    
    // ========== OBTENER DÍAS RESTANTES ==========
    public static long getDaysRemaining() {
        String expiryDateStr = getExpiryDate();
        if (expiryDateStr == null) return 0;
        
        try {
            LocalDate expiryDate = LocalDate.parse(expiryDateStr, DATE_FORMATTER);
            LocalDate today = LocalDate.now();
            return java.time.temporal.ChronoUnit.DAYS.between(today, expiryDate);
        } catch (Exception e) {
            return 0;
        }
    }
    
    // ========== OBTENER HWID ACTUAL ==========
    public static String getCurrentHWID() {
        return HardwareUtils.getHWID();
    }
}