package com.example.demo;


import com.example.arouter.Router;
import com.example.base.BaseApplication;

public class MyApplication extends BaseApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        Router.init(this);
    }
}
