/**
 *
 */


package com.mau.chorely.activities.utils;


import com.mau.chorely.model.Model;

public class BridgeInstances {
    private static Model model;
    private static Presenter presenter;
    private BridgeInstances(){}
    public static Model getModel(){
        if(model == null){
            model = new Model();
        }
        return model;
    }

    public static Presenter getPresenter(){
        if (presenter == null){
            presenter = new Presenter();
        }
        return presenter;
    }
}
