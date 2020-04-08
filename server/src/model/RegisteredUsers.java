package model;

import shared.transferable.User;

import java.io.*;
import java.util.ArrayList;

/**
 * RegisteredUser contains all the registered users by reading and writing to a file stored on the server.
 * version 1.0 2020-04-06
 *
 * @author Theresa Dannberg
 */

public class RegisteredUsers {

    private String filename;
    private ArrayList<User> registeredUsers;

    /**
     * The constructor sets the path to the file with registered users.
     * If the file doesn´t already exist it creates it.
     */

    public RegisteredUsers() {
        this.filename = "files/registeredUsers.dat";

        File savedUsers = new File(filename);
        if (savedUsers.exists()) {
            readUsersFromFile();

        } else {
            registeredUsers = new ArrayList<>();
            writeUsersToFile();

        }

    }


    public void addTestUsers() {
        registeredUsers.clear();
        registeredUsers.add(new User("Theresa", "mittPassword"));
        registeredUsers.add(new User("Emma", "emmasPassword"));
        registeredUsers.add(new User("Angelica", "angelicasPassword"));
        registeredUsers.add(new User("Tim", "timsPassword"));
        registeredUsers.add(new User("Fredrik", "fredrikPassword"));
        System.out.printf("När det tillagt %d användare", registeredUsers.size());

    }

    public void printUsers() {
        if (registeredUsers.size() == 0) {
            System.out.println("Listan är tom på användare");
        } else {
            for (User u : registeredUsers) {
                System.out.printf("Username: %s \tPassword: %s\n", u.getUsername(), u.getPassword());
            }

        }

    }

    /**
     * TODO: no need for comment?
     */

    public void addRegisteredUser(User newUser) {
        registeredUsers.add(newUser);

    }


    /**
     * TODO: no need for comment?
     */

    public void readUsersFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(filename)))) {
            registeredUsers = (ArrayList<User>) ois.readObject();

        } catch (IOException | ClassNotFoundException e) {
            e.getMessage();

        }

    }

    /**
     * TODO: no need for comment?
     */

    public void writeUsersToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(filename)))) {
            oos.writeObject(registeredUsers);
            oos.flush();

        } catch (IOException e) {
            e.getMessage();

        }

    }

    /**
     * Compares the username of a new user to already registered users.
     * @param newUsername the requested username of a new user.
     * @return true if username is available and false if it already taken.
     */

    public boolean userNameAvailable(String newUsername) {
        for (User u : registeredUsers) {
            if (u.getUsername().equals(newUsername)) {
                return false;
            }
        }
        return true;

    }


    public static void main(String[] args) {
        RegisteredUsers test = new RegisteredUsers();
//        test.printUsers();
//        test.readUsersFromFile();
        test.addTestUsers();
        test.writeUsersToFile();
        test.readUsersFromFile();
//        test.printUsers();
//        System.out.println(test.checkIfExistingUser("Theresa"));
//        System.out.println(test.checkIfExistingUser("Therese"));

    }

}
