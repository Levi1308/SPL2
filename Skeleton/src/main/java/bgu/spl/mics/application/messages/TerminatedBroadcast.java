package bgu.spl.mics.application.messages;



import bgu.spl.mics.Broadcast;

/**
 * TerminatedBroadcast is sent by services to notify all others of termination.
 */
public class TerminatedBroadcast implements Broadcast {
    private final String serviceName;

    public TerminatedBroadcast(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceName() {
        return serviceName;
    }
}

