package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * LiDarWorkerTracker is responsible for managing a LiDAR worker.
 * It processes DetectObjectsEvents and generates TrackedObjectsEvents by using data from the LiDarDataBase.
 * Each worker tracks objects and sends observations to the FusionSlam service.
 */
public class LiDarWorkerTracker {

    private int id;
    private STATUS status;
    private int frequency;
    List<TrackedObject> lastTrackedobjects;
    public LiDarWorkerTracker(int id, int frequency) {
        this.id = id;
        this.lastTrackedobjects = new ArrayList<>();
        this.frequency = frequency;
        status=STATUS.UP;
    }

    public int getId() {
        return id;
    }

    public int getFrequency() {
        return frequency;
    }

    public STATUS getStatus() {
        return status;
    }

    public List<TrackedObject> getLastTrackedobjects() {
        return lastTrackedobjects;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }
    //public void onDetectObjectsEvent(DetectObjectsEvent event) {
    //}
    private void processLiDARData(List<StampedCloudPoints> cloudPoints) {
    }
    private void sendTrackedObjectsEvent() {

    }
}
