package com.mau.chorely.model;



import com.mau.chorely.model.common.Transferable;

import java.util.ArrayList;

public interface NetworkListener {
    public void notify(ArrayList<Transferable> transferred);
}
