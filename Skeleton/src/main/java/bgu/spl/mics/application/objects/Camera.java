package bgu.spl.mics.application.objects;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

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
    private final ReentrantLock lock = new ReentrantLock();
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

    public STATUS getStatus() {
        return this.status;
    }

    public void setTick(int tick) {
        this.tick = tick;
        lock.lock();
        try {
            return;
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


    public int getId() {
        return id;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setDetectedObjectsMap(Map<Integer, StampedDetectedObjects> detectedObjectsMap,int numberObjects) {
        lock.lock();
        try {
            this.detectedObjectsMap = new ConcurrentHashMap<>(detectedObjectsMap);
            this.numberObjects=numberObjects;
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


    public List<DetectedObject> onTick() {
        List<DetectedObject> detectedObjects = getDetectedObjects(getTick() - getFrequency());
        statisticalFolder.incrementDetectedObjects(detectedObjects.size());
        numberObjects-=detectedObjects.size();
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


