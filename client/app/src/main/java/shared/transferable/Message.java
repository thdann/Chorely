package shared.transferable;

import java.util.List;

public class Message implements Transferable {
    private NetCommands command;
    private User user;
    private Group group;
    private List<Transferable> data;
    private ErrorMessage errorMessage;
    private Chore chore;
    private Reward reward;

    public Message(NetCommands command, User user, List<Transferable> data) {
        this.command = command;
        this.user = user;
        this.data = data;
    }

    public Message(NetCommands command, User user, List<Transferable> data, ErrorMessage errorMessage) {
        this.command = command;
        this.user = user;
        this.data = data;
        this.errorMessage = errorMessage;
    }

    public Message(NetCommands command, User user, ErrorMessage errorMessage) {
        this.command = command;
        this.user = user;
        this.errorMessage = errorMessage;
    }

    public NetCommands getCommand() {
        return command;
    }

    public User getUser() {
        return user;
    }

    public Group getGroup() {
        return group;
    }

    public ErrorMessage getErrorMessage() {
        return errorMessage;
    }

    public List<Transferable> getData() {
        return data;
    }

    public Chore getChore() {
        return chore;
    }

    public Reward getReward() {
        return reward;
    }
}
