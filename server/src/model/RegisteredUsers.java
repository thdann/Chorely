package model;

import shared.transferable.User;

import java.io.*;
import java.util.logging.Logger;

/**
 * RegisteredUser handles all registered users by reading and writing each User object to a
 * separate file stored on the server.
 * version 2.0 2020-04-08
 *
 * @author Theresa Dannberg
 */
public class RegisteredUsers {
    private final static Logger messagesLogger = Logger.getLogger("messages");
    private final static String filePath = "files/users/";
    private final static RegisteredUsers instance = new RegisteredUsers();

    /**
     * Constructor
     */
    private RegisteredUsers() {
    }

    public static RegisteredUsers getInstance() {
        return instance;
    }

    /**
     * Saves a User object to its own file on the server.
     *
     * @param user the User object to be saved to file
     */
    public synchronized void writeUserToFile(User user) {
        String filename = String.format("%s%s.dat", filePath, user.getUsername());
        try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(filename)))) {
            oos.writeObject(user);
            oos.flush();
            messagesLogger.info("wrote user to file " + filename);
        } catch (IOException e) {
            messagesLogger.info("writeUserToFile(user): " + e.getMessage());
        }
    }

    /**
     * Searches among registered users and returns the requested user if it exists, otherwise return null.
     *
     * @param userToFind the user searched for.
     * @return the requested User-object
     */
    public synchronized User getUserFromFile(User userToFind) {
        String filename = String.format("%s%s.dat", filePath, userToFind.getUsername());
        User foundUser = null;

        try (ObjectInputStream ois = new ObjectInputStream((new BufferedInputStream(new FileInputStream(filename))))) {
            foundUser = (User) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            messagesLogger.info("getUserFromFile(userToFind): " + e.getMessage());
            return null;
        }

        return foundUser;
    }

    /**
     * Updates the directory with the new updated user.
     *
     * @param user is the new updated version of the User object to be saved to file.
     */
    public synchronized void updateUser(User user) {
        File file = new File(filePath + user.getUsername() + ".dat");
        if (file.exists()) {
            file.delete();
        }
        writeUserToFile(user);
    }

    /**
     * Compares the username of a new user to already registered users.
     *
     * @param newUsername the requested username of a new user.
     * @return true if username is available and false if it already taken.
     */
    public synchronized boolean userNameAvailable(String newUsername) {
        File file = new File(filePath + newUsername + ".dat");
        if (file.exists()) {
            return false;
        }
        return true;
    }

    /**
     * Looks for a requested user among the registered users.
     *
     * @param dummyUser the requested user/username to look for
     * @return the requested user if it exists, otherwise return null
     */
    public synchronized User findUser(User dummyUser) {
        User foundUser = null;
        if (userNameAvailable(dummyUser.getUsername())) {
            return null;
        } else {
            foundUser = getUserFromFile(dummyUser);
        }
        return foundUser;
    }
}
