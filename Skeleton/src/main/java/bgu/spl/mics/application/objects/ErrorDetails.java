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
        this.error = null;
        this.faultySensor = null;
        this.poses = null;
        this.lastFrames = LastFrame.getInstance();
    }

    public static synchronized ErrorDetails getInstance() {
        return ErrorDetailsHolder.INSTANCE;
    }

    public void setError(String error, String faultySensor, List<Pose> poses) {
        this.error = error;
        this.faultySensor = faultySensor;
        this.poses = poses;
    }

    public String getError() {
        return error;
    }

    public String getFaultySensor() {
        return faultySensor;
    }

    public String toJson() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        if (error == null) {
            // Return an empty JSON if no error exists
            return "{}";
        } else {
            return gson.toJson(this);
        }
    }
}
