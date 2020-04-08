package model;

import shared.transferable.User;

import java.io.*;

/**
 * RegisteredUser contains all the registered users by reading and writing each User object to a
 * separate file stored on the server.
 * version 1.0 2020-04-06
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
     * Saves the User object to a separate file on the server
     *
     * @param user
     */

    public void writeUserToFile(User user) {
        String filename1 = String.format("%s%s.dat", filePath, user.getUsername());
        try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(filename1)))) {
            oos.writeObject(user);
            oos.flush();
            System.out.println(filename1);

        } catch (IOException e) {
            e.getMessage();

        }

    }

    public void updateUser(User user) {
        File file = new File(filePath + user.getUsername() + ".dat");
        System.out.println(file.getPath());
        if (file.exists()) {
            file.delete();
        }


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

    public void loopRegisteredUsers(File dir) {

        if (dir.isDirectory()) {
            for (File file1 : dir.listFiles()) {
                System.out.println(file1);
            }
        }

    }


    //TODO För testning endast, ta bort sen...
    public static void main(String[] args) {
        RegisteredUsers prog = new RegisteredUsers();
        prog.updateUser(new User("Theresa", "pass"));


//        File file = new File("files/users/");
//        prog.loopRegisteredUsers(file);


    }


}
