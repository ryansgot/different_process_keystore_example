package com.fsryan.example.multiprocess.keystore.cp;

import java.util.Date;

public class InfoChangedEvent extends ServiceEvent {

    public InfoChangedEvent() {
        super(new Date());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        return sb.delete(sb.length() - 1, sb.length())
                .append("; ")
                .append(InfoChangedEvent.class.getSimpleName())
                .append("{}}")
                .toString();
    }
}
