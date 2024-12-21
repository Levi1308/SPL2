package bgu.spl.mics.application.objects;

import java.util.List;

/**
 * Represents a group of cloud points corresponding to a specific timestamp.
 * Used by the LiDAR system to store and process point cloud data for tracked objects.
 */
public class StampedCloudPoints {
    private final String id;
    private int time;
    private List<List<CloudPoint>> cloudPoints;


    public StampedCloudPoints(String id) {
        this.id = id;
    }
}
