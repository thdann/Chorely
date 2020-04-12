/**
 *
 */


package com.mau.chorely.activities.utils;


import com.mau.chorely.model.Model;

import java.io.File;

public class BridgeInstances {
    private static Model model;
    private static Presenter presenter;
    private BridgeInstances(){}

    public static void instantiateModel(File appFilesDir){
        if(model == null) {
            model = new Model(appFilesDir);
        }
    }

    public static Model getModel(){
        if(model == null){
            throw new RuntimeException("Model unreferenced");
            //model = new Model();
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
