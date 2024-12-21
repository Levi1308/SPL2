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
    List<StampedCloudPoints> stampedCloudPointsList;
    private int frequency;
    public LiDarWorkerTracker(int id, int frequency) {
        this.id = id;
        this.stampedCloudPointsList = new ArrayList<>();
        this.frequency = frequency;
    }

    public int getId() {
        return id;
    }

    public int getFrequency() {
        return frequency;
    }

    public List<StampedCloudPoints> getStampedCloudPointsList() {
        return stampedCloudPointsList;
    }
}
