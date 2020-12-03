package com.example.base;

import androidx.lifecycle.MutableLiveData;

import java.util.HashMap;

public class LiveDataBus {
    private static volatile LiveDataBus instance = new LiveDataBus();
    private HashMap<String, MutableLiveData<Object>> hashMap ;
    private static byte[] bytes = new byte[10];
    private LiveDataBus() {
        hashMap = new HashMap<>();
    }

    public static LiveDataBus getInstance(){
        if(instance == null){//双重校验锁
            synchronized (bytes){
                if(instance == null){
                    instance = new LiveDataBus();
                }
            }

        }
        return instance;
    }

    //泛型 方 法
    public <T> MutableLiveData<T> with(String key,Class<T> type){
        if(!hashMap.containsKey(key)){
            hashMap.put(key,new MutableLiveData<Object>());
        }
        return (MutableLiveData<T>) hashMap.get(key);
    }

}
