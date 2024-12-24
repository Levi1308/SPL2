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
    private Pose currentPose;
    private Map<String, LandMark> landmarks;
    private List<Pose> Poses;
    private int Tick;
    private StatisticalFolder statisticalFolder = StatisticalFolder.getInstance();


    /**
     * Constructor for FusionSlam.
     * Initializes empty lists for landmarks and tracked objects.
     */
    public FusionSlam() {
        this.currentPose = new Pose(0, 0, 0,0);
        this.landmarks =  new HashMap<>();
        this.Poses = new ArrayList<>();
        this.Tick = 0;
    }

    public void setTick(int tick) {
        this.Tick = tick;
    }
    public int getTick() {return this.Tick;}

    public Pose getCurrentPose() {return this.currentPose;}

    public Map<String, LandMark> getLandmarks() {return this.landmarks;}

    public List<Pose> getPoses() {return this.Poses;}

    public Pose addPose(Pose pose) {
        this.Poses.add(pose);
        this.currentPose = pose;
        return pose;
    }

    public void doMapping(List<TrackedObject> trackedObjects) {
        for (TrackedObject obj : trackedObjects) {
            String id = obj.getId();

            // Convert coordinates to global system
            List<CloudPoint> globalPoints = convertToGlobal(obj.getCoordinate(), currentPose);

            if (landmarks.containsKey(id)) {
                // Update existing landmark
                LandMark existingLandmark = landmarks.get(id);
                existingLandmark.UpdateCoordinates(globalPoints);
            } else {
                // Add new landmark
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

        for (CloudPoint point : localPoints) {
            float globalX = point.getX() + pose.getX();
            float globalY = point.getY() + pose.getY();
            float globalZ = point.getZ() + point.getZ();
            globalPoints.add(new CloudPoint(globalX, globalY,globalZ));
        }
        return globalPoints;
    }





}

