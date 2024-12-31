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
    private STATUS status;
    private int tick;
    private StatisticalFolder statisticalFolder = StatisticalFolder.getInstance();
    private List<DetectedObject> lastDetectedObject;

    public Camera(int id, int frequency) {
        this.id = id;
        this.frequency = frequency;
        this.status = STATUS.UP;
        lastDetectedObject=new ArrayList<>();
        tick = 0;

    }


    public void addDetectedObjects(List<DetectedObject> detectedObjects) {
        if (!detectedObjects.isEmpty()) {
            lastDetectedObject.addAll(detectedObjects);
            statisticalFolder.incrementDetectedObjects(detectedObjects.size());
        }
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







}
