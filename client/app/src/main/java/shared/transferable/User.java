package shared.transferable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

/**
 * Objects of the User class correspond to a registered end-users of the system.
 */
public class User implements Transferable {
    private final String username;
    private String password; //todo remove password storing in object, only in database
    private boolean adult;
    private ArrayList<Group> dbGroups = new ArrayList<>();

    public User(String username) {
        this.username = username;
    }

    public User(String username, boolean adult) {
        this(username);
        this.adult = adult;
    }
    public User(String username, String password) {
        this(username);
        this.password =  password;
    }

    public User(String username, boolean adult, ArrayList<Group> groups) {
        this(username, adult);
        this.dbGroups = groups;
    }

    public User(String username, String password, boolean adult) {
        this(username, password);
        this.adult = adult;
    }

    public ArrayList<Group> getGroups() {
        return dbGroups;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void removeGroupMembership(Group group) {
        dbGroups.remove(group);
    }

    public void addGroupMembership(Group newGroup) {
        if (!dbGroups.contains(newGroup)) {
            dbGroups.add(newGroup);
        }
    }
    @Override
    public String toString() {
        String isAdult = "";
        if(isAdult()) {
            isAdult = ", adult";
        }
        return "User{" +
                "username='" + username + "'" + isAdult
                + "}";
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof User)) {
            return false;
        } else {
            return username.equals(((User) obj).getUsername());
        }
    }


    public boolean compareUsernamePassword(User otherUser) {
        String otherPassword = otherUser.getPassword();
        String otherUsername = otherUser.getUsername();
        boolean isEqual = username.equals(otherUsername) && password.equals(otherPassword);
        return isEqual;
    }

    public boolean isAdult() {
        return adult;
    }

    public ArrayList<Group> getDbGroups() {
        return dbGroups;
    }
    public boolean setDBGroups (ArrayList<Group> groups) {
        boolean success = false;
        dbGroups = groups;
        success = true;
        return success;
    }

    public void setAdult(boolean isAdult) {
        this.adult = isAdult;
    }
}
