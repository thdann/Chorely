package shared.transferable;


/**
 * ErrorMessage is used to communicate error message between server and client.
 */
public class ErrorMessage implements Transferable {
    private String message;

    public ErrorMessage(String message){
        this.message = message;
    }

    public String getMessage(){
        return message;
    }

    public String toString() {
        return message;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ErrorMessage)) {
            return false;
        }
        ErrorMessage otherError = (ErrorMessage) obj;
        return message.equals(otherError.message);
    }
}
