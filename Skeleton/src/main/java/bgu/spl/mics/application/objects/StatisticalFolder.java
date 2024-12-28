package bgu.spl.mics.application.objects;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * Holds statistical information about the system's operation.
 * This class aggregates metrics such as the runtime of the system,
 * the number of objects detected and tracked, and the number of landmarks identified.
 */
public class StatisticalFolder {
    private static StatisticalFolder instance;
    private static final Object lock = new Object();

    private final AtomicInteger systemRuntime;
    private final AtomicInteger numTrackedObjects;
    private final AtomicInteger numDetectedObjects;
    private final AtomicInteger numLandmarks;

    public StatisticalFolder() {
        this.systemRuntime = new AtomicInteger(0);
        this.numTrackedObjects = new AtomicInteger(0);
        this.numDetectedObjects = new AtomicInteger(0);
        this.numLandmarks = new AtomicInteger(0);
    }
    /**
     * Returns the single instance of StatisticalFolder.
     * Uses double-checked locking to ensure thread safety and performance.
     *
     * @return The singleton instance of StatisticalFolder.
     */
    public static StatisticalFolder getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new StatisticalFolder();
                }
            }
        }
        return instance;
    }
    /**
     * Increments the system runtime by one tick.
     */
    public void incrementRuntime() {
        systemRuntime.incrementAndGet();
    }

    /**
     * Increments the number of detected objects by a specific count.
     *
     * @param count Number of detected objects to add.
     */
    public void incrementDetectedObjects(int count) {
        numDetectedObjects.addAndGet(count);
    }

    /**
     * Increments the number of tracked objects by a specific count.
     *
     * @param count Number of tracked objects to add.
     */
    public void incrementTrackedObjects(int count) {
        numTrackedObjects.addAndGet(count);
    }

    /**
     * Increments the number of landmarks by a specific count.
     *
     * @param count Number of landmarks to add.
     */
    public void incrementLandmarks(int count) {
        numLandmarks.addAndGet(count);
    }

    /**
     * @return The current system runtime.
     */
    public int getSystemRuntime() {
        return systemRuntime.get();
    }

    /**
     * @return The total number of detected objects.
     */
    public int getNumDetectedObjects() {
        return numDetectedObjects.get();
    }

    /**
     * @return The total number of tracked objects.
     */
    public int getNumTrackedObjects() {
        return numTrackedObjects.get();
    }

    /**
     * @return The total number of landmarks.
     */
    public int getNumLandmarks() {
        return numLandmarks.get();
    }

    public void reset() {
        this.systemRuntime.set(0);
        this.numTrackedObjects.set(0);
        this.numDetectedObjects.set(0);
        this.numLandmarks.set(0);
    }
}
