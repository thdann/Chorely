package com.mau.chorely.model;



import com.mau.chorely.model.transferrable.NetCommands;
import com.mau.chorely.model.transferrable.Transferable;
import com.mau.chorely.model.persistentStorage.PersistentStorage;
import com.mau.chorely.model.utils.MultiMap;
import com.mau.chorely.model.utils.ResultHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingDeque;


public class Model implements NetworkListener{

    private MultiMap<NetCommands, ResultHandler> threadsWaitingForResult = new MultiMap<>();
    private HashMap<NetCommands, NetCommands> resultAndRequestPairs = new HashMap<>();
    private LinkedBlockingDeque<ArrayList<Transferable>> taskToHandle = new LinkedBlockingDeque<>();
    private NetInterface network;
    private Thread modelThread = new Thread(new ModelThread());
    PersistentStorage storage = new PersistentStorage();
    Model(){
        network = new NetInterface(this);
        modelThread.start();
    }

    private void registerCommandPairs(){
        resultAndRequestPairs.put(NetCommands.registrationOk, NetCommands.register);
        resultAndRequestPairs.put(NetCommands.registrationDenied, NetCommands.register);
    }

    public void stop(){
        network.disconnect();
        modelThread.interrupt();
    }

    public synchronized NetCommands notifyForResult(ArrayList<Transferable> curWorkingOn){
        try {
            taskToHandle.put(curWorkingOn);
            ResultHandler threadLockObject = new ResultHandler();
            threadsWaitingForResult.put(((NetCommands)curWorkingOn.get(0)), threadLockObject);
            return threadLockObject.waitForResult();
        } catch (InterruptedException e){
            System.out.println("Exception putting in notifyForResult" + e.getMessage());
            return NetCommands.internalClientError;
        }
    }

    private void handleResult(NetCommands returnCommand){
        ArrayList<ResultHandler> waitingThreads =
                threadsWaitingForResult.get(resultAndRequestPairs.get(returnCommand));

        for (ResultHandler thread : waitingThreads) {
            thread.notifyResult(returnCommand);
        }
    }


    @Override
    public  void notify(ArrayList<Transferable> transferred) {
        try {
            taskToHandle.put(transferred);

        } catch (InterruptedException e){
            System.out.println("Error in model callback" + e.getMessage());
        }
    }


    private class ModelThread implements Runnable{
        @Override
        public void run() {

            while (!Thread.interrupted()){
                ArrayList<Transferable> curWorkingOn = taskToHandle.remove();

                switch ((NetCommands)curWorkingOn.get(0)){
                    case register:
                        storage.updateData("/user.cho", curWorkingOn.get(0));
                        network.sendData(curWorkingOn);
                        break;
                    case registrationOk:
                        handleResult((NetCommands) curWorkingOn.get(0));
                        break;

                }
            }
        }
    }

}
