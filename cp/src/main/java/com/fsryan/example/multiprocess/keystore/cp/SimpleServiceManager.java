package com.fsryan.example.multiprocess.keystore.cp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;

import java.util.concurrent.Executor;

public class SimpleServiceManager {

    private static Context appContext;
    private static ISimpleService svc;

    private static EventBus bus = new AsyncEventBus(new Executor() {
        private Handler handler = new Handler(Looper.getMainLooper());
        @Override
        public void execute(Runnable runnable) {
            handler.post(runnable);
        }
    });

    private static final ISimpleServiceCallbacks.Stub callbacks = new ISimpleServiceCallbacks.Stub() {
        @Override
        public void onInfoChanged() throws RemoteException {
            bus.post(new InfoChangedEvent());
        }
    };

    private static final ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            svc = ISimpleService.Stub.asInterface(service);
            try {
                svc.registerCallbacks(callbacks);
            } catch (Exception re) {

            }
            bus.post(new BindStateChangedEvent(true));
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            svc = null;
            bus.post(new BindStateChangedEvent(false));
        }
    };

    public static void init(Context context) {
        appContext = context != null && appContext == null ? context.getApplicationContext() : appContext;
        bindToService();
    }

    public static void bindToService() {
        if (appContext == null) {
            return;
        }
        if (svc != null) {
            bus.post(new BindStateChangedEvent(true));
            return;
        }
        Intent bindSimpleServiceManager = new Intent(appContext, SimpleService.class);
        appContext.bindService(bindSimpleServiceManager, conn, Context.BIND_AUTO_CREATE | Context.BIND_IMPORTANT);
    }

    public static void unbind() {
        if (appContext == null) {
            return;
        }
        if (svc == null) {
            bus.post(new BindStateChangedEvent(false));
            return;
        }

        try {
            appContext.unbindService(conn);
        } catch (Exception e) {
            Log.e("RYAN", "failed to unbind", e);
        }
    }

    public static void stopRefreshing() {
        if (appContext == null || svc == null) {
            return;
        }
        try {
            svc.stopRefreshing();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void startRefreshing() {
        if (appContext == null || svc == null) {
            return;
        }
        try {
            svc.startRefreshing();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void register(Object observer) {
        bus.register(observer);
    }

    public static void unregister(Object observer) {
        bus.unregister(observer);
    }
}
