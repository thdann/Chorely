package model;

import service.*;
import shared.transferable.User;


/**
 * RegisteredUser handles all registered users by reading and writing each User object to a
 * separate file stored on the server.
 *
 * @author Theresa Dannberg, Fredrik Jeppsson
 */
public class RegisteredUsers {
    private final static RegisteredUsers instance = new RegisteredUsers();
    UserQueries userQueries;
    GroupQueries groupQueries;
    ChoreRewardQueries choreRewardQueries;
    LeaderboardQueries leaderboardQueries;

    public RegisteredUsers() {
    }

    /**
     * @return the singleton instance of RegisteredUsers.
     */
    public static RegisteredUsers getInstance() {
        return instance;
    }

    /**
     * Saves a User object to its own file on the server.
     *
     * @param user the User object to be saved to file
     * @return true upon success
     */
    public synchronized boolean writeUserToFile(User user) {
        boolean success = false;
        if (user != null && userQueries.registerUser(user.getUsername(), user.getPassword(), user.isAdult())) {
            success = true;
        }
        return success;
    }

    /**
     * Searches among registered users and returns the requested user if it exists, otherwise return null.
     *
     * @param userToFind the user searched for.
     * @return the requested User-object
     */
    public synchronized User getUserFromFile(User userToFind) {
        User gotUser = null;
        if (userToFind!=null) {
            gotUser = userQueries.getUserInfo(userToFind.getUsername());
        }
        return gotUser;

    }
    public synchronized boolean checkPassword(String username, String password) {
        boolean verified = false;
        if (username!=null && password!=null) {
            verified = userQueries.checkPassword(username, password);
        }
        return verified;
    }

    /**
     * Looks for a requested user among the registered users.
     *
     * @param dummyUser the requested user/username to look for
     * @return the requested user if it exists, otherwise return null
     */
    public synchronized User findUser(User dummyUser) {
        return getUserFromFile(dummyUser);
    }

    /**
     * Compares the username of a new user to already registered users.
     * todo method unnecessary as registerUser need to do the same thing?
     * @param newUsername the requested username of a new user.
     * @return true if username is available and false if it is already taken.
     */
    public boolean userNameAvailable(String newUsername) {
        boolean nameAvailable = false;
        if (newUsername != null && userQueries.getUserInfo(newUsername) == null) {
            nameAvailable = true;
        }
        return nameAvailable;
    }

    public void setQueryPerformers(QueryExecutor queryExecutor) {
        this.userQueries = queryExecutor.getUserQueries();
        this.groupQueries = queryExecutor.getGroupQueries();
        this.choreRewardQueries = queryExecutor.getChoreRewardQueries();
        this.leaderboardQueries = queryExecutor.getLeaderboardQueries();
    }

    public User loginUser(User user) {
        User loggedInUser = userQueries.loginUser(user.getUsername(), user.getPassword());
        return loggedInUser;
    }
}
