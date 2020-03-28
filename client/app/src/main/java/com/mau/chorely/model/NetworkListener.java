/**
 * Callback interface for the clients networking class.
 * @version 1.0
 * @author Timothy Denison
 */

package com.mau.chorely.model;


import shared.transferable.Transferable;

import java.util.ArrayList;

public interface NetworkListener {
    public void notify(ArrayList<Transferable> transferred);
}
