package shared.transferable;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Objects of the User class correspond to a registered end-users of the system.
 */
public class User implements Transferable {
    private final String username;
    private final String password; //todo remove password storing in object, only in database
    private final boolean adult;
    private final ArrayList<GenericID> groups;
    private ArrayList<Group> dbGroups = new ArrayList<>();

    public User(String username, String password) {
        this(username, password, true);
    }
    public User(String username, String password, boolean adult) {
        this(username, password, adult, new ArrayList<GenericID>());
    }
    public User(String username, String password, boolean adult, ArrayList<GenericID> groups) {
        this.username = username;
        this.password = password;
        this.adult = adult;
        this.groups = groups;
    }

    public User(String userName, boolean adult) {
        this(userName, null, adult);
    }

    public ArrayList<GenericID> getGroups() {
        return groups;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void removeGroupMembership(GenericID id) {
        groups.remove(id);
    }

    public void addGroupMembership(GenericID newGroup) {
        if (!groups.contains(newGroup)) {
            groups.add(newGroup);
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
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
            String hashString = username;
            String objHashString = ((User) obj).getUsername();
            return hashString.hashCode() == objHashString.hashCode();
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
}
