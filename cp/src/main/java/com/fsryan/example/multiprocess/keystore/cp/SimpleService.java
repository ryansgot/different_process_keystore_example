package com.fsryan.example.multiprocess.keystore.cp;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimpleService extends Service {

    private static final String LOG_TAG = SimpleService.class.getSimpleName();

    private final Set<ISimpleServiceCallbacks> registeredCallbacks = new CopyOnWriteArraySet<>();

    private final Binder binder = new ISimpleService.Stub() {

        @Override
        public void stopRefreshing() {
            if (executor == null || executor.isShutdown()) {
                return;
            }

            executor.shutdownNow();
        }

        @Override
        public void startRefreshing() {
            if (executor != null && !executor.isShutdown()) {
                return;
            }

            executor = Executors.newSingleThreadExecutor();
            refreshOnInterval();
        }

        @Override
        public void registerCallbacks(ISimpleServiceCallbacks callbacks) {
            if (callbacks == null || registeredCallbacks.contains(callbacks)) {
                return;
            }
            registeredCallbacks.add(callbacks);
        }
    };

    private ExecutorService executor;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(LOG_TAG, "onBind");
        return binder;
    }

    @Nullable
    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(LOG_TAG, "onUnbind");
        return true;
    }

    @Nullable
    @Override
    public void onRebind(Intent intent) {
        Log.i(LOG_TAG, "onRebind");
    }

    @Override
    public void onCreate() {
        Log.i(LOG_TAG, "onCreate");
    }

    @Override
    public void onDestroy() {
        Log.i(LOG_TAG, "onDestroy");
        executor.shutdownNow();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(LOG_TAG, "onStartCommand(intent, flags = " + flags + ", startId = " + startId);
        return START_REDELIVER_INTENT;
    }

    private void refreshOnInterval() {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                changeInfoTo(UUID.randomUUID().toString());
                executor.submit(this);
            }
        });
    }

    private void changeInfoTo(String info) {
        Bundle cv = new Bundle();
        cv.putString(SimpleContent.INFO_KEY, info);
        getContentResolver().call(SimpleContent.URI, "save", null, cv);

        Iterator<ISimpleServiceCallbacks> it = registeredCallbacks.iterator();
        while (it.hasNext()) {
            try {
                it.next().onInfoChanged();
            } catch (RemoteException re) {
                re.printStackTrace();
            }
        }
    }

}
