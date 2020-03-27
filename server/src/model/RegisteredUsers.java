package model;



import shared.transferable.User;

import java.util.ArrayList;

public class RegisteredUsers {

    private ArrayList<User> registeredUsers = new ArrayList<>();

    public void addRegisteredUser(User newUser) {
        registeredUsers.add(newUser);
    }

}
