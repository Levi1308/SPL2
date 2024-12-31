package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;

public class LastFrame {

    private List<DetectedObject> detectedObjects;
    private List<CloudPoint> lidarCloudPoints;



    public LastFrame() {
        this.detectedObjects = new ArrayList<>();
        this.lidarCloudPoints = new ArrayList<>();

    }


    public synchronized List<DetectedObject> getDetectedObjects() {
        return detectedObjects;
    }

    public synchronized List<CloudPoint> getLidarCloudPoints() {
        return lidarCloudPoints;
    }

    public synchronized void addDetectedObject(List<DetectedObject> detectedObject){
        detectedObjects.addAll(detectedObject);
    }

    public synchronized void addLidarCloudPoint(List<CloudPoint> cloudPoint){
        lidarCloudPoints.addAll(cloudPoint);
    }



}

