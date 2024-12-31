package bgu.spl.mics.application.objects;

import bgu.spl.mics.Future;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectObjectsEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a camera sensor on the robot.
 * Responsible for detecting objects in the environment.
 */

public class Camera {
    private final int id;
    private final int frequency;
    private ConcurrentHashMap<Integer, StampedDetectedObjects> detectedObjectsMap;
    private STATUS status;
    private int tick;
    private StatisticalFolder statisticalFolder = StatisticalFolder.getInstance();
    private int numberObjects;

    public Camera(int id, int frequency) {
        this.id = id;
        this.frequency = frequency;
        this.detectedObjectsMap = new ConcurrentHashMap<>();
        this.status = STATUS.UP;
        tick = 0;
        numberObjects = 0;
    }

    /**
     * Adds detected objects to the camera's internal list.
     *
     * @param detectedObjects The list of detected objects to add.
     * @param tick            The tick at which the objects were detected.
     */
    public void addDetectedObjects(List<DetectedObject> detectedObjects, int tick) {
        if (!detectedObjects.isEmpty()) {
            StampedDetectedObjects stamped = new StampedDetectedObjects(tick, detectedObjects);
            detectedObjectsMap.put(tick, stamped);
            statisticalFolder.incrementDetectedObjects(stamped.getDetectedObjects().size());

        }
    }

    public STATUS getStatus() {
        return this.status;
    }

    public int getTick() {
        return this.tick;
    }

    public void setTick(int tick) {
        this.tick = tick;
    }

    public int getId() {
        return id;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setDetectedObjectsMap(Map<Integer, StampedDetectedObjects> detectedObjectsMap, int numberObjects) {
        this.detectedObjectsMap = new ConcurrentHashMap<>(detectedObjectsMap); // Convert to ConcurrentHashMap
        this.numberObjects = numberObjects;
    }

    public Map<Integer, StampedDetectedObjects> getStampedDetectedObjectsMap() {
        return detectedObjectsMap;
    }

    public List<DetectedObject> getDetectedObjects(int tick) {
        StampedDetectedObjects stampedObjects = detectedObjectsMap.getOrDefault(tick, null);
        if (stampedObjects == null) {
            return new ArrayList<>();
        }
        numberObjects-=stampedObjects.getDetectedObjects().size();
        return stampedObjects.getDetectedObjects();
    }

    public List<DetectedObject> getDetectedObjectstill(int tick) {
        List<DetectedObject> output = new ArrayList<>();
        for (StampedDetectedObjects stampedObjects : detectedObjectsMap.values()) {
            if (stampedObjects.getTime() <= tick) {
                output.addAll(stampedObjects.getDetectedObjects());
            }
        }
        return output;
    }

    public List<DetectedObject> onTick() {
        List<DetectedObject> detectedObjects = getDetectedObjects(getTick() - getFrequency());
        DetectedObject error = AnErrorOccured(detectedObjects);
        if(error!=null)
        {
            detectedObjects.clear();
            detectedObjects.add(error);
            return detectedObjects;
        }
        else
            return detectedObjects;
    }

    public DetectedObject AnErrorOccured(List<DetectedObject> objectList) {
        for (DetectedObject d : objectList) {
            if (d.getId().equals("ERROR")) {
                return d;
            }
        }
        return null;
    }
    public boolean detectAll(){
        return numberObjects==0;
    }
}