package bgu.spl.mics.application.objects;

import java.util.List;
import java.util.Map;

public class ErrorDetails {
    private String error;
    private List<String> faultySensor;
    private Map<String, List<DetectedObject>> lastFrames;
    private List<Pose> poses;
    private StatisticalFolder statistics = StatisticalFolder.getInstance();;

    public ErrorDetails(String error, List<String> faultySensor, Map<String, List<DetectedObject>> lastFrames, List<Pose> poses, StatisticalFolder statistics) {
        this.error = error;
        this.faultySensor = faultySensor;
        this.lastFrames = lastFrames;
        this.poses = poses;
    }
}
