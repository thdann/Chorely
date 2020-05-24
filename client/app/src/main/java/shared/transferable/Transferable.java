package shared.transferable;

import java.io.Serializable;

/**
 * Messages sent between server and client have a list of optional data.
 * Objects placed in this list must implement Transferable.
 */
public interface Transferable extends Serializable {
}
