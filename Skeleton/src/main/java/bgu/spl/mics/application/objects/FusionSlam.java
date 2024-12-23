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
    private final List<LandMark> landmarks;
    private List<Pose> poses;
    private final List<TrackedObject> trackedObjects;


    /**
     * Constructor for FusionSlam.
     * Initializes empty lists for landmarks, tracked objects, and pose history.
     */
    private FusionSlam(){
        this.landmarks = new ArrayList<>();
        this.trackedObjects = new ArrayList<>();
        this.poses = new ArrayList<>();
    }

    /**
     * @return The list of all landmarks in the global map.
     */
    public List<LandMark> getLandmarks() {
        return landmarks;
    }


    /**
     * @return The pose history of the robot.
     */
    public List<Pose> getPoseHistory() {
        return poses;
    }

}
