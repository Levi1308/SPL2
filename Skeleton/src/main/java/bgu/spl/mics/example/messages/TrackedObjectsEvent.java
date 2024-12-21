package bgu.spl.mics.example.messages;


import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.TrackedObject;
import java.util.List;

/**
 * TrackedObjectsEvent is sent by LiDarWorkerService to FusionSLAM
 * to update the environment map with tracked objects.
 */
public class TrackedObjectsEvent implements Event<List<TrackedObject>> {
    private final List<TrackedObject> trackedObjects;

    public TrackedObjectsEvent(List<TrackedObject> trackedObjects) {
        this.trackedObjects = trackedObjects;
    }

    public List<TrackedObject> getTrackedObjects() {
        return trackedObjects;
    }
}

