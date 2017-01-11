package com.fsryan.example.multiprocess.keystore.test;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.fsryan.example.multiprocess.keystore.cp.SimpleContent;
import com.fsryan.example.multiprocess.keystore.cp.SimpleService;
import com.fsryan.example.multiprocess.keystore.cp.SimpleServiceManager;

public class App extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Log.d("DEBUG", "If you want to do stuff to influence " + SimpleContent.class.getSimpleName() + " initialization, do it here.");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("DEBUG", "If you want to do stuff to influence " + SimpleService.class.getSimpleName() + " initialization, do it here.");
        SimpleServiceManager.init(this);
    }
}
