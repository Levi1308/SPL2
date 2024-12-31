package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Represents a camera sensor on the robot.
 * Responsible for detecting objects in the environment.
 */

public class Camera {
    private final int id;
    private final int frequency;
    private Map<Integer, StampedDetectedObjects> detectedObjectsMap;
    private STATUS status;
    private int tick;
    private final ReentrantLock lock = new ReentrantLock();
    private StatisticalFolder statisticalFolder = StatisticalFolder.getInstance();

    public Camera(int id, int frequency) {
        this.id = id;
        this.frequency = frequency;
        this.detectedObjectsMap = new HashMap<>();
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
        lock.lock();
        try {
            if (!detectedObjects.isEmpty()) {
                StampedDetectedObjects stamped = new StampedDetectedObjects(tick, detectedObjects);
                detectedObjectsMap.put(tick, stamped);
                statisticalFolder.incrementDetectedObjects(stamped.getDetectedObjects().size());
            }
        } finally {
            lock.unlock();
        }
    }

    public STATUS getStatus() {
        lock.lock();
        try {
            return this.status;
        } finally {
            lock.unlock();
        }
    }

    public int getTick() {
        lock.lock();
        try {
            return this.tick;
        } finally {
            lock.unlock();
        }
    }

    public void setTick(int tick) {
        lock.lock();
        try {
            this.tick = tick;
        } finally {
            lock.unlock();
        }
    }

    public int getId() {
        return id;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setDetectedObjectsMap(Map<Integer, StampedDetectedObjects> detectedObjectsMap) {
        lock.lock();
        try {
            this.detectedObjectsMap = detectedObjectsMap;
        } finally {
            lock.unlock();
        }
    }

    public Map<Integer, StampedDetectedObjects> getStampedDetectedObjectsMap() {
        lock.lock();
        try {
            return new HashMap<>(detectedObjectsMap);  // Return a copy to avoid exposing internal state
        } finally {
            lock.unlock();
        }
    }

    public List<DetectedObject> getDetectedObjects(int tick) {
        lock.lock();
        try {
            StampedDetectedObjects stampedObjects = detectedObjectsMap.get(tick);
            if (stampedObjects == null) {
                return new ArrayList<>();
            }
            return stampedObjects.getDetectedObjects();
        } finally {
            lock.unlock();
        }
    }

    public List<DetectedObject> getDetectedObjectstill(int tick) {
        lock.lock();
        try {
            List<DetectedObject> output = new ArrayList<>();
            for (StampedDetectedObjects stampedObjects : detectedObjectsMap.values()) {
                if (stampedObjects.getTime() <= tick) {
                    output.addAll(stampedObjects.getDetectedObjects());
                }
            }
            return output;
        } finally {
            lock.unlock();
        }
    }
}
