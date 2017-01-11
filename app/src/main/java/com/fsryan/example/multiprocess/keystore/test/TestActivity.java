package com.fsryan.example.multiprocess.keystore.test;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.fsryan.example.multiprocess.keystore.cp.BindStateChangedEvent;
import com.fsryan.example.multiprocess.keystore.cp.InfoChangedEvent;
import com.fsryan.example.multiprocess.keystore.cp.SimpleContent;
import com.fsryan.example.multiprocess.keystore.cp.SimpleServiceManager;
import com.google.common.eventbus.Subscribe;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestActivity extends AppCompatActivity {

    private static final int ONE_MEGABYTE = 1024 * 1024;

    private ExecutorService executor;

    private TextView consoleText;
    private ScrollView console;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        consoleText = (TextView) findViewById(R.id.console_text);
        console = (ScrollView) findViewById(R.id.console_scroll_view);
    }

    @Override
    public void onStart() {
        super.onStart();
        SimpleServiceManager.register(this);
    }

    @Override
    public void onStop() {
        SimpleServiceManager.unregister(this);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdownNow();
        }
        super.onDestroy();
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onBindStateChangedEvent(BindStateChangedEvent event) {
        if (isFinishing()) {
            return;
        }
        consoleText.append("\n" + event.toString());
        scrollToBottom();
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onInfoChangedEvent(InfoChangedEvent event) {
        appendCurrentInfoToConsole(true);
    }

    private void appendCurrentInfoToConsole(boolean fromEvent) {
        if (isFinishing()) {
            return;
        }
        Bundle b = getContentResolver().call(SimpleContent.URI, "retrieve", SimpleContent.INFO_KEY, null);
        String message = "\nInfo" + (fromEvent ? " changed to: " : " read: " ) + b.getString(SimpleContent.INFO_KEY);
        consoleText.append(message);
        scrollToBottom();
    }

    public void onUnbindClicked(View view) {
        SimpleServiceManager.unbind();
    }

    public void onBindClicked(View view) {
        SimpleServiceManager.bindToService();
    }

    public void onStopSettingClicked(View view) {
        SimpleServiceManager.stopRefreshing();
    }

    public void onStartSettingClicked(View view) {
        SimpleServiceManager.startRefreshing();
    }

    public void onStartReadingClicked(View view) {
        if (executor != null && !executor.isShutdown()) {
            return;
        }

        executor = Executors.newSingleThreadExecutor();
        executor.submit(new Runnable() {
            private final Random random = new Random();
            @Override
            public void run() {
                try {
                    Thread.sleep(random.nextInt(1000));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        appendCurrentInfoToConsole(false);
                    }
                });
                executor.submit(this);
            }
        });
    }

    public void onStopReadingClicked(View view) {
        if (executor == null || executor.isShutdown()) {
            return;
        }
        executor.shutdownNow();
    }

    public void onClearConsoleClicked(View view) {
        if (isFinishing()) {
            return;
        }
        consoleText.setText("");
    }

    private void scrollToBottom() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    return;
                }
                if (byteCount(consoleText.getText().length()) > ONE_MEGABYTE) {
                    consoleText.setText("Had to clear");
                } else {
                    console.fullScroll(View.FOCUS_DOWN);
                }
            }
        },
        100);
    }

    private static int byteCount(int length) {
        return 8 * (int) ((((length) * 2) + 45) / 8);
    }
}
