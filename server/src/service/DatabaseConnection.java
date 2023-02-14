package service;

import shared.transferable.User;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private java.sql.Connection conn;
    private String databaseName;

    public DatabaseConnection(String databaseName) {
        this.databaseName = databaseName;
    }

    private java.sql.Connection createConnection() throws SQLException, UnknownHostException {
        String dbServerIp = "127.0.0.1";
        String dbServerPort = "1433"; //default port for mssql
        String dbUser = "chorely-admin";
        String dbPassword = "Welovesven";
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
        UserRepository userRepository = new UserRepository(queryExecutor);
//        userRepository.registerUser(new User("Chris", "bajskorv"));
        boolean loginSuccess = userRepository.checkLogin("Chris", "bajskorv");
        if (loginSuccess) System.out.println("Login success");
        boolean deleteSuccess = userRepository.deleteAccount("Chris", "bajskorv");
        if (deleteSuccess) System.out.println("Successfully deleted");

    }
}
