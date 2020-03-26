package com.mau.chorely.model.utils;



import com.mau.chorely.model.transferrable.NetCommands;

import java.util.ArrayList;
import java.util.HashMap;

public class MultiMap <K, V> {
    private HashMap<K, ArrayList<V>> hashMap = new HashMap<>();

    public void put(K key, V value){
        if(hashMap.containsKey(key)){
            ArrayList<V> newList = hashMap.remove(key);
            newList.add(value);
            hashMap.put(key, newList);
        }
        else{
            ArrayList<V> newList = new ArrayList<>();
            newList.add(value);
            hashMap.put(key, newList);
        }
    }

    public boolean containsKey(NetCommands key){
        return hashMap.containsKey(key);
    }

    public int size(){
        return hashMap.size();
    }

    public ArrayList<V> get(NetCommands key){
        return hashMap.get(key);
    }
}
