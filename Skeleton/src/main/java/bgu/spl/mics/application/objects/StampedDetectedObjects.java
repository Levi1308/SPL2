package bgu.spl.mics.application.objects;
import java.util.ArrayList;
import java.util.List;
/**
 * Represents objects detected by the camera at a specific timestamp.
 * Includes the time of detection and a list of detected objects.
 */
public class StampedDetectedObjects {
    private final int time;
    private final List<DetectedObject> detectedObjects;

    public StampedDetectedObjects(int time) {
        this.time = time;
        this.detectedObjects = new ArrayList<>();
    }

    public int getTime() {
        return time;
    }

    public List<DetectedObject> getDetectedObjects() {
        return detectedObjects;
    }
    public void AddDetectedObject(DetectedObject object)
    {
        detectedObjects.add(object);
    }
}


