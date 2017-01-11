package com.fsryan.example.multiprocess.keystore.cp;

import java.util.Date;

public class BindStateChangedEvent extends ServiceEvent {

    private final boolean bound;

    public BindStateChangedEvent(boolean bound) {
        super(new Date());
        this.bound = bound;
    }

    public boolean isBound() {
        return bound;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        return sb.delete(sb.length() - 1, sb.length())
                .append("; ")
                .append(BindStateChangedEvent.class.getSimpleName())
                .append("{bound = ")
                .append(bound)
                .append("}}")
                .toString();
    }
}
