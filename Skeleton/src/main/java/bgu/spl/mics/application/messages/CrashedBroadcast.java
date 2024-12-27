package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import java.util.List;

public class CrashedBroadcast implements Broadcast {
    private final String error;
    private final List<String> faultySensors;

    public CrashedBroadcast(String error, List<String> faultySensors) {
        this.error = error;
        this.faultySensors = faultySensors;
    }

    public String getError() {
        return error;
    }

    public List<String> getFaultySensors() {
        return faultySensors;
    }
}
