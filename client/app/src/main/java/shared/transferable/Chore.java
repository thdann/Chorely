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


    public Chore(String name, int score) {
     this.name = name;
     this.score = score;
    }

    public Chore(String name, int score, String description) {
        this.name = name;
        this.score = score;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public int Score() {
        return score;
    }
    
    public void setScore(int Score) {
        this.score = score;
    }

    public String getDescription() {
        return description;
    }
}
