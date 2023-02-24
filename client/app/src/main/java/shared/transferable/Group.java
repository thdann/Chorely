package shared.transferable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Group is a class that represent a group in the application.
 *
 * @author Timothy Denison and Emma Svensson, Angelica Asplund, Fredrik Jeppsson
 */
public class Group implements Transferable {
    private String owner;
    private String name;
    private String description;
    private ArrayList<User> members = new ArrayList<>(); //change to usernames to prevent looping?
    private int intGroupID;
    private ArrayList<Chore> chores = new ArrayList<>();
    private ArrayList<Reward> rewards = new ArrayList<>();
    private HashMap<User, Integer> leaderBoard = new HashMap<>(); //change to String?

    public Group() {
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
        addMember(user);
    }

    public Group(Group group) {
        this.name = group.name;
        this.description = group.description;
        this.members = new ArrayList<>();
        this.members.addAll(group.members);
        assert group.getIntGroupID() != 0;
        this.intGroupID = group.intGroupID;
        this.chores = new ArrayList<>();
        this.chores.addAll(group.chores);
        this.rewards = new ArrayList<>();
        this.rewards.addAll(group.rewards);
        this.leaderBoard = new HashMap<>();
        this.leaderBoard.putAll(group.leaderBoard);
    }

    public Group(int intGroupID, String name, String description) {
        assert intGroupID != 0;
        this.intGroupID = intGroupID;
        this.name = name;
        this.description = description;
    }

    public Group(int intGroupID, String groupName, String groupOwner, String groupDesc) {
        assert intGroupID != 0;
        this.intGroupID = intGroupID;
        this.owner = groupOwner;
        this.name = groupName;
        this.description = groupDesc;
    }

    public Group(int intGroupID) {
        assert intGroupID != 0;
        this.intGroupID = intGroupID;
    }

    public String getDescription() {
        return description;
    }

    public int getGroupID() {
        return intGroupID;
    }

    public String getName() {
        return name;
    }

    public ArrayList<User> getUsers() {
        return members;
    }

    public void addMember(User user) {
        members.add(user);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void deleteUser(User user) {
        members.remove(user);
    }

    public void deleteAllUsers() {
        members = new ArrayList<>();
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
        return members.size();
    }

    public void modifyUserPoints(User user, int incomingPoints) {

        if (leaderBoard.get(user) != null) {
            int tempPoints = leaderBoard.get(user);
            tempPoints += incomingPoints;
            leaderBoard.put(user, tempPoints);
        } else {
            leaderBoard.put(user, incomingPoints);
        }
    }

    public int getUserPoints(User user) {
        if (leaderBoard.containsKey(user)) {
            if (leaderBoard.get(user) != null) {
                return leaderBoard.get(user);
            } else {
                return 0;
            }
        } else {
            return 0;
        }

    }

    public HashMap<User, Integer> getLeaderBoard() {
        return leaderBoard;
    }

    /**
     * todo this method is unnecessary remove when all uses accounted for
     * @param group
     * @return
     */
    public boolean allIsEqual(Group group) {
        if (group.getRewards().size() == rewards.size() && group.getChores().size() == chores.size()
                && group.getMembers().size() == members.size()) {
            for (int i = 0; i < chores.size(); i++) {
                if (!group.getChores().get(i).equals(chores.get(i))) {
                    return false;
                }
            }

            for (int i = 0; i < rewards.size(); i++) {
                if (!group.getRewards().get(i).equals(rewards.get(i))) {
                    return false;
                }
            }
            for (int i = 0; i < members.size(); i++) {
                if (!group.getMembers().get(i).equals(members.get(i))) {
                    return false;
                }
            }
            for (Map.Entry inputEntry : group.getLeaderBoard().entrySet()) {
                boolean foundEntry = false;
                for (Map.Entry entry : leaderBoard.entrySet()) {
                    if (entry.getKey().equals(inputEntry.getKey()) && entry.getValue().equals(inputEntry.getValue())) {
                        foundEntry = true;
                    }
                }
                if (!foundEntry) {
                    return false;
                }
            }
            //If all of the above are true, the rest of the data is checked to be equal, and the
            //result is returned.
            return (group.getIntGroupID() == intGroupID && group.getDescription().equals(description) &&
                    group.getName().equals(name));
        } else
            return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Group)
            return intGroupID == ((Group) obj).getGroupID();

        else
            return false;
    }

    @Override
    public String toString() {
        String ret = intGroupID + ", " + name;
        return ret;
    }

    public int getIntGroupID() {
        return intGroupID;
    }

    public ArrayList<User> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<User> members) {
        this.members = members;
    }


    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setLeaderboard(HashMap<User, Integer> leaderboard) {
        this.leaderBoard = leaderboard;
    }

    public void setChores(ArrayList<Chore> choreList) {
        this.chores = choreList;
    }

    public void setRewards(ArrayList<Reward> rewardList) {
        this.rewards = rewardList;
    }

    public void addToLeaderboard(User user, int score) {
        leaderBoard.put(user, score);
    }
}
