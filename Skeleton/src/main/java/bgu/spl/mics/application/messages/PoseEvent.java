package bgu.spl.mics.application.messages;


import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Pose;

/**
 * PoseEvent is sent by PoseService to FusionSLAM to provide the robot's
 * current pose at each tick.
 */
public class PoseEvent implements Event<Pose> {
    private final Pose pose;

    public PoseEvent(Pose pose) {
        this.pose = pose;
    }

    public Pose getPose() {
        return pose;
    }

    @Override
    public String toString() {
        return "PoseEvent";
    }
}

