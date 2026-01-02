package io.neskdev.api.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ServerInfo {

    private static String serverName = "global";

    public static void loadServerName() {
        File configFile = new File("server.properties");

        try (FileInputStream fis = new FileInputStream(configFile)) {
            Properties properties = new Properties();
            properties.load(fis);

            if (properties.containsKey("server-name")) {
                serverName = properties.getProperty("server-name");
            } else {
                System.out.println("Clé 'server-name' manquante dans server.properties. Utilisation de la valeur par défaut : Global");
                serverName = "Global";
                properties.setProperty("server-name", serverName);

                try (FileOutputStream fos = new FileOutputStream(configFile)) {
                    properties.store(fos, null);
                    System.out.println("Clé 'server-name' ajoutée au fichier avec la valeur par défaut.");
                }
            }
        } catch (IOException e) {
            e.getMessage();
        }
    }

    /**
     * Get the server name from the server.properties file.
     * @return
     */

    public static String getServerName() {
        loadServerName();
        return serverName;
    }
}