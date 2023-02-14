package service;

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
        String dbServerIp = "localhost";
        String dbServerPort = "1433";
        String dbUser = "sven";
        String dbPassword = "Welovesven";
        //can be different sql driver
        DriverManager.registerDriver(new com.microsoft.sqlserver.jdbc.SQLServerDriver());

//        if (InetAddress.getLocalHost().getHostName().equals(PasswordsAndKeys.dbHostName)) {
//            dbServerIp = "localhost";
//        }
        String dbURL = String.format("jdbc:sqlserver://%s:%s;databaseName=" + databaseName + ";integratedSecurity=true", dbServerIp, dbServerPort);
        //String dbURL = String.format("jdbc:sqlserver://%s:%s;databaseName=" + databaseName + ";user=%s;password=%s", dbServerIp, dbServerPort, dbUser, dbPassword);
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
        databaseConnection.getConnection();
        databaseConnection.closeConnection();
    }
}
