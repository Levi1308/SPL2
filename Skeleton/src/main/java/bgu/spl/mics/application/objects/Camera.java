package bgu.spl.mics.application.objects;

import bgu.spl.mics.events.DetectObjectsEvent;

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
    private int tick;

    public Camera(int id, int frequency) {
        this.id = id;
        this.frequency = frequency;
        this.detectedObjectsList = new ArrayList<>();
        this.status = STATUS.UP;
        tick = 0;
    }

    public void DetectObjects(DetectedObject object, int time) {
        List<DetectedObject> tempObjects = getDetectedObjectsList(time);
        if (tempObjects != null) {
            if (!tempObjects.contains(object)) {
                List<DetectedObject> newStampObj = getDetectedObjectsList(time + frequency);
                if (newStampObj != null)
                    newStampObj.add(object);
                else {
                    StampedDetectedObjects newObj = new StampedDetectedObjects(time + frequency);
                    newObj.AddDetectedObject(object);
                    detectedObjectsList.add(newObj);
                }
            }

        } else {
            StampedDetectedObjects newObj = new StampedDetectedObjects(time + frequency);
            newObj.AddDetectedObject(object);
            detectedObjectsList.add(newObj);
        }

    }

    public void onTick() {
        if (shouldSendEvent()) {
           //DetectObjectsEvent
        }
        tick++;
    }

    public int getId() {
        return id;
    }

    public int getFrequency() {
        return frequency;
    }

    public List<StampedDetectedObjects> getStampedDetectedObjectsList() {
        return detectedObjectsList;
    }

    public void updateStatistics() {
        //StatisticalFolder.(detectedObjectsList.size());
    }

    public List<DetectedObject> getDetectedObjectsList(int time) {
        for (StampedDetectedObjects stamped : detectedObjectsList) {
            if (stamped.getTime() == time) {
                return stamped.getDetectedObjects();
            }
        }
        return null;
    }

    public boolean shouldSendEvent() {
        return tick % frequency == 0;
    }
}
