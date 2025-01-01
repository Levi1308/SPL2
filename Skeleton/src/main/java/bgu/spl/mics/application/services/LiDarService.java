package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.*;

import java.util.List;


public class LiDarService extends MicroService {
    private LiDarWorkerTracker liDarTracker;
    private LiDarDataBase database;
    private ErrorDetails errorDetails;

    //LiDarWorkerTracker liDarWorkerTracker
    public LiDarService(LiDarWorkerTracker liDarTracker) {
        super("LidarWorkerService");
        this.liDarTracker = liDarTracker;
        database=LiDarDataBase.getInstance();
        errorDetails = ErrorDetails.getInstance();
    }
    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, (TickBroadcast broadcast) -> {
            int currentTick = broadcast.getTick();
            if (checkForSensorDisconnection()) {
                errorDetails.setError("LiDar disconnected", "LiDar"+liDarTracker.getId(), FusionSlam.getInstance().getPosesTill(currentTick));
                errorDetails.addLastLiDarFrame("LiDarWorkerTracker"+liDarTracker.getId(),liDarTracker.getTrackedObjects(currentTick));
                sendBroadcast(new CrashedBroadcast(currentTick,"LiDar disconnected","LiDar"+liDarTracker.getId()));

                terminate();
            }
            onTick(currentTick);
        });
        subscribeBroadcast(TerminatedBroadcast.class, (TerminatedBroadcast broadcast) -> {
            onTerminate();
        });
        subscribeBroadcast(CrashedBroadcast.class, crash -> {
            System.out.println(getName() + " terminating due to error: " + crash.getError());
            //errorDetails.setError(crash.getError(), crash.getFaultySensor(), FusionSlam.getInstance().getPosesTill(crash.getTime()));
            errorDetails.addLastLiDarFrame("LiDarWorkerTracker"+liDarTracker.getId(),liDarTracker.getTrackedObjects(crash.getTime()));
            onTerminate();
        });
        subscribeEvent(DetectObjectsEvent.class, (DetectObjectsEvent event) -> {
            onDetectedObject2(event.getTime(),event.getDetectedObjects());
        });
    }

    private boolean checkForSensorDisconnection() {
        return liDarTracker.getStatus() == STATUS.ERROR;
    }

    public void onTick(int currentTick){
        if(!database.detectAll()) {
            List<TrackedObject> objectList = liDarTracker.onTick(currentTick);
            sendEvent(new TrackedObjectsEvent(objectList));
            System.out.println("Lidar send new Tracked Objects");
        }
        else
        {
            System.out.println("The Lidar "+liDarTracker.getId()+" detect all object. Terminating at tick" +currentTick);
            terminate();
        }

    }
    public void onTerminate() {
        System.out.println(getName() + " is terminating.");
        if (liDarTracker.getLastTrackedobjects().isEmpty()) {
            sendBroadcast(new TerminatedBroadcast("LiDarService " + getName()));
        }
        System.out.println("Final tracked objects: " + liDarTracker.getLastTrackedobjects().size());
        terminate();
    }


    public void onDetectedObject(int currentTime, List<DetectedObject> detectedObjectList){
        if(!detectedObjectList.isEmpty()) {
            DetectedObject error=liDarTracker.onDetectedObject(currentTime,detectedObjectList);
        if(error!=null){
            String faultySensor = "LiDar Disconnected";
            sendBroadcast(new CrashedBroadcast(currentTime,error.getDescription(), faultySensor));
            terminate();
        }
    }
    }
    public void onDetectedObject2(int currentTime, List<DetectedObject> detectedObjectList) {
        if(!liDarTracker.onDetectedObject2(currentTime,detectedObjectList))
        {
            sendBroadcast(new CrashedBroadcast(currentTime, "Lidar" + liDarTracker.getId(), errorDetails.getError()));
        }
    }
}
