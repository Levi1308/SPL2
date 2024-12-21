package bgu.spl.mics.example.messages;

import bgu.spl.mics.Broadcast;

/**
 * TickBroadcast is sent every tick by the TimeService to all subscribed services.
 */
public class TickBroadcast implements Broadcast {
    private final int tick;

    public TickBroadcast(int tick) {
        this.tick = tick;
    }

    public int getTick() {
        return tick;
    }
}

