package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages the fusion of sensor data for simultaneous localization and mapping (SLAM).
 * Combines data from multiple sensors (e.g., LiDAR, camera) to build and update a global map.
 * Implements the Singleton pattern to ensure a single instance of FusionSlam exists.
 */
public class FusionSlam {
    private static class FusionSlamHolder{
        private static FusionSlam INSTANCE = new FusionSlam();
    }
    private Pose currentPose;
    private Map<String, LandMark> landmarks;
    private Map<Integer,Pose> Poses;
    private int Tick;
    private StatisticalFolder statisticalFolder = StatisticalFolder.getInstance();


    /**
     * Constructor for FusionSlam.
     * Initializes empty lists for landmarks and tracked objects.
     */
    private FusionSlam() {
        this.currentPose = new Pose(0, 0, 0,0);
        this.landmarks =  new HashMap<>();
        this.Poses = new HashMap<>();
        this.Tick = 0;
    }

    /**
     * Returns the singleton instance of FusionSlam.
     */
    public static FusionSlam getInstance() {
        return FusionSlamHolder.INSTANCE;
    }

    public void setTick(int tick) {
        this.Tick = tick;
    }
    public int getTick() {return this.Tick;}

    public Pose getCurrentPose() {return this.currentPose;}

    public void setCurrentPose(Pose currentPose) {
        this.currentPose = currentPose;
    }

    public synchronized Map<String, LandMark> getLandmarks() {
        return new HashMap<>(this.landmarks);  // Return a copy to avoid concurrency issues
    }


    public HashMap<Integer, Pose> getPoses() {
        return new HashMap<>(this.Poses);
    }

    public Pose addPose(Pose pose) {
        this.Poses.put(pose.getTime(), pose);
        this.currentPose = pose;
        return pose;
    }

    public List<Pose> getPosesTill(int tick){
        List<Pose> poses = new ArrayList<>();
        for(Pose pose : this.Poses.values()){
            if(tick >= pose.getTime()){
                poses.add(pose);
            }
        }
        return poses;
    }

    public void doMapping(List<TrackedObject> trackedObjects) {
        for (TrackedObject obj : trackedObjects) {
            if (obj.getCoordinate() == null || obj.getCoordinate().isEmpty()) {
                System.out.println("TrackedObject " + obj.getId() + " has no coordinates.");
                continue;
            }

            Pose mappingPose = getPoses().get(obj.getTime());
            if (mappingPose == null) {
                System.out.println("No matching pose for tracked object " + obj.getId() + " at tick " + obj.getTime());
                continue;
            }

            List<CloudPoint> globalPoints = convertToGlobal(obj.getCoordinate(), mappingPose);
            String id = obj.getId();

            if (landmarks.containsKey(id)) {
                System.out.println("Updating landmark " + id);
                LandMark existingLandmark = landmarks.get(id);
                existingLandmark.UpdateCoordinates(globalPoints);
            } else {
                System.out.println("Adding new landmark " + id);
                LandMark newLandmark = new LandMark(id, obj.getDescription(), globalPoints);
                landmarks.put(id, newLandmark);
                statisticalFolder.incrementLandmarks(1);
            }
        }
    }



    /**
     * Converts local LiDAR coordinates to the global coordinate system.
     */
    private List<CloudPoint> convertToGlobal(List<CloudPoint> localPoints, Pose pose) {
        List<CloudPoint> globalPoints = new ArrayList<>();

        double yawRad = Math.toRadians(pose.getYaw());
        double cosTheta = Math.cos(yawRad);
        double sinTheta = Math.sin(yawRad);

        for (CloudPoint point : localPoints) {
            double globalX = cosTheta * point.getX() - sinTheta * point.getY() + pose.getX();
            double globalY = sinTheta * point.getX() + cosTheta * point.getY() + pose.getY();
            globalPoints.add(new CloudPoint(globalX, globalY));

            //System.out.println("Converted local point (" + point.getX() + ", " + point.getY() + ") " +
                   // "to global point (" + globalX + ", " + globalY + ")" + " with pose: " + pose.getX() + ", " + pose.getY() );
        }
        return globalPoints;
    }

}