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

    public List<DetectedObject> getDetectedObjects() {
        return detectedObjects;
    }

    public List<CloudPoint> getLidarCloudPoints() {
        return lidarCloudPoints;
    }



    public void setDetectedObjects(List<DetectedObject> detectedObjects) {
        this.detectedObjects = detectedObjects;
    }

    public void setLidarCloudPoints(List<CloudPoint> lidarCloudPoints) {
        this.lidarCloudPoints = lidarCloudPoints;
    }

}

