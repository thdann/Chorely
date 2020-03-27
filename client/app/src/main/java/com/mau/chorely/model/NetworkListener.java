package com.mau.chorely.model;



import shared.transferable.Transferable;

import java.util.ArrayList;

public interface NetworkListener {
    public void notify(ArrayList<Transferable> transferred);
}
