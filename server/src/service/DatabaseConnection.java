package service;

import shared.transferable.Chore;
import shared.transferable.Group;
import shared.transferable.User;

import java.net.UnknownHostException;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private java.sql.Connection conn;
    private String databaseName;
    QueryExecutor queryExecutor;

    public DatabaseConnection(String databaseName) {
        this.databaseName = databaseName;
        queryExecutor = new QueryExecutor(this);
    }

    private java.sql.Connection createConnection() throws SQLException, UnknownHostException {
        String dbServerIp = ServerAccess.dbServerIp;
        String dbServerPort = ServerAccess.dbServerPort; //default port for mssql
        String dbUser = ServerAccess.dbUser;
        String dbPassword =ServerAccess.dbPassword;
        //can be different sql driver
        DriverManager.registerDriver(new com.microsoft.sqlserver.jdbc.SQLServerDriver());
//        //if logging into database both in local network and remotely, specify localhost when at home
//        if (InetAddress.getLocalHost().getHostName().equals(PasswordsAndKeys.dbHostName)) {
//            dbServerIp = "localhost";
//        }
        String dbURL = String.format("jdbc:sqlserver://%s:%s;databaseName=" + databaseName + ";TrustServerCertificate=True;user=%s;password=%s", dbServerIp, dbServerPort, dbUser, dbPassword);
        this.conn = DriverManager.getConnection(dbURL);
        return conn;
    }

    public java.sql.Connection getConnection() {
        if(conn==null) {
            try {
                conn = createConnection();

                System.out.println("Connection successful");
            }
            catch (UnknownHostException e) {
                e.printStackTrace();
            }
            catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
        }
        return conn;
    }

    public void closeConnection() {
        try {
            conn.close();
            System.out.println("Connection closed");
        } catch (SQLException sqlException) {
            //do nothing when this occurs, we don't care about this exception
        }
        conn = null;
    }

    public static void main(String[] args) {
        DatabaseConnection databaseConnection = new DatabaseConnection("Chorely");
        QueryExecutor queryExecutor = new QueryExecutor(databaseConnection);
        UserQueries userQueries = new UserQueries(queryExecutor);
        ChoreRewardQueries choreRewardQueries = new ChoreRewardQueries(queryExecutor);
        GroupQueries groupQueries = new GroupQueries(queryExecutor);
//        boolean registerSuccess = userQueries.registerUser("Bilbo", "mypassword", true);
//        if (registerSuccess) System.out.println("Registration success");
//        User loggedInUser = userQueries.loginUser("Bilbo", "mypassword");
//        assert loggedInUser != null;
//        boolean deleteSuccess = userQueries.deleteAccount(loggedInUser, "mypassword");
//        if (deleteSuccess) System.out.println("Successfully deleted");
//        System.out.println(newGroup.getIntGroupID() +", "+ newGroup.getName() +", "+ newGroup.getDescription());

//        if (loggedInUser!=null) {
//            System.out.println("Login success");
//            System.out.println("member of group: " + loggedInUser.getDbGroups());
//        }
//        Chore testChore = new Chore("Chore1", 200, "This is a CHORE", 3);
//        choreRewardQueries.createChore(testChore);
//        testChore.setLastDoneByUser("Bilbo");
//        choreRewardQueries.updateChore(testChore);
//        choreRewardQueries.deleteChore(testChore);

//        Group newGroup = groupQueries.createGroup("Bilbo","Bilbos group","Welcome to Bilbo");
        Group group = new Group(4);
        group.setOwner("Kinda");
        Group bigGroup = groupQueries.addMember("Bilbo", new Group(4));
        System.out.println(bigGroup.getMembers());


        databaseConnection.closeConnection();
    }
}
