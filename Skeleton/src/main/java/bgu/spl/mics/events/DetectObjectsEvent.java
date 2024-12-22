package bgu.spl.mics.events;



import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.DetectedObject;
import java.util.List;

/**
 * DetectObjectsEvent is sent by CameraService to LiDarWorkerService
 * to process detected objects.
 */
public class DetectObjectsEvent implements Event<List<DetectedObject>> {
    private final List<DetectedObject> detectedObjects;
    private int time;

    public DetectObjectsEvent(List<DetectedObject> detectedObjects,int time) {
        this.detectedObjects = detectedObjects;
        this.time=time;
    }

    public int getTime() {
        return time;
    }
    public List<DetectedObject> getDetectedObjects() {

        return detectedObjects;
    }

}
