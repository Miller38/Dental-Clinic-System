package com.dentalclinicsystem.config;

import com.dentalclinicsystem.config.ActualizacionConfig;
import javax.swing.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;


public class ActualizadorApp {

    public static void verificarActualizacion(JFrame parent) {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                try {
                    String repo = ActualizacionConfig.getRepo();
                    String versionActual = ActualizacionConfig.getVersion();

                    System.out.println("Buscando actualizaciones...");
                    System.out.println("Repo: " + repo);
                    System.out.println("Version actual: " + versionActual);

                    String apiUrl = "https://api.github.com/repos/" + repo + "/releases/latest";
                    URL url = URI.create(apiUrl).toURL();
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("Accept", "application/vnd.github.v3+json");
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(5000);

                    int responseCode = conn.getResponseCode();
                    if (responseCode != 200) {
                        System.out.println("Error al consultar GitHub: HTTP " + responseCode);
                        conn.disconnect();
                        return null;
                    }

                    String json = new String(conn.getInputStream().readAllBytes());
                    conn.disconnect();

                    String versionRemota = extractJsonValue(json, "tag_name");
                    String downloadUrl = extractDownloadUrl(json);
                    String nombreArchivoRemoto = extractFileNameFromUrl(downloadUrl);

                    if (versionRemota == null || downloadUrl == null) {
                        System.out.println("No se pudo obtener la información de versión");
                        return null;
                    }
                    
                    System.out.println("Version actual: " + versionActual);
                    System.out.println("Version remota: " + versionRemota);
                    System.out.println("Archivo remoto: " + nombreArchivoRemoto);
                    System.out.println("URL descarga: " + downloadUrl);
                    
                    if (versionRemota.startsWith("v")) {
                        versionRemota = versionRemota.substring(1);
                    }

                    if (esVersionNueva(versionActual, versionRemota)) {
                        mostrarDialogoActualizacion(parent, versionActual, versionRemota, downloadUrl, nombreArchivoRemoto);
                    } else {
                        System.out.println("La aplicacion esta actualizada");
                    }

                } catch (Exception e) {
                    System.err.println("Error al verificar actualizaciones:");
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    private static String extractJsonValue(String json, String key) {
        String searchKey = "\"" + key + "\":\"";
        int idx = json.indexOf(searchKey);
        if (idx == -1) {
            return null;
        }
        idx += searchKey.length();
        int end = json.indexOf("\"", idx);
        if (end == -1) {
            return null;
        }
        return json.substring(idx, end);
    }

    private static String extractDownloadUrl(String json) {
        try {
            String searchPattern = "\"browser_download_url\":\"";
            int lastIndex = 0;
            
            while (true) {
                int urlStart = json.indexOf(searchPattern, lastIndex);
                if (urlStart == -1) break;
                
                urlStart += searchPattern.length();
                int urlEnd = json.indexOf("\"", urlStart);
                if (urlEnd == -1) break;
                
                String url = json.substring(urlStart, urlEnd);
                // Aceptar .jar O cualquier ejecutable
                if (url.endsWith(".jar") || url.endsWith(".exe")) {
                    System.out.println("URL encontrada: " + url);
                    return url;
                }
                
                lastIndex = urlEnd;
            }
            
            System.err.println("No se encontró ningún archivo .jar o .exe en los assets");
            return null;
        } catch (Exception e) {
            System.err.println("Error al extraer URL de descarga: " + e.getMessage());
            return null;
        }
    }
    
    private static String extractFileNameFromUrl(String url) {
        if (url == null) return "DentalClinicSytem.jar";
        String[] parts = url.split("/");
        return parts[parts.length - 1];
    }

    private static boolean esVersionNueva(String actual, String remota) {
        try {
            String[] actualParts = actual.split("\\.");
            String[] remotaParts = remota.split("\\.");

            for (int i = 0; i < Math.max(actualParts.length, remotaParts.length); i++) {
                int act = i < actualParts.length ? Integer.parseInt(actualParts[i].replaceAll("[^0-9]", "")) : 0;
                int rem = i < remotaParts.length ? Integer.parseInt(remotaParts[i].replaceAll("[^0-9]", "")) : 0;
                if (rem > act) {
                    return true;
                }
                if (rem < act) {
                    return false;
                }
            }
            return false;
        } catch (NumberFormatException e) {
            System.err.println("Error al comparar versiones: " + e.getMessage());
            return false;
        }
    }

    private static void mostrarDialogoActualizacion(JFrame parent, String versionActual,
            String versionRemota, String downloadUrl, String nombreArchivo) {
        SwingUtilities.invokeLater(() -> {
            int respuesta = JOptionPane.showConfirmDialog(parent,
                    "<html><body style='width: 300px;'>"
                    + "<h3>¡Nueva versión disponible!</h3>"
                    + "<p>Versión actual: <b>" + versionActual + "</b><br>"
                    + "Nueva versión: <b>" + versionRemota + "</b><br>"
                    + "Archivo: <b>" + nombreArchivo + "</b></p>"
                    + "<p>¿Desea actualizar ahora?</p>"
                    + "</body></html>",
                    "Actualización disponible",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE);

            if (respuesta == JOptionPane.YES_OPTION) {
                descargarYActualizar(parent, downloadUrl, versionRemota, nombreArchivo);
            }
        });
    }

    private static void descargarYActualizar(JFrame parent, String downloadUrl, 
                                             String nuevaVersion, String nombreArchivoRemoto) {
        JDialog progressDialog = new JDialog(parent, "Actualizando", true);
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressDialog.add(progressBar);
        progressDialog.setSize(300, 80);
        progressDialog.setLocationRelativeTo(parent);

        new Thread(() -> {
            try {
                String tempDir = System.getProperty("java.io.tmpdir");
                // Usar el nombre original del archivo descargado
                Path nuevoJarPath = Paths.get(tempDir, nombreArchivoRemoto);

                System.out.println("Descargando desde: " + downloadUrl);
                System.out.println("Guardando como: " + nuevoJarPath);
                URL url = URI.create(downloadUrl).toURL();
                Files.copy(url.openStream(), nuevoJarPath, StandardCopyOption.REPLACE_EXISTING);

                System.out.println("Descarga completada en: " + nuevoJarPath);

                crearScriptActualizacion(nuevoJarPath.toAbsolutePath().toString(), 
                                        nuevaVersion, nombreArchivoRemoto);

                SwingUtilities.invokeLater(() -> {
                    progressDialog.dispose();
                    JOptionPane.showMessageDialog(parent,
                            "<html><body>"
                            + "<p>La actualización se completará al reiniciar la aplicación.</p>"
                            + "<p>La aplicación se cerrará ahora.</p>"
                            + "</body></html>",
                            "Actualización lista",
                            JOptionPane.INFORMATION_MESSAGE);

                    System.exit(0);
                });

            } catch (Exception e) {
                System.err.println("Error en descarga: " + e.getMessage());
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    progressDialog.dispose();
                    JOptionPane.showMessageDialog(parent,
                            "Error al descargar la actualización:\n" + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();

        progressDialog.setVisible(true);
    }

    private static void crearScriptActualizacion(String nuevoJarPath, String nuevaVersion, 
                                                  String nombreArchivoRemoto) {
        try {
            String currentDir = System.getProperty("user.dir");
            String scriptPath;
            String comando;
            String repo = ActualizacionConfig.getRepo();
            
            // Determinar el nombre del JAR actual (ejecutable)
            String jarActual = getCurrentJarName();

            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                scriptPath = currentDir + File.separator + "update.bat";
                comando = "@echo off\n"
                        + "timeout /t 2 /nobreak > nul\n"
                        + "echo Actualizando DentalClinicSystem...\n"
                        + "echo Copiando nuevo archivo...\n"
                        + "copy /Y \"" + nuevoJarPath + "\" \"" + currentDir + "\\" + jarActual + "\"\n"
                        + "echo Actualizando version...\n"
                        + "echo app.version=" + nuevaVersion + " > \"" + currentDir + "\\app.properties\"\n"
                        + "echo app.repo=" + repo + " >> \"" + currentDir + "\\app.properties\"\n"
                        + "echo Limpiando archivos temporales...\n"
                        + "del \"" + nuevoJarPath + "\" 2>nul\n"
                        + "echo Actualizacion completada!\n"
                        + "echo Iniciando aplicacion...\n"
                        + "start javaw -jar \"" + currentDir + "\\" + jarActual + "\"\n"
                        + "del \"%~f0\"\n";
            } else {
                scriptPath = currentDir + File.separator + "update.sh";
                comando = "#!/bin/bash\n"
                        + "sleep 2\n"
                        + "echo 'Actualizando DentalClinicSystem...'\n"
                        + "cp \"" + nuevoJarPath + "\" \"" + currentDir + "/" + jarActual + "\"\n"
                        + "echo \"app.version=" + nuevaVersion + "\" > \"" + currentDir + "/app.properties\"\n"
                        + "echo \"app.repo=" + repo + "\" >> \"" + currentDir + "/app.properties\"\n"
                        + "rm \"" + nuevoJarPath + "\"\n"
                        + "echo 'Actualizacion completada!'\n"
                        + "java -jar \"" + currentDir + "/" + jarActual + "\" &\n"
                        + "rm -- \"$0\"\n";
            }

            Files.writeString(Paths.get(scriptPath), comando);
            
            if (!System.getProperty("os.name").toLowerCase().contains("win")) {
                new ProcessBuilder("chmod", "+x", scriptPath).start();
            }
            
            Runtime.getRuntime().exec(scriptPath);
            
        } catch (IOException e) {
            System.err.println("Error al crear script: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static String getCurrentJarName() {
        try {
            String path = ActualizadorApp.class.getProtectionDomain()
                            .getCodeSource()
                            .getLocation()
                            .toURI()
                            .getPath();
            return new File(path).getName();
        } catch (Exception e) {
            System.err.println("Error obteniendo nombre del JAR actual: " + e.getMessage());
            return "DentalClinicSystem.jar"; // Valor por defecto
        }
    }
}