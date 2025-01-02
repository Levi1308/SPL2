package bgu.spl.mics.application.objects;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

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
    private ErrorDetails errorDetails = ErrorDetails.getInstance();
    private ConcurrentHashMap<Integer, TrackedObject> lastFrameMap = new ConcurrentHashMap<>();




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
        if (!lidarlist.isEmpty()) {
            TrackedObject lastTracked = lidarlist.get(lidarlist.size() - 1);
            lastFrameMap.put(this.getId(), lastTracked);
            errorDetails.addLastLiDarFrame("LiDarWorkerTracker" + this.getId(), new ArrayList<>(lastFrameMap.values()));
        }
        List<TrackedObject> allTrackedObj=new ArrayList<>();
        int frequency= getFrequency();
        for(TrackedObject obj:lidarlist){
            if(obj.getTime()+frequency<=currentTick){
                allTrackedObj.add(obj);
            }
        }

        for(TrackedObject t:allTrackedObj)
        {
            lastTrackedobjects.remove(t);
        }
        return allTrackedObj;
    }
    public void addTrackedObject(TrackedObject obj){
        lastTrackedobjects.add(obj);
        lastFrameMap.put(obj.getTime(), obj);

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
            if (doublePoints.isEmpty()) {
                System.out.println("No cloud points retrieved for object " + object.getId() + " at tick " + currentTime);
                continue; // Skip this object if no cloud points are available
            }

            List<CloudPoint> cloudPoints = new ArrayList<>();
            for (List<Double> list : doublePoints) {
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

    public boolean onDetectedObject2(int currentTime, List<DetectedObject> detectedObjectList) {
        for (DetectedObject object : detectedObjectList) {
            if (!object.getId().equals("ERROR")) {
                List<List<Double>> doublePoints = database.RetriveCloudPoints(object, currentTime);
                List<CloudPoint> cloudPoints = new ArrayList<>();
                for (List<Double> list : doublePoints)
                    cloudPoints.add(new CloudPoint(list.get(0), list.get(1)));
                TrackedObject trackedObject = new TrackedObject(object.getId(), currentTime, object.getDescription(), cloudPoints);
                addTrackedObject(trackedObject);
                database.DecreaseNumberObjects(); // Update database count
                statisticalFolder.incrementTrackedObjects(1); // Increment tracked object count
                System.out.println("Tracked object added: " + trackedObject);
            } else {
                return false;
                }
        }
        return true;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }
}
