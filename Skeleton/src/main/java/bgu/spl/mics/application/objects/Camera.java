package bgu.spl.mics.application.objects;
import java.util.ArrayList;
import java.util.List;
/**
 * Represents a camera sensor on the robot.
 * Responsible for detecting objects in the environment.
 */

public class Camera {
    private final int id;
    private final int frequency;
    private final List<StampedDetectedObjects> detectedObjectsList;
    private STATUS status;

    public Camera(int id, int frequency) {
        this.id = id;
        this.frequency = frequency;
        this.detectedObjectsList = new ArrayList<>();
        this.status = STATUS.UP;
    }

    public StampedDetectedObjects detectObjects(int time) {
        List<DetectedObject> detectedObjects = generateRandomObjects();
        StampedDetectedObjects stampedObjects = new StampedDetectedObjects(time, detectedObjects);
        detectedObjectsList.add(stampedObjects);
        return stampedObjects;
    }

    private List<DetectedObject> generateRandomObjects() {
        List<DetectedObject> objects = new ArrayList<>();
        int numObjects = (int) (Math.random() * 5) + 1;  // Random number of objects (1 to 5)

        for (int i = 0; i < numObjects; i++) {
            String id = "Obj_" + (int) (Math.random() * 1000);
            String description = "Object Description " + i;
            objects.add(new DetectedObject(id, description));
        }

        return objects;
    }

    public int getId() {
        return id;
    }

    public int getFrequency() {
        return frequency;
    }

    public List<StampedDetectedObjects> getDetectedObjectsList() {
        return detectedObjectsList;
    }
}
/*
    /**
     * Generates random detected objects for simulation purposes.
     *
     * @return List of randomly detected objects.
     */