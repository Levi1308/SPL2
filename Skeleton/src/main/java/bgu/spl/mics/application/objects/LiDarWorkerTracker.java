package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.TrackedObjectsEvent;

import java.util.ArrayList;
import java.util.Collections;
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
    StatisticalFolder statisticalFolder;
    LiDarDataBase database;



    public LiDarWorkerTracker(int id, int frequency) {
        this.id = id;
        this.lastTrackedobjects = Collections.synchronizedList(new ArrayList<>());
        this.frequency = frequency;
        status=STATUS.UP;
        statisticalFolder=StatisticalFolder.getInstance();
        database=LiDarDataBase.getInstance();

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

    public List<TrackedObject> getTrackedObjects(int tick) {
        List<TrackedObject> trackedObjects=new ArrayList<>();
        trackedObjects.add(lastTrackedobjects.get(tick));
        return trackedObjects;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public List<TrackedObject> onTick(int currentTick) {
        List<TrackedObject> lidarlist=getLastTrackedobjects();
        List<TrackedObject> allTrackedObj=new ArrayList<>();
        int frequency= getFrequency();
        for(TrackedObject obj:lidarlist){
            if(obj.getTime()+frequency<=currentTick){
                allTrackedObj.add(obj);
            }
        }
        return allTrackedObj;
    }
    public void addTrackedObject(TrackedObject obj){
        lastTrackedobjects.add(obj);

    }
    private void processLiDARData(List<StampedCloudPoints> cloudPoints) {

    }
    public DetectedObject onDetectedObject(int currentTime, List<DetectedObject> detectedObjectList) {
        if (detectedObjectList == null || detectedObjectList.isEmpty()) {
            System.out.println("No objects detected at tick " + currentTime);
            return null;
        }

        for (DetectedObject object : detectedObjectList) {
            if (object.getId().equals("ERROR")) {
                System.out.println("Error detected in object at tick " + currentTime + ": " + object.getDescription());
                return object; // Return immediately if an error object is detected
            }

            // Process non-error objects
            List<List<Double>> doublePoints = database.RetriveCloudPoints(object, currentTime);
            if (doublePoints == null || doublePoints.isEmpty()) {
                System.out.println("No cloud points retrieved for object " + object.getId() + " at tick " + currentTime);
                continue; // Skip this object if no cloud points are available
            }

            List<CloudPoint> cloudPoints = new ArrayList<>();
            for (List<Double> list : doublePoints) {
                if (list.size() < 2) {
                    System.out.println("Invalid cloud point data for object " + object.getId());
                    continue; // Skip invalid data
                }
                cloudPoints.add(new CloudPoint(list.get(0), list.get(1)));
            }

            TrackedObject trackedObject = new TrackedObject(object.getId(), currentTime, object.getDescription(), cloudPoints);
            lastTrackedobjects.add(trackedObject); // Add to tracked objects
            database.DecreaseNumberObjects(); // Update database count
            statisticalFolder.incrementTrackedObjects(1); // Increment tracked object count
            System.out.println("Tracked object added: " + trackedObject);
        }
        return null; // Return null if no errors were encountered
    }


}
