package shared.transferable;

import java.util.ArrayList;
import java.util.List;

/**
 * Objects of the Message class are used for communication between server and client.
 *
 * @author Angelica Asplund, Emma Svensson, Theresa Dannberg, Timothy Denison, Fredrik Jeppsson
 */
public class Message implements Transferable {
    private NetCommands command;
    private User user;
    private List<Transferable> data;
    private ErrorMessage errorMessage;

    public Message(NetCommands command, User user) {
        this.command = command;
        this.user = user;
        this.data = new ArrayList<>();
        this.errorMessage = new ErrorMessage("");
    }

    public Message(NetCommands command, User user, List<Transferable> data) {
        this.command = command;
        this.user = user;
        this.data = data;
        this.errorMessage = new ErrorMessage("");
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
        this.data = new ArrayList<>();
        this.errorMessage = errorMessage;
    }

    public void setCommand(NetCommands command){
        this.command = command;
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

    @Override
    public String toString() {
        return "Message{" +
                "command=" + command +
                ", user=" + user +
                ", data=" + data +
                ", errorMessage=" + errorMessage +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Message)) {
            return false;
        }
        Message otherMessage = (Message) obj;

        boolean equalCommands = command.equals(otherMessage.command);
        boolean equalUser = user.equals(otherMessage.user);
        boolean equalErrorMessage = errorMessage.equals(otherMessage.errorMessage);
        boolean equalData = data.equals(otherMessage.data);

        return equalCommands && equalUser && equalErrorMessage && equalData;
    }
}
