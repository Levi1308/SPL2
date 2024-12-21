package bgu.spl.mics.example.messages;



import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.DetectedObject;
import java.util.List;

/**
 * DetectObjectsEvent is sent by CameraService to LiDarWorkerService
 * to process detected objects.
 */
public class DetectObjectsEvent implements Event<List<DetectedObject>> {
    private final List<DetectedObject> detectedObjects;

    public DetectObjectsEvent(List<DetectedObject> detectedObjects) {
        this.detectedObjects = detectedObjects;
    }

    public List<DetectedObject> getDetectedObjects() {
        return detectedObjects;
    }
}
