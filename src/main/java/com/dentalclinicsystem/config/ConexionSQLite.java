package com.dentalclinicsystem.config;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Configuración de conexión a SQLite para consultorio odontológico
 * @author Refactorizado desde BarberProSystem
 */
public class ConexionSQLite {
    
    // Base de datos en carpeta data/ para mejor organización
    private static final String DATA_DIR = "data";
    private static final String DB_NAME = "dental_clinic.db";
    private static final String URL = "jdbc:sqlite:" + DATA_DIR + File.separator + DB_NAME;
    
    /**
     * Establece conexión con la base de datos
     * @return Connection o null si falla
     */
    public static Connection conectar() {
        try {
            // Crear directorio data si no existe
            File dataDir = new File(DATA_DIR);
            if (!dataDir.exists()) {
                dataDir.mkdirs();
                System.out.println("📁 Directorio 'data' creado.");
            }
            
            // Mostrar ruta exacta para debugging
            System.out.println("🔍 Ruta de la BD: " + URL);
            
            Connection conn = DriverManager.getConnection(URL);
            
            // Activar claves foráneas y modo WAL (mejor rendimiento)
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON");
                stmt.execute("PRAGMA journal_mode = WAL");
                stmt.execute("PRAGMA synchronous = NORMAL");
                stmt.execute("PRAGMA cache_size = 10000");
            }
            
            System.out.println("✅ Conexión SQLite exitosa.");
            return conn;
            
        } catch (SQLException e) {
            System.out.println("❌ Error de conexión: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Verifica si la base de datos ya existe
     */
    public static boolean baseDatosExiste() {
        File dbFile = new File(DATA_DIR + File.separator + DB_NAME);
        return dbFile.exists();
    }
    
    /**
     * Cierra la conexión de forma segura
     */
    public static void cerrarConexion(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("🔒 Conexión cerrada.");
            } catch (SQLException e) {
                System.err.println("Error al cerrar conexión: " + e.getMessage());
            }
        }
    }
}