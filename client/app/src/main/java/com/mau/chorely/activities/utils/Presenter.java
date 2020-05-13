package com.mau.chorely.activities.utils;

import com.mau.chorely.activities.interfaces.UpdatableActivity;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * This class keeps a reference to the currently running activity in order to enable callbacks from
 * the model.
 *
 * @author Timothy Denison
 */
public class Presenter {
    private static Presenter presenter;
    private ConcurrentLinkedDeque<UpdatableActivity> registeredListeners = new ConcurrentLinkedDeque<>();

    private Presenter() {
    }

    public static Presenter getInstance(){
        if (presenter == null){
            presenter = new Presenter();
        }
        return presenter;
    }



    /**
     * Callback method to let the current activity know to update itself.
     */
    public void updateCurrent() {
        for (UpdatableActivity updatable : registeredListeners) {
            updatable.updateActivity();
        }
    }

    /**
     * Method to toast the current activity.
     *
     * @param message message to toast.
     */
    public void toastCurrent(String message) {
        for (UpdatableActivity updatable :
                registeredListeners) {
            updatable.doToast(message);
        }
    }

    /**
     * Method to register an activity as the current.
     *
     * @param currentActivity a reference to the current activity.
     */
    public void register(UpdatableActivity currentActivity) {
        if (!registeredListeners.contains(currentActivity)) {
            registeredListeners.add(currentActivity);
        }
    }

    /**
     * Method to de-register an activity upon stop.
     *
     * @param deregister Activity to de-register.
     */
    public void deregisterForUpdates(UpdatableActivity deregister) {
        registeredListeners.remove(deregister);
    }

}
