package Model;

import java.util.ArrayList;

public class RegisteredUsers {

    private ArrayList<User> registeredUsers = new ArrayList<>();

    public void addRegisteredUser(User newUser) {
        registeredUsers.add(newUser);
    }

}