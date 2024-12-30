package bgu.spl.mics.application.objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

public class ErrorDetails {
    private static class ErrorDetailsHolder {
        private static final ErrorDetails INSTANCE = new ErrorDetails();
    }

    private String error;
    private String faultySensor;
    private LastFrame lastFrames;
    private List<Pose> poses;

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

    // Reset error details to null if no error occurs
    public synchronized void reset() {
        this.error = null;
        this.faultySensor = null;
        this.poses = null;
    }

    public String getError() {
        return error;
    }

    public String getFaultySensor() {
        return faultySensor;
    }

    public synchronized String toJson() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return error == null ? "{}" : gson.toJson(this);
    }
}
