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
    private Map<String ,LandMark> landsmark;
    private List<Pose> poses;
    private StatisticalFolder stats;
    boolean created=false;
    private FusionSlam(){
        landsmark=new HashMap<>();
        poses =new ArrayList<>();
        stats=new StatisticalFolder();
    }


    public List<Pose> getPoses() {
        return poses;
    }

    public Map<String, LandMark> getLandsmark() {
        return landsmark;
    }

    public StatisticalFolder getStats() {
        return stats;
    }


    public void AddPose(Pose pose){
        poses.add(pose);
    }
    public void AddLandMark(LandMark landmark){
        landsmark.put(landmark.getId(),landmark);
    }
    public void TrackedObjectEvent(TrackedObject trackedObject){
        if(landsmark.containsKey(trackedObject.getId())){
            LandMark templandMark=new LandMark(trackedObject);
            landsmark.put(templandMark.getId(), templandMark);
        }
        else
        {
            landsmark.get(trackedObject.getId()).UpdateCoordinates(trackedObject.getCoordinate());
        }

    }
    public void UpdateMap(){

    }
    public void CoordinateTransformation(){

    }
    public void PoseEvent(){

    }
    public void TrackedObjectEvent(){

    }
    private static class FusionSlamHolder {
        private FusionSlam Instance=null;
        private FusionSlamHolder()
        {

        }
        public FusionSlam getInstance(){
            if(Instance==null)
            {
                Instance=new FusionSlam();
                return Instance;
            }
            return Instance;
        }
    }
}
