package shared.transferable;

import java.util.List;

public class Message implements Transferable {
    private NetCommands command;
    private User user;
    private List<Transferable> data;
    private ErrorMessage errorMessage;

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

    public NetCommands getCommand() {
        return command;
    }

    public User getUser() {
        return user;
    }

    public ErrorMessage getErrorMessage() {
        return errorMessage;
    }

    public List<Transferable> getData() {
        return data;
    }
}