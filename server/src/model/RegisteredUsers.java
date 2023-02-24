package model;

import service.*;
import shared.transferable.Group;
import shared.transferable.User;

import java.io.*;
import java.util.logging.Logger;

/**
 * RegisteredUser handles all registered users by reading and writing each User object to a
 * separate file stored on the server.
 *
 * @author Theresa Dannberg, Fredrik Jeppsson
 */
public class RegisteredUsers {
    private final static Logger messagesLogger = Logger.getLogger("messages");
    private final static String filePath = "files/users/";
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

//    /**
//     * Saves a User object to its own file on the server.
//     *
//     * @param user the User object to be saved to file
//     * @return
//     */
//    public synchronized int writeUserToFile(User user) {
//        String filename = String.format("%s%s.dat", filePath, user.getUsername());
//        try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(filename)))) {
//            oos.writeObject(user);
//            oos.flush();
//            messagesLogger.info("wrote user to file " + filename);
//            return 1;
//        } catch (IOException e) {
//            messagesLogger.info("writeUserToFile(user): " + e.getMessage());
//            return 0;
//        }
//    }
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
//    public synchronized User getUserFromFile(User userToFind) {
//        String filename = String.format("%s%s.dat", filePath, userToFind.getUsername());
//        User foundUser = null;
//
//        try (ObjectInputStream ois = new ObjectInputStream((new BufferedInputStream(new FileInputStream(filename))))) {
//            foundUser = (User) ois.readObject();
//        } catch (IOException | ClassNotFoundException e) {
//            messagesLogger.info("getUserFromFile(userToFind): " + e.getMessage());
//            return null;
//        }
//
//        return foundUser;
//    }
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

//    /**
//     * Looks for a requested user among the registered users.
//     *
//     * @param dummyUser the requested user/username to look for
//     * @return the requested user if it exists, otherwise return null
//     */
//    public synchronized User findUser(User dummyUser) {
//        User foundUser = null;
//        if (userNameAvailable(dummyUser.getUsername())) {
//            return null;
//        } else {
//            foundUser = getUserFromFile(dummyUser);
//        }
//        return foundUser;
//    }
    /**
     * Looks for a requested user among the registered users.
     *
     * @param dummyUser the requested user/username to look for
     * @return the requested user if it exists, otherwise return null
     */
    public synchronized User findUser(User dummyUser) {
        return getUserFromFile(dummyUser);
    }
//    /**
//     * Updates the directory with the new updated user.
//     *
//     * @param user is the new updated version of the User object to be saved to file.
//     */
//    public synchronized void updateUser(User user) {
//        File file = new File(filePath + user.getUsername() + ".dat");
//        if (file.exists()) {
//            file.delete();
//        }
//        writeUserToFile(user);
//    }
//    /**
//     * Copy of previous method, deletes and re/registers user
//     * todo bad method, remove and (if needed) replace with update method
//     *
//     * @param user is the new updated version of the User object to be saved to file.
//     */
//    public synchronized void updateUser(User user) {
//        userQueries.deleteAccount(user, user.getPassword());
//        userQueries.registerUser(user.getUsername(), user.getPassword(), true);
//
//    }
//    /**
//     * Compares the username of a new user to already registered users.
//     *
//     * @param newUsername the requested username of a new user.
//     * @return true if username is available and false if it is already taken.
//     */
//    public synchronized boolean userNameAvailable(String newUsername) {
//        File file = new File(filePath + newUsername + ".dat");
//        if (file.exists()) {
//            return false;
//        }
//        return true;
//    }

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
        System.out.println("QP set");
        this.userQueries = queryExecutor.getUserQueries();
        this.groupQueries = queryExecutor.getGroupQueries();
        this.choreRewardQueries = queryExecutor.getChoreRewardQueries();
        this.leaderboardQueries = queryExecutor.getLeaderboardQueries();
    }
}
