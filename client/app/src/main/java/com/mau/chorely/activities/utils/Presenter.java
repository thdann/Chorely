package com.mau.chorely.activities.utils;

import com.mau.chorely.activities.interfaces.UpdatableActivity;

import java.util.ArrayList;

public class Presenter {

    private ArrayList <UpdatableActivity> registeredListeners = new ArrayList<>();

    protected Presenter(){}

    public void updateCurrent(){
        for(UpdatableActivity updatable : registeredListeners){
            updatable.UpdateActivity();
        }
    }

    public void toastCurrent(String message){
        for (UpdatableActivity updatable:
             registeredListeners) {
            updatable.doToast(message);
        }
    }


    public void register(UpdatableActivity currentActivity){
        registeredListeners.add(currentActivity);
    }

    public void deregisterForUpdates(UpdatableActivity deregister){
        registeredListeners.remove(deregister);
    }

}
