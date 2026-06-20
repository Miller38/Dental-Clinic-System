    package com.dentalclinicsystem.config;

    import java.sql.Connection;
    import java.sql.Statement;

    /**
     * Inicializador de base de datos para Consultorio Odontológico
     * @author Refactorizado desde BarberProSystem
     */
    public class InitDatabase {

        public static void crearTablas() {

            // ==================== TABLAS PRINCIPALES ====================

            // TABLA USUARIOS (personal del consultorio)
            String sqlUsuarios = 
                "CREATE TABLE IF NOT EXISTS usuarios (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT NOT NULL," +
                "usuario TEXT UNIQUE NOT NULL," +
                "password TEXT NOT NULL," +
                "rol TEXT NOT NULL," + // ADMIN, DENTISTA, ASISTENTE, RECEPCIONISTA
                "especialidad TEXT," + // Odontólogo general, Ortodoncista, Endodoncista, etc.
                "numero_licencia TEXT," + // Número de licencia profesional
                "email TEXT," +
                "telefono TEXT," +
                "foto TEXT," +
                "estado INTEGER DEFAULT 1," +
                "intentos INTEGER DEFAULT 0," +
                "bloqueado INTEGER DEFAULT 0," +
                "bloqueado_hasta TEXT," +
                "ultimo_acceso TEXT," +
                "fecha_creacion TEXT DEFAULT CURRENT_TIMESTAMP" +
                ")";

            // TABLA PACIENTES (antes clientes)
            String sqlPacientes = 
                "CREATE TABLE IF NOT EXISTS pacientes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT NOT NULL," +
                "apellido TEXT NOT NULL," +
                "fecha_nacimiento TEXT," +
                "edad INTEGER," +
                "genero TEXT," + // M, F, OTRO
                "tipo_documento TEXT," + // CC, CE, PASAPORTE, etc.
                "numero_documento TEXT UNIQUE," +
                "telefono TEXT," +
                "telefono_alternativo TEXT," +
                "email TEXT," +
                "direccion TEXT," +
                "ocupacion TEXT," +
                "estado_civil TEXT," +
                "estado INTEGER DEFAULT 1," + // 1: Activo, 0: Inactivo
                "fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "foto TEXT," +
                "alergias TEXT," + // IMPORTANTE para odontología
                "enfermedades_sistema TEXT," + // Diabetes, hipertensión, etc.
                "medicamentos TEXT," + // Medicamentos actuales
                "contacto_emergencia_nombre TEXT," +
                "contacto_emergencia_telefono TEXT" +
                ")";

            // TABLA HISTORIAL_CLINICO (expediente dental)
            String sqlHistorialClinico = 
                "CREATE TABLE IF NOT EXISTS historial_clinico (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "paciente_id INTEGER NOT NULL," +
                "fecha_apertura TEXT DEFAULT CURRENT_TIMESTAMP," +
                "odontologo_id INTEGER," +
                "motivo_consulta TEXT," +
                "diagnostico TEXT," +
                "tratamiento TEXT," +
                "observaciones TEXT," +
                "odontograma TEXT," + // Representación de dientes en JSON
                "firma_digital BLOB," +
                "FOREIGN KEY(paciente_id) REFERENCES pacientes(id) ON DELETE CASCADE," +
                "FOREIGN KEY(odontologo_id) REFERENCES usuarios(id)" +
                ")";

            // TABLA SERVICIOS ODONTOLÓGICOS
            String sqlServicios = 
                "CREATE TABLE IF NOT EXISTS servicios (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT NOT NULL," +
                "categoria TEXT NOT NULL," + // Consulta, Diagnóstico, Tratamiento, Cirugía, etc.
                "subcategoria TEXT," + // Limpieza, Extracción, Obturación, Endodoncia, etc.
                "duracion_minutos INTEGER DEFAULT 30," + // Duración típica del servicio
                "precio REAL NOT NULL," +
                "precio_particular REAL," +
                "precio_aseguradora REAL," +
                "codigo_procedimiento TEXT," + // Códigos estandarizados (CDT, etc.)
                "requiere_consulta_previa INTEGER DEFAULT 0," +
                "materiales_necesarios TEXT," + // Lista de materiales/insumos en JSON
                "estado INTEGER DEFAULT 1" +
                ")";

            // TABLA TRATAMIENTOS (planes de tratamiento a largo plazo)
            String sqlTratamientos = 
                "CREATE TABLE IF NOT EXISTS tratamientos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "paciente_id INTEGER NOT NULL," +
                "odontologo_id INTEGER NOT NULL," +
                "nombre_tratamiento TEXT NOT NULL," +
                "descripcion TEXT," +
                "fecha_inicio TEXT," +
                "fecha_fin_estimada TEXT," +
                "fecha_fin_real TEXT," +
                "costo_total_estimado REAL," +
                "costo_total_real REAL," +
                "estado TEXT DEFAULT 'ACTIVO'," + // ACTIVO, COMPLETADO, CANCELADO, PAUSADO
                "notas TEXT," +
                "FOREIGN KEY(paciente_id) REFERENCES pacientes(id)," +
                "FOREIGN KEY(odontologo_id) REFERENCES usuarios(id)" +
                ")";

            // TABLA CITAS MEJORADA
            String sqlCitas = 
                "CREATE TABLE IF NOT EXISTS citas (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "paciente_id INTEGER NOT NULL," +
                "odontologo_id INTEGER NOT NULL," +
                "servicio_id INTEGER NOT NULL," +
                "tratamiento_id INTEGER," + // Opcional, si es parte de un tratamiento
                "fecha TEXT NOT NULL," +
                "hora TEXT NOT NULL," +
                "duracion INTEGER DEFAULT 30," +
                "estado TEXT NOT NULL," + // PROGRAMADA, CONFIRMADA, EN_PROCESO, COMPLETADA, CANCELADA, NO_ASISTIO
                "nota TEXT," +
                "recordatorio_enviado INTEGER DEFAULT 0," +
                "fecha_creacion TEXT DEFAULT CURRENT_TIMESTAMP," +
                "modificada_por TEXT," +
                "FOREIGN KEY(paciente_id) REFERENCES pacientes(id)," +
                "FOREIGN KEY(odontologo_id) REFERENCES usuarios(id)," +
                "FOREIGN KEY(servicio_id) REFERENCES servicios(id)," +
                "FOREIGN KEY(tratamiento_id) REFERENCES tratamientos(id)" +
                ")";

            // ==================== INVENTARIO Y PRODUCTOS ====================

            // TABLA INSUMOS/INVENTARIO (productos médicos/odontológicos)
            String sqlInsumos = 
                "CREATE TABLE IF NOT EXISTS insumos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT NOT NULL," +
                "codigo TEXT UNIQUE," +
                "categoria TEXT," + // Instrumental, Materiales dentales, Medicamentos, PPE, etc.
                "tipo_insumo TEXT," + // CONSUMIBLE, NO_CONSUMIBLE, CADUCO, MEDICAMENTO
                "presentacion TEXT," + // Unidad, Caja, Kit, etc.
                "stock INTEGER DEFAULT 0," +
                "stock_minimo INTEGER DEFAULT 5," +
                "stock_maximo INTEGER," +
                "ubicacion TEXT," + // Ubicación física en consultorio
                "precio_compra REAL," +
                "precio_venta REAL," +
                "fecha_compra TEXT," +
                "fecha_vencimiento TEXT," +
                "lote TEXT," +
                "proveedor TEXT," +
                "estado INTEGER DEFAULT 1" +
                ")";

            // TABLA MOVIMIENTOS DE INVENTARIO
            String sqlMovimientos = 
                "CREATE TABLE IF NOT EXISTS movimientos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "insumo_id INTEGER NOT NULL," +
                "tipo TEXT NOT NULL," + // ENTRADA, SALIDA, AJUSTE, VENCIDO, DEVOLUCION
                "cantidad INTEGER NOT NULL," +
                "motivo TEXT," +
                "usuario TEXT," +
                "fecha TEXT DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY(insumo_id) REFERENCES insumos(id)" +
                ")";

            // ==================== FINANZAS Y VENTAS ====================

            // TABLA VENTAS (facturación)
            String sqlVentas = 
                "CREATE TABLE IF NOT EXISTS ventas (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "paciente_id INTEGER NOT NULL," +
                "usuario TEXT," +
                "odontologo_id INTEGER," +
                "tipo_comprobante TEXT," + // FACTURA, BOLETA, NOTA_CREDITO
                "serie TEXT," +
                "numero_comprobante TEXT," +
                "subtotal REAL," +
                "impuesto REAL," + // IVA u otros impuestos
                "descuento REAL," +
                "total REAL," +
                "metodo_pago TEXT," + // EFECTIVO, TARJETA, TRANSFERENCIA, SEGURO
                "seguro_nombre TEXT," + // Si aplica seguro dental
                "seguro_cobertura REAL," + // Monto cubierto por seguro
                "estado TEXT DEFAULT 'ACTIVA'," + // ACTIVA, PAGADA, CANCELADA, ANULADA
                "fecha TEXT DEFAULT CURRENT_TIMESTAMP," +
                "notas TEXT," +
                "FOREIGN KEY(paciente_id) REFERENCES pacientes(id)" +
                ")";

            // TABLA DETALLES DE VENTA (servicios e insumos)
            String sqlDetalleVenta = 
                "CREATE TABLE IF NOT EXISTS detalle_ventas (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "venta_id INTEGER NOT NULL," +
                "tipo_item TEXT NOT NULL," + // SERVICIO, INSUMO, TRATAMIENTO
                "item_id INTEGER NOT NULL," +
                "nombre TEXT NOT NULL," +
                "cantidad INTEGER NOT NULL," +
                "precio_unitario REAL NOT NULL," +
                "subtotal REAL NOT NULL," +
                "FOREIGN KEY(venta_id) REFERENCES ventas(id) ON DELETE CASCADE" +
                ")";

            // ==================== SEGURIDAD Y AUDITORÍA ====================

            // TABLA LOGS_LOGIN
            String sqlLogsLogin = 
                "CREATE TABLE IF NOT EXISTS logs_login (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "usuario TEXT NOT NULL," +
                "fecha TEXT NOT NULL," +
                "estado TEXT NOT NULL," + // EXITOSO, FALLIDO, BLOQUEADO
                "mensaje TEXT," +
                "ip TEXT" +
                ")";

            // TABLA AUDITORIA GENERAL
            String sqlAuditoria = 
                "CREATE TABLE IF NOT EXISTS auditoria (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "usuario TEXT NOT NULL," +
                "accion TEXT NOT NULL," + // INSERT, UPDATE, DELETE, LOGIN, LOGOUT
                "tabla TEXT," +
                "registro_id INTEGER," +
                "detalle_anterior TEXT," +
                "detalle_nuevo TEXT," +
                "fecha TEXT DEFAULT CURRENT_TIMESTAMP" +
                ")";

            // TABLA SESIONES
            String sqlSesiones = 
                "CREATE TABLE IF NOT EXISTS sesiones (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "usuario TEXT NOT NULL," +
                "login TEXT," +
                "logout TEXT," +
                "duracion_minutos INTEGER" +
                ")";

            // TABLA PERMISOS
            String sqlPermisos = 
                "CREATE TABLE IF NOT EXISTS permisos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "usuario_id INTEGER NOT NULL," +
                "modulo TEXT NOT NULL," + // PACIENTES, CITAS, VENTAS, INVENTARIO, REPORTES, etc.
                "accion TEXT," + // CREAR, LEER, ACTUALIZAR, ELIMINAR
                "permitido INTEGER DEFAULT 1," +
                "FOREIGN KEY(usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE" +
                ")";

            // ==================== CONFIGURACIÓN Y PARÁMETROS ====================

            // TABLA CONFIGURACION
            String sqlConfiguracion = 
                "CREATE TABLE IF NOT EXISTS configuracion (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "clave TEXT UNIQUE NOT NULL," +
                "valor TEXT NOT NULL," +
                "descripcion TEXT," +
                "categoria TEXT" +
                ")";

            // TABLA HORARIOS_ATENCION (disponibilidad de odontólogos)
            String sqlHorariosAtencion = 
                "CREATE TABLE IF NOT EXISTS horarios_atencion (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "odontologo_id INTEGER NOT NULL," +
                "dia_semana INTEGER NOT NULL," + // 0=Lunes, 6=Domingo
                "hora_inicio TEXT NOT NULL," +
                "hora_fin TEXT NOT NULL," +
                "intervalo_minutos INTEGER DEFAULT 30," +
                "activo INTEGER DEFAULT 1," +
                "FOREIGN KEY(odontologo_id) REFERENCES usuarios(id) ON DELETE CASCADE" +
                ")";

            // TABLA RECORDATORIOS (automatización)
            String sqlRecordatorios = 
                "CREATE TABLE IF NOT EXISTS recordatorios (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "cita_id INTEGER NOT NULL," +
                "tipo TEXT," + // SMS, EMAIL, WHATSAPP
                "fecha_envio TEXT," +
                "estado TEXT," + // PENDIENTE, ENVIADO, FALLIDO
                "mensaje TEXT," +
                "FOREIGN KEY(cita_id) REFERENCES citas(id) ON DELETE CASCADE" +
                ")";

            // ==================== ODONTOGRAMA Y DIAGNÓSTICO ====================

            // TABLA ODONTOGRAMA (diagnóstico dental detallado)
            String sqlOdontograma = 
                "CREATE TABLE IF NOT EXISTS odontograma (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "paciente_id INTEGER NOT NULL," +
                "diente INTEGER NOT NULL," + // 11 al 48 (numeración universal)
                "superficie TEXT," + // VESTIBULAR, LINGUAL, MESIAL, DISTAL, OCLUSAL
                "diagnostico TEXT," + // CARIES, FRACTURA, AUSENTE, TRATADO, etc.
                "tratamiento_realizado TEXT," +
                "fecha_diagnostico TEXT," +
                "odontologo_id INTEGER," +
                "notas TEXT," +
                "FOREIGN KEY(paciente_id) REFERENCES pacientes(id) ON DELETE CASCADE," +
                "FOREIGN KEY(odontologo_id) REFERENCES usuarios(id)" +
                ")";

            // ==================== FACTURACIÓN ELECTRÓNICA ====================

            // TABLA FACTURAS_ELECTRONICAS (cumplimiento fiscal)
            String sqlFacturasElectronicas = 
                "CREATE TABLE IF NOT EXISTS facturas_electronicas (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "venta_id INTEGER NOT NULL," +
                "uuid TEXT UNIQUE," +
                "fecha_emision TEXT," +
                "fecha_firma TEXT," +
                "xml_firmado TEXT," +
                "codigo_qr TEXT," +
                "estado TEXT," + // GENERADA, FIRMADA, ENVIADA, RECHAZADA
                "mensaje_sunat TEXT," +
                "FOREIGN KEY(venta_id) REFERENCES ventas(id) ON DELETE CASCADE" +
                ")";

            // En tu InitDatabase.java o donde crees las tablas
             String sqlSesion = 
                "CREATE TABLE IF NOT EXISTS sesiones (" +
                " id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "usuario TEXT NOT NULL," +
                "login TEXT NOT NULL," +
                "logout TEXT," +
                "duracion_minutos INTEGER," +
                "ultima_actividad TEXT," +
                "FOREIGN KEY (usuario) REFERENCES usuarios(usuario) ON DELETE CASCADE" +
                ")";


            // ==================== CREACIÓN DE TABLAS ====================

            try (Connection conn = ConexionSQLite.conectar(); 
                 Statement stmt = conn.createStatement()) {

                // Crear tablas en orden correcto (evitar conflictos FK)
                stmt.execute(sqlUsuarios);
                stmt.execute(sqlPacientes);
                stmt.execute(sqlHistorialClinico);
                stmt.execute(sqlServicios);
                stmt.execute(sqlTratamientos);
                stmt.execute(sqlInsumos);
                stmt.execute(sqlVentas);
                stmt.execute(sqlCitas);
                stmt.execute(sqlDetalleVenta);
                stmt.execute(sqlMovimientos);
                stmt.execute(sqlOdontograma);
                stmt.execute(sqlSesion);

                // Seguridad y configuración
                stmt.execute(sqlLogsLogin);
                stmt.execute(sqlAuditoria);
                stmt.execute(sqlSesiones);
                stmt.execute(sqlPermisos);
                stmt.execute(sqlConfiguracion);
                stmt.execute(sqlHorariosAtencion);
                stmt.execute(sqlRecordatorios);
                stmt.execute(sqlFacturasElectronicas);

                System.out.println("╔═══════════════════════════════════════╗");
                System.out.println("║  Tablas del consultorio creadas con   ║");
                System.out.println("║          éxito ✓✓✓                   ║");
                System.out.println("╚═══════════════════════════════════════╝");

            } catch (Exception e) {
                System.out.println("❌ Error al crear las tablas: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }