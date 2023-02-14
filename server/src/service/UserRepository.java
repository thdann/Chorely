package service;

import org.mindrot.jbcrypt.BCrypt;
import shared.transferable.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Class responsible for calling the database about users.

 */
public class UserRepository {

    private QueryExecutor database;

    public UserRepository(QueryExecutor database){
       this.database = database;
    }

    /**
     * Method to save a new user using BCrypt.
     *
     * @param user An instance of a newly created User that should be stored in the database.
     * @return A boolean value, true if the user was stored successfully
     */
    public boolean registerUser(User user) {
        boolean success = false;
        int isAdult = 0;
        if (user.isAdult()) {
            isAdult = 1;
        }
        //Hash password
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        //simple (but not secure) method to clean sql input
        String sqlSafeUsername = user.getUsername().replace("'", "''");
        String query = "INSERT INTO [User] VALUES ('" + sqlSafeUsername + "', '" + hashedPassword + "',)";
        try {
            database.executeUpdate(query);
            success = true;
        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return success;
    }

    /**
     * Method to check if a user exists in database.
     *
     * @param password typed password from client and the application
     * @return A boolean value, true if the user exist in database and the password is correct
     */
    public boolean checkLogin(String username, String password) {
        boolean isVerified = false;
        //simple (but not secure) method to clean sql input
        String sqlSafeUsername = username.replace("'", "''");
        String query = "SELECT password FROM [User] WHERE user_name = '" + sqlSafeUsername + "';";
        try {
            ResultSet resultSet = database.executeQuery(query);
            if (resultSet.next()) {
                String hashedPassword = resultSet.getString(1);
                isVerified = BCrypt.checkpw(password, hashedPassword);
            }
        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return isVerified;
    }

    /**
     * Method to delete a user
     *
     * @return boolean value, false if transaction is rolled back
     * @throws SQLException
     */
    public boolean deleteAccount(String username, String password) {
        boolean accountDeleted = false;
        if (checkLogin(username, password)) {
            try {
                Statement statement = database.beginTransaction();
                String queryDeleteUser = "DELETE FROM [User] WHERE user_name = " + username + ";";
                statement.executeUpdate(queryDeleteUser);
                database.endTransaction();
                accountDeleted = true;
            }
            catch (SQLException sqlException) {
                try {
                   database.rollbackTransaction();
                }
                catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
        return accountDeleted;
    }


}

