package shared.transferable;





import java.util.UUID;

public class RequestID implements Transferable, Comparable<RequestID> {
    private UUID id;
    public RequestID(){
        id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }

    public  String getIdString(){
        return id.toString();
    }

    @Override
    public boolean equals(Object obj) {
        return id.equals(((RequestID)obj).getId());
    }

    @Override
    public int compareTo(RequestID val) {
        return id.compareTo(val.getId());
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
