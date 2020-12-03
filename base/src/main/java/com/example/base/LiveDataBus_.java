package com.example.base;

import androidx.lifecycle.MutableLiveData;

import java.util.HashMap;

public class LiveDataBus_ {
    private static volatile LiveDataBus_ instance = new LiveDataBus_();
    private HashMap<String, BusLivaData<Object>> hashMap ;
    private static byte[] bytes = new byte[10];
    private LiveDataBus_() {
        hashMap = new HashMap<>();
    }

    public static LiveDataBus_ getInstance(){
        if(instance == null){//双重校验锁
            synchronized (bytes){
                if(instance == null){
                    instance = new LiveDataBus_();
                }
            }

        }
        return instance;
    }


    //泛型 方 法
    public <T> BusLivaData<T> with_(String key,Class<T> type){
        if(!hashMap.containsKey(key)){
            hashMap.put(key,new BusLivaData<Object>());
        }
        return (BusLivaData<T>) hashMap.get(key);
    }
}
