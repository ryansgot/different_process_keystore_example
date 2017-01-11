package com.fsryan.example.multiprocess.keystore.cp;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

public class SimpleContent extends ContentProvider {

    private static final String authority = "com.fsryan.example.multiprocess.keystore.cp";
    public static final Uri URI = Uri.parse("content://" + authority + "/info");
    public static final String INFO_KEY = "info";
    public static final String RESULT_STATUS_KEY = "result_status";

    private static final String LOG_TAG = SimpleContent.class.getSimpleName();

    private String infoInMemory;

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Bundle call(String method, String arg, Bundle extras) {
        Log.i(LOG_TAG, "call(" + method + ", " + arg + ", bundle = " + extras);

        boolean success = false;
        Bundle ret = new Bundle();
        if ("save".equals(method) && extras != null && extras.containsKey(INFO_KEY)) {
            infoInMemory = extras.getString(INFO_KEY);
            success = true;
        } else if ("retrieve".equals(method) && INFO_KEY.equals(arg) && infoInMemory != null) {
            success = true;
            ret.putString(INFO_KEY, infoInMemory);
        }

        ret.putBoolean(RESULT_STATUS_KEY, success);
        return ret;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
