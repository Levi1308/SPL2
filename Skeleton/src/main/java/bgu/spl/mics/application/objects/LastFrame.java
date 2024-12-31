package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;

public class LastFrame {
    private static class LastFrameHolder{
        private static LastFrame instance = new LastFrame();
    }
    private List<DetectedObject> detectedObjects;
    private List<CloudPoint> lidarCloudPoints;



    private LastFrame() {
        this.detectedObjects = new ArrayList<>();
        this.lidarCloudPoints = new ArrayList<>();

    }
    public static LastFrame getInstance(){
        return LastFrameHolder.instance;
    }

    public synchronized List<DetectedObject> getDetectedObjects() {
        return detectedObjects;
    }

    public synchronized List<CloudPoint> getLidarCloudPoints() {
        return lidarCloudPoints;
    }



    public synchronized void setDetectedObjects(List<DetectedObject> detectedObjects) {
        this.detectedObjects = detectedObjects;
    }

    public synchronized void setLidarCloudPoints(List<CloudPoint> lidarCloudPoints) {
        this.lidarCloudPoints = lidarCloudPoints;
    }

}

