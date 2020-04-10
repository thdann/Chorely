package shared.transferable;

/**
 * Group is a class that represent a group in the application.
 * version 2.0 2020-04-08
 *
 * @autor Timothy Denison and Emma Svensson.
 */

import java.util.ArrayList;

public class Group implements Transferable {
    private String name;
    private String description;
    private ArrayList<User> users = new ArrayList<>();
    private GenericID groupID = new GenericID();
    private ArrayList<Chore> chores = new ArrayList<>();
    private ArrayList<Reward> rewards = new ArrayList<>();


    private Group() {
    }

    public Group(String groupName) {
        this.name = groupName;
    }

    public Group(String groupName, String description) {
        this.name = groupName;
        this.description = description;
    }

    public Group(String groupName, User user) {
        this.name = groupName;
        users.add(user);
    }

    public String getDescription() {
        return description;
    }

    public GenericID getGroupID() {
        return groupID;
    }

    public String getName() {
        return name;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void addUser(User user) {
        users.add(user);
    }

    public void deleteUser(User user) {
        users.remove(user);
    }

    public void addChore(Chore chore) {
        chores.add(chore);
    }

    public void deleteChore(Chore chore) {
        chores.remove(chore);
    }

    public Chore getSingleChore(int index) {
        return chores.get(index);
    }

    public ArrayList<Chore> getChores() {
        return chores;
    }

    public void addReward(Reward reward) {
        rewards.add(reward);
    }

    public void deleteReward(Reward reward) {
        rewards.remove(reward);
    }

    public Reward getSingleReward(int index) {
        return rewards.get(index);
    }

    public ArrayList<Reward> getRewards() {
        return rewards;
    }

    public int size() {
        return users.size();
    }

    public boolean allIsEqual(Group group) {
        if (group.getRewards().size() == rewards.size() && group.getChores().size() == chores.size()
                && group.getUsers().size() == users.size()) {
            for (int i = 0; i < chores.size(); i++) {
                if (!group.getChores().get(i).equals(chores.get(i))) {
                    return false;
                }
            }

            for (int i = 0; i < rewards.size(); i++) {
                if(!group.getRewards().get(i).equals(rewards.get(i))){
                    return false;
                }
            }
            for(int i = 0; i< users.size(); i++){
                if(!group.getUsers().get(i).equals(users.get(i))){
                    return false;
                }
            }
            //If all of the above are true, the rest of the data is checked to be equal, and the
            //result is returned.
            return (group.getGroupID().equals(groupID) && group.getDescription().equals(description) &&
                    group.getName().equals(name));
        } else
            return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Group)
            return groupID.equals(((Group) obj).getGroupID());

        else
            return false;
    }

    @Override
    public int hashCode() {
        return groupID.hashCode();
    }
}
