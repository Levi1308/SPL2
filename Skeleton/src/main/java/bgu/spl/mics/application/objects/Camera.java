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


    /**
     * Constructor for Camera.
     *
     * @param id        The unique identifier for the camera.
     * @param frequency The detection frequency (in ticks).
     */
    public Camera(int id, int frequency) {
        this.id = id;
        this.frequency = frequency;
        this.detectedObjectsList = new ArrayList<>();
        this.status = STATUS.UP;
    }
    /**
     * Detects objects at a specific time tick.
     * This simulates the camera capturing objects in the environment.
     *
     * @param time The current tick time during detection.
     * @return A list of detected objects with time stamps.
     */
    public StampedDetectedObjects detectObjects(int time) {
        List<DetectedObject> detectedObjects = generateRandomObjects();
        StampedDetectedObjects stampedObjects = new StampedDetectedObjects(time, detectedObjects);
        detectedObjectsList.add(stampedObjects);
        return stampedObjects;
    }

    /**
     * Generates random detected objects for simulation purposes.
     *
     * @return List of randomly detected objects.
     */
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

    /**
     * @return The camera's unique ID.
     */
    public int getId() {
        return id;
    }

    /**
     * @return The camera's detection frequency.
     */
    public int getFrequency() {
        return frequency;
    }

    /**
     * @return List of all objects detected by this camera.
     */
    public List<StampedDetectedObjects> getDetectedObjectsList() {
        return detectedObjectsList;
    }

    public void updateStatistics() {
        StatisticalFolder.incrementDetectedObjects(detectedObjectsList.size());
    }

    public List<DetectedObject> getDetectedObjects(int tick) {
        for (StampedDetectedObjects stamped : detectedObjectsList) {
            if (stamped.getTime() == tick) {
                return stamped.getDetectedObjects();
            }
        }
        return new ArrayList<>();
    }

    public boolean shouldSendEvent(int tick) {
        return tick % frequency == 0;
    }
}
