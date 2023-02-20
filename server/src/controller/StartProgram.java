package controller;

import model.RegisteredUsers;
import service.*;

import java.io.File;

/**
 * Main method of the server.
 *
 * Sets up logging properties.
 * Creates directories for user and group files.
 * Starts ServerController.
 */
public class StartProgram {
    public static void main(String[] args) {
        File f = new File("logs/logging.properties");
        if (f.exists() && !f.isDirectory()) {
            System.setProperty("java.util.logging.config.file", "logs/logging.properties");
        } else {
            System.err.println("Couldn't load logging properties file. Exiting.");
            System.exit(1);
        }

        // If these directories already exist, mkdir() will return false and nothing will happen.
        // Therefore not necessary to handle the return values of mkdir().
        File files = new File("files");
        File users = new File("files/users");
        File groups = new File("files/groups");
        files.mkdir();
        users.mkdir();
        groups.mkdir();

        // If the creation of the necessary directories failed for any reason, the server should
        // not be started since normal operation is impossible.
        if (!files.exists() || !users.exists() || !groups.exists()) {
            System.err.println("Couldn't find necessary files directories.");
            System.exit(1);
        }

        ServerController prog = new ServerController(6583);
        //setup database connection
        DatabaseConnection databaseConnection = new DatabaseConnection("Chorely");
        //link to query executer
        QueryExecutor queryExecutor = new QueryExecutor(databaseConnection);
        //link to model classes
        UserQueries userQueries = new UserQueries(queryExecutor);
        GroupQueries groupQueries = new GroupQueries(queryExecutor);
        ChoreRewardQueries choreRewardQueries = new ChoreRewardQueries(queryExecutor);
        RegisteredUsers.getInstance().setQueryPerformers(userQueries, groupQueries, choreRewardQueries);
    }
}
