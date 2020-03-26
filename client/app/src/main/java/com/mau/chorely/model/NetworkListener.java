package com.mau.chorely.model;

import com.example.myapplication.model.common.Transferable;

import java.util.ArrayList;

public interface NetworkListener {
    public void notify(ArrayList<Transferable> transferred);
}
