package shared.transferable;


/**
 * Chore is a class that represent a chore in the application.
 *
 * @author Emma Svensson
 */
public class Chore implements Transferable {
    private String name;
    private int score;
    private String description;
    private String lastDoneByUser = " ";
    private int groupID; //must have a group

    public Chore(String name, int score, String description) {
        this.name = name;
        this.score = score;
        this.description = description;
    }
    public Chore(String name, int score, String description, int groupID) {
        this.name = name;
        this.score = score;
        this.description = description;
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

    public int getScore() {
        return score;
    }

    public String getDescription() {
        return description;
    }

    public void updateChore(Chore chore) {
        this.name = chore.getName();
        this.description = chore.getDescription();
        this.score = chore.getScore();
    }

    public boolean nameEquals(Chore input) {
        return this.name.equals(input.getName());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Chore) {
            return ((Chore) obj).getName().equals(name);
        } else
            return false;
    }

    public int getGroupID() {
        return groupID;
    }
}
