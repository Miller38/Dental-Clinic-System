package com.dentalclinicsystem.service;

import com.dentalclinicsystem.config.ConexionSQLite;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class BackupService {
    
    private static final String DATA_DIR = "data";
    private static final String BACKUP_DIR = "backups";
    private static final String DB_NAME = "dental_clinic.db";
    private static final String AUTOBACKUP_DIR = "backups/automaticos";
    
    public BackupService() {
        crearDirectorios();
    }
    
    private void crearDirectorios() {
        try {
            File backupDir = new File(BACKUP_DIR);
            if (!backupDir.exists()) {
                backupDir.mkdirs();
                System.out.println("📁 Directorio de backups creado: " + backupDir.getAbsolutePath());
            }
            
            File autobackupDir = new File(AUTOBACKUP_DIR);
            if (!autobackupDir.exists()) {
                autobackupDir.mkdirs();
                System.out.println("📁 Directorio de backups automáticos creado: " + autobackupDir.getAbsolutePath());
            }
        } catch (Exception e) {
            System.err.println("❌ Error al crear directorios: " + e.getMessage());
        }
    }
    
    // ================================================================
    // ============== BACKUP MANUAL ===================================
    // ================================================================
    
    public boolean crearBackup(JFrame parent) {
        try {
            String fecha = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            String nombreBackup = "backup_" + fecha + ".zip";
            String rutaBackup = BACKUP_DIR + File.separator + nombreBackup;
            
            exportarYComprimir(rutaBackup);
            limpiarBackupsAntiguos(BACKUP_DIR, 10);
            
            JOptionPane.showMessageDialog(parent, 
                "✅ Backup creado exitosamente:\n" + rutaBackup,
                "Backup completado", JOptionPane.INFORMATION_MESSAGE);
            
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ Error al crear backup: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(parent, 
                "Error al crear backup: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    // ================================================================
    // ============== BACKUP AUTOMÁTICO ===============================
    // ================================================================
    
    public boolean crearBackupAutomatico() {
        try {
            String fecha = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            String nombreBackup = "autobackup_" + fecha + ".zip";
            String rutaBackup = AUTOBACKUP_DIR + File.separator + nombreBackup;
            
            exportarYComprimir(rutaBackup);
            limpiarBackupsAntiguos(AUTOBACKUP_DIR, 20);
            
            System.out.println("✅ Backup automático creado: " + rutaBackup);
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ Error al crear backup automático: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private void exportarYComprimir(String rutaBackup) throws Exception {
        String fecha = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        String sqlPath = BACKUP_DIR + File.separator + "temp_" + fecha + ".sql";
        
        exportarBaseDatosSQL(sqlPath);
        comprimirArchivo(sqlPath, rutaBackup);
        
        File tempFile = new File(sqlPath);
        if (tempFile.exists()) {
            tempFile.delete();
        }
        
        System.out.println("✅ Backup guardado: " + rutaBackup);
    }
    
    private void exportarBaseDatosSQL(String sqlPath) throws Exception {
        try (Connection conn = ConexionSQLite.conectar();
             PrintWriter writer = new PrintWriter(new FileWriter(sqlPath))) {
            
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});
            
            writer.println("-- ============================================");
            writer.println("-- BACKUP - Dental Clinic System");
            writer.println("-- Fecha: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
            writer.println("-- ============================================");
            writer.println();
            
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                
                if (tableName.startsWith("sqlite_")) continue;
                
                writer.println("-- Tabla: " + tableName);
                writer.println("DROP TABLE IF EXISTS " + tableName + ";");
                
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT sql FROM sqlite_master WHERE type='table' AND name='" + tableName + "'")) {
                    if (rs.next()) {
                        String createSql = rs.getString("sql");
                        if (createSql != null) {
                            writer.println(createSql + ";");
                        }
                    }
                }
                
                writer.println();
                
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName)) {
                    
                    ResultSetMetaData rsMeta = rs.getMetaData();
                    int columnCount = rsMeta.getColumnCount();
                    
                    while (rs.next()) {
                        StringBuilder insert = new StringBuilder("INSERT INTO " + tableName + " VALUES (");
                        for (int i = 1; i <= columnCount; i++) {
                            Object value = rs.getObject(i);
                            if (value == null) {
                                insert.append("NULL");
                            } else if (value instanceof String) {
                                String strValue = ((String) value).replace("'", "''");
                                insert.append("'").append(strValue).append("'");
                            } else if (value instanceof Date) {
                                insert.append("'").append(value.toString()).append("'");
                            } else {
                                insert.append(value.toString());
                            }
                            if (i < columnCount) insert.append(", ");
                        }
                        insert.append(");");
                        writer.println(insert.toString());
                    }
                    writer.println();
                }
            }
            
            writer.println("-- ============================================");
            writer.println("-- Fin del backup");
            writer.println("-- ============================================");
            
            System.out.println("✅ Base de datos exportada a: " + sqlPath);
        }
    }
    
    private void comprimirArchivo(String archivoOrigen, String archivoDestino) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(archivoDestino);
             ZipOutputStream zos = new ZipOutputStream(fos);
             FileInputStream fis = new FileInputStream(archivoOrigen)) {
            
            ZipEntry entry = new ZipEntry(new File(archivoOrigen).getName());
            zos.putNextEntry(entry);
            
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, length);
            }
            
            zos.closeEntry();
            System.out.println("✅ Archivo comprimido: " + archivoDestino);
        }
    }
    
    private void limpiarBackupsAntiguos(String directorio, int mantener) {
        try {
            File backupDir = new File(directorio);
            File[] archivos = backupDir.listFiles((dir, name) -> 
                (name.startsWith("backup_") || name.startsWith("autobackup_")) && name.endsWith(".zip"));
            
            if (archivos != null && archivos.length > mantener) {
                java.util.Arrays.sort(archivos, (f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));
                
                for (int i = mantener; i < archivos.length; i++) {
                    if (archivos[i].delete()) {
                        System.out.println("🗑️ Backup antiguo eliminado: " + archivos[i].getName());
                    }
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error al limpiar backups antiguos: " + e.getMessage());
        }
    }
    
    // ================================================================
    // ============== RESTAURAR BACKUP ================================
    // ================================================================
    
    public boolean restaurarBackup(JFrame parent) {
        try {
            JFileChooser fileChooser = new JFileChooser(BACKUP_DIR);
            fileChooser.setDialogTitle("Seleccionar archivo de backup");
            fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos ZIP (*.zip)", "zip"));
            
            int result = fileChooser.showOpenDialog(parent);
            if (result != JFileChooser.APPROVE_OPTION) {
                return false;
            }
            
            File archivoBackup = fileChooser.getSelectedFile();
            
            int confirm = JOptionPane.showConfirmDialog(parent,
                "⚠️ ADVERTENCIA: Esta acción reemplazará TODOS los datos actuales.\n" +
                "¿Está seguro de continuar?\n\n" +
                "Archivo: " + archivoBackup.getName(),
                "Confirmar restauración",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (confirm != JOptionPane.YES_OPTION) {
                return false;
            }
            
            String sqlPath = BACKUP_DIR + File.separator + "restore_temp.sql";
            descomprimirArchivo(archivoBackup.getAbsolutePath(), sqlPath);
            restaurarBaseDatosSQL(sqlPath);
            
            File tempFile = new File(sqlPath);
            if (tempFile.exists()) {
                tempFile.delete();
            }
            
            JOptionPane.showMessageDialog(parent, 
                "✅ Backup restaurado exitosamente.\n" +
                "La base de datos ha sido actualizada.",
                "Restauración completada", JOptionPane.INFORMATION_MESSAGE);
            
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ Error al restaurar backup: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(parent, 
                "Error al restaurar backup: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    private void descomprimirArchivo(String archivoZip, String destino) throws IOException {
        try (FileInputStream fis = new FileInputStream(archivoZip);
             ZipInputStream zis = new ZipInputStream(fis);
             FileOutputStream fos = new FileOutputStream(destino)) {
            
            ZipEntry entry = zis.getNextEntry();
            if (entry != null) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, length);
                }
                zis.closeEntry();
            }
            
            System.out.println("✅ Archivo descomprimido: " + destino);
        }
    }
    
    private void restaurarBaseDatosSQL(String sqlPath) throws Exception {
        String dbPath = DATA_DIR + File.separator + DB_NAME;
        String backupDbPath = DATA_DIR + File.separator + "backup_temp_" + System.currentTimeMillis() + ".db";
        
        Files.copy(Paths.get(dbPath), Paths.get(backupDbPath), StandardCopyOption.REPLACE_EXISTING);
        
        try {
            new File(dbPath).delete();
            
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
                 Statement stmt = conn.createStatement()) {
                
                String sql = new String(Files.readAllBytes(Paths.get(sqlPath)));
                String[] sentencias = sql.split(";");
                for (String sentencia : sentencias) {
                    sentencia = sentencia.trim();
                    if (!sentencia.isEmpty() && !sentencia.startsWith("--")) {
                        try {
                            stmt.execute(sentencia);
                        } catch (SQLException e) {
                            System.err.println("⚠️ Error en sentencia SQL: " + e.getMessage());
                        }
                    }
                }
            }
            
            System.out.println("✅ Base de datos restaurada exitosamente");
            
        } catch (Exception e) {
            Files.copy(Paths.get(backupDbPath), Paths.get(dbPath), StandardCopyOption.REPLACE_EXISTING);
            throw e;
        } finally {
            File tempFile = new File(backupDbPath);
            if (tempFile.exists()) {
                tempFile.delete();
            }
        }
    }
    
    // ================================================================
    // ============== LISTAR BACKUPS ==================================
    // ================================================================
    
    public File[] listarBackups() {
        File backupDir = new File(BACKUP_DIR);
        File[] archivos = backupDir.listFiles((dir, name) -> 
            name.startsWith("backup_") && name.endsWith(".zip"));
        return archivos != null ? archivos : new File[0];
    }
    
    public File[] listarBackupsAutomaticos() {
        File backupDir = new File(AUTOBACKUP_DIR);
        File[] archivos = backupDir.listFiles((dir, name) -> 
            name.startsWith("autobackup_") && name.endsWith(".zip"));
        return archivos != null ? archivos : new File[0];
    }
    
    public String getInfoBackup(File archivo) {
        if (archivo == null || !archivo.exists()) {
            return "Archivo no disponible";
        }
        
        long size = archivo.length();
        String sizeStr = size > 1024 * 1024 ? 
            String.format("%.2f MB", size / (1024.0 * 1024.0)) :
            String.format("%.2f KB", size / 1024.0);
        
        String fecha = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
            .format(new Date(archivo.lastModified()));
        
        String tipo = archivo.getParentFile() != null && 
                      archivo.getParentFile().getName().equals("automaticos") ? "🤖 Auto" : "📁 Manual";
        
        return tipo + " - " + archivo.getName() + " - " + sizeStr + " - " + fecha;
    }
}