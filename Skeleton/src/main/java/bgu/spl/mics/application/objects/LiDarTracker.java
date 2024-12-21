package bgu.spl.mics.application.objects;



import java.util.ArrayList;
import java.util.List;

/**
 * Represents a LiDAR tracker that processes and tracks objects in the environment.
 */
public class LiDarTracker {
    private final int id;
    private STATUS status;

    public LiDarTracker(int id) {
        this.id = id;
        this.status = STATUS.UP;
    }

    public int getId() {
        return id;
    }

    public boolean isOperational() {
        return status == STATUS.UP;
    }

    public List<TrackedObject> trackObjects(int tick) {
        return generateTrackedObjects();
    }

    public List<TrackedObject> processDetection(List<DetectedObject> detectedObjects) {
        List<TrackedObject> trackedObjects = new ArrayList<>();
        for (DetectedObject obj : detectedObjects) {
            trackedObjects.add(new TrackedObject(obj.getId(), "Tracked " + obj.getDescription()));
        }
        return trackedObjects;
    }

    public void updateStatistics(int newTrackedObjects) {
        StatisticalFolder.incrementTrackedObjects(newTrackedObjects);
    }

    private List<TrackedObject> generateTrackedObjects() {
        List<TrackedObject> objects = new ArrayList<>();
        int numObjects = (int) (Math.random() * 5);
        for (int i = 0; i < numObjects; i++) {
            objects.add(new TrackedObject("Obj_" + i, "Random Object"));
        }
        return objects;
    }
}

