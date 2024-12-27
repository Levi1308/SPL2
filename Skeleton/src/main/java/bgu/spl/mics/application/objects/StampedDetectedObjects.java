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

    public StampedDetectedObjects(int time, List<DetectedObject> detectedObjects) {
        this.time = time;
        this.detectedObjects = detectedObjects;
    }

    public int getTime() {
        return time;
    }

    public List<DetectedObject> getDetectedObjects() {
        return detectedObjects;
    }
    public void AddDetectedObject(List<DetectedObject> listObject)
    {
        detectedObjects.addAll(listObject);
    }
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("StampedDetectedObjects{")
                .append("time=").append(time)
                .append(", detectedObjects=[");

        for (DetectedObject obj : detectedObjects) {
            builder.append(obj.toString()).append(", ");
        }
        if (!detectedObjects.isEmpty()) {
            builder.setLength(builder.length() - 2); // Remove trailing comma and space
        }
        builder.append("]}");

        return builder.toString();
    }
}


