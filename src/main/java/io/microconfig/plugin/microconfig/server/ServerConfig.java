package io.microconfig.plugin.microconfig.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ServerConfig {
    private final Properties config = new Properties();

    //todo use IJ plugin config
    //see https://github.com/mplushnikov/lombok-intellij-plugin/tree/master/src/main/java/de/plushnikov/intellij/plugin/settings
    public ServerConfig() {
        try {
            String path = System.getProperty("user.home") + "/.microconfig/config.properties";
            File configFile = new File(path);
            config.load(new FileInputStream(configFile));
        } catch (IOException e) {
            //ignored
        }
    }

    public String vaultToken() {
        return config.getProperty("vault.token");
    }

    public String serverUrl() {
        return config.getProperty("server.url");
    }
}
