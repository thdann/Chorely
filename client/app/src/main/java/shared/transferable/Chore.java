package shared.transferable;



/**
 * Chore is a class that represent a chore in the application.
 * version 1.0 2020-04-08
 *
 * @autor Emma Svensson
 */
public class Chore implements Transferable {
    private String name;
    private int score;
    private String description;
    private String lastDoneByUser = " ";


    public Chore(String name, int score) {
     this.name = name;
     this.score = score;
    }

    public Chore(String name, int score, String description) {
        this.name = name;
        this.score = score;
        this.description = description;
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
    public void setScore(int Score) {
        this.score = score;
    }

    public String getDescription() {
        return description;
    }

    public void updateChore(Chore chore){
        this.name = chore.getName();
        this.description = chore.getDescription();
        this.score = chore.getScore();
    }

    public boolean nameEquals(Chore input){
        return this.name.equals(input.getName());
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Chore){
            return (((Chore) obj).getDescription().equals(description) &&
                    ((Chore) obj).getName().equals(name) && ((Chore) obj).getScore() == score &&
                    ((Chore) obj).getLastDoneByUser().equals(lastDoneByUser));
        }
        else
            return false;
    }
}
