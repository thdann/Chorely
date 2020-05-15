package controller;

import java.io.File;

public class StartProgram {
    public static void main(String[] args) {
        File f = new File("logs/logging.properties");
        if (f.exists() && !f.isDirectory()) {
            System.setProperty("java.util.logging.config.file", "logs/logging.properties");
        } else {
            System.err.println("Couldn't load logging properties file. Exiting.");
            System.exit(0);
        }

        ServerController prog = new ServerController(6583);
    }
}
