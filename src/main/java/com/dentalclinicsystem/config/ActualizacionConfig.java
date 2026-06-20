package com.dentalclinicsystem.config;

import java.io.*;
import java.util.Properties;

public class ActualizacionConfig {

    private static final String CONFIG_FILE = "app.properties";
    private static Properties props = new Properties();

    static {
        cargarConfiguracion();
    }

    private static void cargarConfiguracion() {
        File configFile = new File(CONFIG_FILE);
        if (configFile.exists()) {
            try (FileInputStream fis = new FileInputStream(configFile)) {
                props.load(fis);
                props.setProperty(
                        "app.repo",
                        props.getProperty("app.repo", "").trim()
                );

                props.setProperty(
                        "app.version",
                        props.getProperty("app.version", "").trim()
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            props.setProperty("app.version", "1.0.0");
            props.setProperty("app.repo", "Miller38/dental-clinic-system");
            guardarConfiguracion();
        }
    }

    private static void guardarConfiguracion() {
        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            props.store(fos, "Configuracion de DentalClinicSystem");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getVersion() {
        return props.getProperty("app.version", "1.0.0");
    }

    public static void setVersion(String version) {
        props.setProperty("app.version", version);
        guardarConfiguracion();
    }

    public static String getRepo() {
        return props.getProperty("app.repo", "Miller38/dental-clinic-system");
    }

    public static void setRepo(String repo) {
        props.setProperty("app.repo", repo);
        guardarConfiguracion();
    }
}
