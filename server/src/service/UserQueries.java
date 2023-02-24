package service;

import org.mindrot.jbcrypt.BCrypt;
import shared.transferable.Group;
import shared.transferable.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Class responsible for calling the database about users.

 */
public class UserQueries {

    private QueryExecutor queryExecutor;
    GroupQueries groupQueries;
    LeaderboardQueries leaderboardQueries;
    ChoreRewardQueries choreRewardQueries;

    public UserQueries(QueryExecutor queryExecutor){
        System.out.println("build UQ");
       this.queryExecutor = queryExecutor;
        leaderboardQueries = queryExecutor.getLeaderboardQueries();
        choreRewardQueries = queryExecutor.getChoreRewardQueries();
    }

    /**
     * Method to save a new user and a hashed password using BCrypt.
     * @param userName and password from login message received from client
     * @return A boolean value, true if the user is created, false if user already exists or something else went wrong
     */
    public boolean registerUser(String userName, String password, boolean adult) {
        boolean success = false;
        //Hash password
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        String sqlSafeUsername = makeSqlSafe(userName);
        int isAdult = 1;
        if (!adult) {
            isAdult = 0;
        }
        String query = "INSERT INTO [User] VALUES ('" + sqlSafeUsername + "', '" + hashedPassword + "', " + isAdult + ")";
        try {
            queryExecutor.executeUpdateQuery(query);
            success = true;
        }
        catch (SQLException sqlException) {
            //todo will fail if duplicate username found -> throw appropriate error message
            sqlException.printStackTrace();
        }
        return success;
    }
    /**
     * Method to retrieve a users info
     *
     * @param userName and password from login message received from client
     * @return A user object containing their username, whether they are an adult, and a list of the groups they are a member of
     */
    public User loginUser(String userName, String password) {
        User loggedInUser = null;
        if (checkPassword(userName, password)) {
            String sqlSafeUsername = makeSqlSafe(userName);
            //get users basic info
            loggedInUser = getUserInfo(sqlSafeUsername);
            //get groups user is member of
            ArrayList<Group> groups = groupQueries.getGrouplist(sqlSafeUsername);
            loggedInUser.setDBGroups(groups);
        }
        return loggedInUser;
    }

    /**
     * Finds and retrieves a user's details from the database
     *
     * @param userName username to be found
     * @return the found user
     */
    public User getUserInfo(String userName) {
        String sqlSafeUsername = makeSqlSafe(userName);
        boolean adult = false;
        User gotUser = null;
        String query = "SELECT * FROM [User] WHERE user_name = '" + sqlSafeUsername + "';";
        try {
            ResultSet resultSet = queryExecutor.executeReadQuery(query);
            if (resultSet.next()) {
                gotUser = new User(sqlSafeUsername);
                int adultInt = resultSet.getInt("is_adult");
                    if(adultInt == 1) gotUser.setAdult(true);
                //get groups user is member of
                ArrayList<Group> groups = groupQueries.getGrouplist(sqlSafeUsername);
                gotUser.setDBGroups(groups);
            }
            else {
                System.out.println("User: " + userName + " not found");
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return gotUser;
    }
    /**
     * Method to delete a user --no requirement but useful for testing
     *
     * @param userToDelete object to be deleted, password for authorisation
     * @return boolean with success outcome
     */
    public boolean deleteAccount(User userToDelete, String password) {
        boolean accountDeleted = false;
        String sqlSafeUsername = makeSqlSafe(userToDelete.getUsername());
        if (checkPassword(userToDelete.getUsername(), password)) {
            try {
                Statement statement = queryExecutor.beginTransaction();
                String queryDeleteUser =
                        //remove chores from owned groups
                        "DELETE FROM [Chore] WHERE group_id = (SELECT group_id FROM [Group] WHERE group_owner = '" + sqlSafeUsername + "');" +
                        //remove rewards from owned groups
                        "DELETE FROM [Reward] WHERE group_id = (SELECT group_id FROM [Group] WHERE group_owner = '" + sqlSafeUsername + "');" +
                        //remove members from owned groups
                       "DELETE FROM [Member] WHERE group_id = (SELECT group_id FROM [Group] WHERE group_owner = '" + sqlSafeUsername + "');" +
                        //remove memberships
                       "DELETE FROM [Member] WHERE user_name = '" + sqlSafeUsername + "';" +
                        //remove owned groups
                        "DELETE FROM [Group] WHERE group_owner = '" + sqlSafeUsername + "';" +
                        //remove user
                        "DELETE FROM [User] WHERE user_name = '" + sqlSafeUsername + "';";
                System.out.println(queryDeleteUser);
                statement.executeUpdate(queryDeleteUser);
                queryExecutor.endTransaction();
                accountDeleted = true;
            }
            catch (SQLException sqlException) {
                try {
                    System.out.println(sqlException);
                   queryExecutor.rollbackTransaction();
                }
                catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
        return accountDeleted;
    }

    /**
     * Method to check if a user exists in database.
     *
     * @param password typed password from client and the application
     * @return A boolean value, true if the user exist in database and the password is correct
     */
    public boolean checkPassword(String username, String password) {
        boolean isVerified = false;
        //simple (but not secure) method to clean sql input
        String sqlSafeUsername = makeSqlSafe(username);
        String query = "SELECT user_password FROM [User] WHERE user_name = '" + sqlSafeUsername + "';";
        try {
            ResultSet resultSet = queryExecutor.executeReadQuery(query);
            if (resultSet.next()) {
                String hashedPassword = resultSet.getString(1);
                isVerified = BCrypt.checkpw(password, hashedPassword);
            }
        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        System.out.println(isVerified);
        return isVerified;
    }




    private static String makeSqlSafe(String string) {
        //simple (but not secure) method to clean sql input
        return string.replace("'", "''");
    }

    public void setGroupQueries(GroupQueries groupQueries) {
        this.groupQueries = groupQueries;
    }
}

