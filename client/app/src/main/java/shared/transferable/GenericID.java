package shared.transferable;





import java.util.UUID;

public class GenericID implements Transferable, Comparable<GenericID> {
    private UUID id;
    public GenericID(){
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
        return id.equals(((GenericID)obj).getId());
    }

    @Override
    public int compareTo(GenericID val) {
        return id.compareTo(val.getId());
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
