/**
 * Callback interface for the clients networking class.
 * @version 1.0
 * @author Timothy Denison
 */

package com.mau.chorely.model;


import shared.transferable.Message;
import shared.transferable.Transferable;

import java.util.ArrayList;

public interface NetworkListener {
     void handleTask(Message msg);
}
