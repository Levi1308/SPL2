package bgu.spl.mics.application.objects;

import java.util.List;

/**
 * Represents a camera sensor on the robot.
 * Responsible for detecting objects in the environment.
 */

public class Camera {
    private int id;
    private int frequency;
    public enum Status{
        Up,Down,Error;
    }
    private Status status;
    private List<StampedDetectedObjects> detectedObjects;
    public Camera()
    {
       //implement
    }

    public synchronized int getId() {
        return id;
    }

    public synchronized int getFrequency() {
        return frequency;
    }
    public synchronized Status getStatus(){
        return status;
    }
    public synchronized List<StampedDetectedObjects> getDetectedObjects(){
        return detectedObjects;
    }

    public synchronized void setStatus(Status status) {
        this.status = status;
    }

    public synchronized void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public synchronized void addDetectedObject(StampedDetectedObjects object){

    }
}
