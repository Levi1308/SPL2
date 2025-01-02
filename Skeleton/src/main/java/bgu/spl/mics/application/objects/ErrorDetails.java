package bgu.spl.mics.application.objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ErrorDetails {
    private static class ErrorDetailsHolder {
        private static final ErrorDetails INSTANCE = new ErrorDetails();
    }

    private volatile String error;
    private volatile String faultySensor;

    @Expose
    private Map<String, StampedDetectedObjects> lastCamerasFrame = new HashMap<>();

    @Expose
    private Map<String, List<TrackedObject>> lastLiDarWorkerTrackersFrame = new HashMap<>();

    @Expose
    private List<Pose> poses;
    @Expose
    private SimulationOutput simulationOutput;

    // Private constructor for Singleton
    private ErrorDetails() {
        reset();
    }

    public static synchronized ErrorDetails getInstance() {
        return ErrorDetailsHolder.INSTANCE;
    }

    public synchronized void setError(String error, String faultySensor, List<Pose> poses) {
        this.error = error;
        this.faultySensor = faultySensor;
        this.poses = poses;
    }

    public synchronized void addLastCameraFrame(String id, StampedDetectedObjects detectedObjects) {
        lastCamerasFrame.put(id, detectedObjects);

    }

    public synchronized void addLastLiDarFrame(String lidarId, List<TrackedObject> trackedObjects) {
        if (trackedObjects != null && !trackedObjects.isEmpty()) {
            lastLiDarWorkerTrackersFrame.put(lidarId, trackedObjects);
        }
    }



    public synchronized void reset() {
        this.error = null;
        this.faultySensor = null;
        this.poses = null;
        this.lastCamerasFrame.clear();
        this.lastLiDarWorkerTrackersFrame.clear();
    }

    public String getError() {
        return error;
    }

    public String getFaultySensor() {
        return faultySensor;
    }

    public String toJson() {
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .setPrettyPrinting()
                .create();
        return error == null ? "{}" : gson.toJson(this);
    }

    public void setSimulationOutput(SimulationOutput simulationOutput) {
        this.simulationOutput = simulationOutput;
    }

    public void resteLastCamera() {
        this.lastCamerasFrame.clear();
    }

    public void resteLastLiDar() {
        this.lastLiDarWorkerTrackersFrame.clear();
    }

    public List<TrackedObject> getLastLiDarWorkerTrackersFrame(String lidarId) {
        return lastLiDarWorkerTrackersFrame.get(lidarId);
    }


}
