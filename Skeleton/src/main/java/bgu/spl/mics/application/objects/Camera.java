package bgu.spl.mics.application.objects;

import bgu.spl.mics.Future;
import bgu.spl.mics.application.messages.DetectObjectsEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a camera sensor on the robot.
 * Responsible for detecting objects in the environment.
 */

public class Camera {
    private final int id;
    private final int frequency;
    private Map<Integer,StampedDetectedObjects> detectedObjectsMap;
    private STATUS status;
    private int tick;
    private StatisticalFolder statisticalFolder = StatisticalFolder.getInstance();


    public Camera(int id, int frequency) {
        this.id = id;
        this.frequency = frequency;
        this.detectedObjectsMap = new HashMap<Integer ,StampedDetectedObjects >();
        this.status = STATUS.UP;
        tick = 0;
    }

    /**
     * Adds detected objects to the camera's internal list.
     *
     * @param detectedObjects The list of detected objects to add.
     * @param tick            The tick at which the objects were detected.
     */
    public void addDetectedObjects(List<DetectedObject> detectedObjects, int tick) {
        if (!detectedObjects.isEmpty()) {
            StampedDetectedObjects stamped = new StampedDetectedObjects(tick,detectedObjects);
            detectedObjectsMap.put(tick, stamped);
            statisticalFolder.incrementDetectedObjects(stamped.getDetectedObjects().size());

        }
    }

    public void handleError(){
        this.status = STATUS.ERROR;
    }

    public STATUS getStatus(){return this.status;}

    public int getTick(){return this.tick;}

    public void setTick(int tick){this.tick = tick;}

    public int getId() {
        return id;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setDetectedObjectsMap(Map<Integer,StampedDetectedObjects> detectedObjectsMap) {
        this.detectedObjectsMap = detectedObjectsMap;
    }

    public Map<Integer,StampedDetectedObjects> getStampedDetectedObjectsMap() {
        return detectedObjectsMap;
    }

    public List<DetectedObject> getDetectedObjects(int tick) {
        StampedDetectedObjects stampedObjects = detectedObjectsMap.get(tick);
        if (stampedObjects == null) {
            return new ArrayList<>();
        }
        return stampedObjects.getDetectedObjects();
    }



}
