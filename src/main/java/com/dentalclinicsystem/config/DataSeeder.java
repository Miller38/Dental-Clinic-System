package com.dentalclinicsystem.config;

import com.dentalclinicsystem.dao.UsuarioDAO;
import com.dentalclinicsystem.model.Usuario;


public class DataSeeder {
    
    private static UsuarioDAO usuarioDAO = new UsuarioDAO();
    
    public static void crearAdmin() {
        boolean existe = usuarioDAO.existeUsuario("admin");
        
        if (!existe) {
            Usuario admin = new Usuario();
            admin.setNombre("Administrador Sistema");
            admin.setUsuario("admin");
            // La contraseña se encriptará dentro del DAO
            admin.setPassword("admin123");
            admin.setRol("ADMIN");
            admin.setEspecialidad("Odontólogo General");
            admin.setNumeroLicencia("12345");
            admin.setEmail("admin@dentalclinic.com");
            admin.setTelefono("3000000000");
            admin.setEstado(1);
            admin.setBloqueado(0);
            
            boolean insertado = usuarioDAO.insertar(admin);
            
            if (insertado) {
                System.out.println("✅ Usuario admin creado - Usuario: admin / Contraseña: admin123");
                
                // VERIFICACIÓN: probar que el login funciona
                Usuario testLogin = usuarioDAO.login("admin", "admin123");
                if (testLogin != null) {
                    System.out.println("✅ VERIFICADO: Login funciona correctamente");
                } else {
                    System.out.println("❌ ERROR: El login NO funciona - revisar encriptación");
                }
            }
        } else {
            System.out.println("ℹ️ Usuario admin ya existe");
            
            // VERIFICACIÓN: probar login del usuario existente
            Usuario testLogin = usuarioDAO.login("admin", "admin123");
            if (testLogin != null) {
                System.out.println("✅ Login funciona correctamente");
            } else {
                System.out.println("❌ ERROR: El login NO funciona - contraseña incorrecta o encriptación");
            }
        }
    }
}