package shared.transferable;

import java.util.UUID;

/**
 * The UUID class creates IDs that are guaranteed to be unique.
 * This makes it possible for a client to create a unique ID without having to
 * communicate with the server to verify that another client hasn't already created
 * the same ID.
 * todo remove this class when unnecessary
 * UUID is wrapped in the GenericID class to make it Transferable.
 */
public class GenericID implements Transferable, Comparable<GenericID> {
    private final UUID id;

    public GenericID() {
        id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }

    @Override
    public String toString() {
        return id.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof GenericID)) {
            return false;
        }

        return id.equals(((GenericID) obj).getId());
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
