package model;

import shared.transferable.User;

import java.io.*;

/**
 * RegisteredUser handles all registered users by reading and writing each User object to a
 * separate file stored on the server.
 * version 2.0 2020-04-08
 *
 * @author Theresa Dannberg
 */

public class RegisteredUsers {

    private String filePath;

    /**
     * Constructor
     */

    public RegisteredUsers() {
        this.filePath = "files/users/";

    }

    /**
     * Saves a User object to its own file on the server.
     *
     * @param user the User object to be saved to file
     */

    public void writeUserToFile(User user) {
        String filename = String.format("%s%s.dat", filePath, user.getUsername());
        try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(filename)))) {
            oos.writeObject(user);
            oos.flush();
            System.out.println("write user to file " + filename);

        } catch (IOException e) {
            e.getMessage();

        }

    }

    /**
     * Updates the directory with the new updated user.
     *
     * @param user is the new updated version of the User object to be saved to file.
     */

    public void updateUser(User user) {
        File file = new File(filePath + user.getUsername() + ".dat");
        System.out.println("updatemetoden " + file.getPath());
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

    public boolean userNameAvailable(String newUsername) {
        File file = new File(filePath + newUsername + ".dat");
        System.out.println(file.getPath());
        if (file.exists()) {
            return false;
        }

        return true;

    }

    /**
     * Looks for a requested user among the registered users.
     * @param user the requested user/username to look for
     * @return the requested user if it exists, otherwise return null
     */
    
    public User findUser(User user) {
        if (userNameAvailable(user.getUsername())) {
            return null;
        }
        return user;
    }

}
