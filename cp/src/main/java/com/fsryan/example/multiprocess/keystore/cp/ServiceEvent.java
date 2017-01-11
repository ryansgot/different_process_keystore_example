package com.fsryan.example.multiprocess.keystore.cp;

import android.support.annotation.CallSuper;

import java.text.DateFormat;
import java.util.Date;

public abstract class ServiceEvent {

    private static final DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);

    private final Date date;

    public ServiceEvent(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    @Override
    @CallSuper
    public String toString() {
        return ServiceEvent.class.getSimpleName() + "{date = " + df.format(date) + "}";
    }
}
