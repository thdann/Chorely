package model;

import shared.transferable.User;

import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class RegisteredUsers {

    private String filename;
    private OutputStream oos;
    private ObjectInputStream ois;

    private ArrayList<User> registeredUsers = new ArrayList<>();

    public RegisteredUsers(String filename) {
        this.filename = filename;
    }

    public void addRegisteredUser(User newUser) {
        registeredUsers.add(newUser);
    }

    public void setRegisteredUsers(ArrayList<User> registeredUsers) {
        this.registeredUsers = registeredUsers;
    }

    public ArrayList<User> getRegisteredUsers() {
        return registeredUsers;
    }
}
