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
    private StatisticalFolder statisticalFolder = StatisticalFolder.getInstance();
    private int numberObjects;
    private final ConcurrentHashMap<String, Boolean> processedObjects = new ConcurrentHashMap<>();
    private StampedDetectedObjects lastFrame;




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

    public synchronized void setTick(int tick) {
        this.tick = tick;
    }

    public synchronized int getTick() {
        return this.tick;
    }


    public int getId() {
        return id;
    }

    public int getFrequency() {
        return frequency;
    }

    public synchronized void setDetectedObjectsMap(Map<Integer, StampedDetectedObjects> detectedObjectsMap,int numberObjects) {
        this.detectedObjectsMap = new ConcurrentHashMap<>(detectedObjectsMap);
        this.numberObjects=numberObjects;

    }



    public synchronized List<DetectedObject> getDetectedObjects(int tick) {
        StampedDetectedObjects stampedObjects = detectedObjectsMap.get(tick);
        if (stampedObjects == null) {
            return new ArrayList<>();
        }
        return stampedObjects.getDetectedObjects();

    }

    public synchronized StampedDetectedObjects getStampedObjects(int tick) {
        return detectedObjectsMap.get(tick);
    }




    public List<DetectedObject> onTick() {
        List<DetectedObject> detectedObjects = getDetectedObjects(getTick() - getFrequency());
        if (!detectedObjects.isEmpty() && !detectedObjects.get(0).getId().equals("ERROR")) {
            lastFrame = new StampedDetectedObjects(getTick(), detectedObjects);
        }
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
    public StampedDetectedObjects getLastFrame() {
        return lastFrame;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }
}


