package bgu.spl.mics.application.objects;

import java.util.*;

/**
 * LiDarDataBase is a singleton class responsible for managing LiDAR data.
 * It provides access to cloud point data and other relevant information for tracked objects.
 */
public class LiDarDataBase {
    private final Map<Integer, List<StampedCloudPoints>> cloudPointsMap;
    private static LiDarDataBase instance = null;
    /*
     * Returns the singleton instance of LiDarDataBase.
     *
     * @param filePath The path to the LiDAR data file.
     * @return The singleton instance of LiDarDataBase.
     */
    private LiDarDataBase(){cloudPointsMap = new HashMap<>();}

    /**
     * Returns the singleton instance of LiDarDataBase.
     *
     * @return The singleton instance of LiDarDataBase.
     */
    public static synchronized LiDarDataBase getInstance() {
        if (instance == null) {
            instance = new LiDarDataBase();
        }
        return instance;
    }

    /**
     * Adds stamped cloud points to the database.
     *
     * @param newPoint The stamped cloud point to add.
     */
    public void addStampedCloudPoints(StampedCloudPoints newPoint) {
        int tick = newPoint.getTime();
        cloudPointsMap.computeIfAbsent(tick, k -> new ArrayList<>()).add(newPoint);
    }

    /**
     * Retrieves all cloud points for a specific tick.
     *
     * @param tick The tick time to retrieve cloud points for.
     * @return List of stamped cloud points at the specified tick, or an empty list if none exist.
     */
    public List<StampedCloudPoints> getCloudPointsByTick(int tick) {
        return cloudPointsMap.getOrDefault(tick, Collections.emptyList());
    }

    /**
     * Retrieves all cloud points.
     *
     * @return A collection of all stamped cloud points.
     */
    public Collection<List<StampedCloudPoints>> getAllCloudPoints() {
        return cloudPointsMap.values();
    }

}
