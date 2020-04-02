package shared.transferable;

import java.io.Serializable;

/**
 * Generic interface to enable transferring of arraylists.
 *
 * The protocol this far for said arraylist is:
 * Element 0 requestType @see NetCommand.
 * Element 1 requestId.
 *
 */

public interface Transferable extends Serializable {
}
