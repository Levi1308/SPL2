package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * LiDarWorkerTracker is responsible for managing a LiDAR worker.
 * It processes DetectObjectsEvents and generates TrackedObjectsEvents by using data from the LiDarDataBase.
 * Each worker tracks objects and sends observations to the FusionSlam service.
 */
public class LiDarWorkerTracker {

    private int id;
    private STATUS status;
    private int frequency;
    List<TrackedObject> lastTrackedobjects;

    public LiDarWorkerTracker(int id, int frequency) {
        this.id = id;
        this.lastTrackedobjects = new ArrayList<>();
        this.frequency = frequency;
        status=STATUS.UP;
    }

    public int getId() {
        return id;
    }

    public int getFrequency() {
        return frequency;
    }

    public STATUS getStatus() {
        return status;
    }

    public List<TrackedObject> getLastTrackedobjects() {
        return lastTrackedobjects;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }
    public List<TrackedObject> onTick(int currentTick) {
        List<TrackedObject> lidarlist=getLastTrackedobjects();
        List<TrackedObject> allTrackedObj=new ArrayList<>();
        int frequency= getFrequency();
        for(TrackedObject obj:lidarlist){
            if(obj.getTime()+frequency==currentTick){
                allTrackedObj.add(obj);
            }
        }
        return allTrackedObj;
    }
    public void addTrackedObject(TrackedObject obj){
        lastTrackedobjects.add(obj);
        //במידה וגיליתי עצם חדש שכבר גיליתי אותו
        /*for(TrackedObject trackedObject:lastTrackedobjects)
        {
            if(trackedObject.getId()==obj.getId())
                trackedObject.
        }*/
    }
    private void processLiDARData(List<StampedCloudPoints> cloudPoints) {

    }


}
