package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import java.util.List;

public class CrashedBroadcast implements Broadcast {
    private final String error;
    private final String faultySensor;
    private final int time;

    public CrashedBroadcast(int time, String error, String faultySensor ) {
        this.time = time;
        this.error = error;
        this.faultySensor = faultySensor;
    }

    public String getError() {
        return error;
    }

    public String getFaultySensor() {
        return faultySensor;
    }

    public int getTime() {
        return time;
    }


}
