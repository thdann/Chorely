package shared.transferable;


import java.util.ArrayList;

public class User implements Transferable {
    private final String username;
    private final String password;
    private ArrayList<GenericID> groups;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        groups = new ArrayList<GenericID>();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void addGroupMembership(GenericID newGroup) {
        groups.add(newGroup);
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
            String hashString = username + password;
            String objHashString = ((User) obj).getUsername() + ((User) obj).getPassword();
            return hashString.hashCode() == objHashString.hashCode();
        }
    }

}
