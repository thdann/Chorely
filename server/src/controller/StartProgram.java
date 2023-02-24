package controller;

import model.RegisteredGroups;
import model.RegisteredUsers;
import service.*;

/**
 * Main method of the server.
 *
 */
public class StartProgram {
    public static void main(String[] args) {
        //start server
        new ServerController(6583);
        //setup database connection
        DatabaseConnection databaseConnection = new DatabaseConnection("Chorely");
        //link to query executor
        QueryExecutor queryExecutor = new QueryExecutor(databaseConnection);
        //link to model classes
        RegisteredUsers.getInstance().setQueryPerformers(queryExecutor);
        RegisteredGroups.getInstance().setQueryPerformers(queryExecutor);
    }
}
