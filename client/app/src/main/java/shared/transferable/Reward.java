package shared.transferable;

/**
 * Reward is a class that represents a reward in the application.
 *
 * @author Emma Svensson
 */
public class Reward implements Transferable {
    private String name;
    private int rewardPrice;
    private String description;
    private String lastDoneByUser = "";
    private int groupID;


    public Reward(String name, int rewardPrice, String description) {
        this.name = name;
        this.rewardPrice = rewardPrice;
        this.description = description;
    }

    public Reward(String name, int rewardPrice, String description, int groupID) {
        this(name, rewardPrice, description);
        this.groupID = groupID;
    }

    public String getLastDoneByUser() {
        return lastDoneByUser;
    }

    public void setLastDoneByUser(String lastDoneByUser) {
        this.lastDoneByUser = lastDoneByUser;
    }

    public String getName() {
        return name;
    }

    public int getRewardPrice() {
        return rewardPrice;
    }

    public String getDescription() {
        return description;
    }

    public void updateReward(Reward reward){
        this.name = reward.getName();
        this.description = reward.getDescription();
        this.rewardPrice = reward.getRewardPrice();
    }

    public boolean nameEquals(Reward reward){
        return reward.getName().equals(this.name);
    }

    @Override
    public boolean equals( Object obj) {
        if (obj instanceof Reward){
            return (((Reward) obj).getName().equals(name) &&
                    ((Reward) obj).getDescription().equals(description) &&
                    ((Reward) obj).getRewardPrice() == rewardPrice &&
                    ((Reward) obj).getLastDoneByUser().equals(lastDoneByUser));
        }
        else
            return false;
    }

    public int getGroupID() {
        return groupID;
    }
}
