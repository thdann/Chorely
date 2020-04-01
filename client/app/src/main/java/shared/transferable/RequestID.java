package shared.transferable;



import java.util.UUID;

public class RequestID implements Transferable, Comparable<UUID> {
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
    public int compareTo(UUID val) {
        return id.compareTo(val);
    }
}
