package bgu.spl.mics.application.services;

import bgu.spl.mics.Event;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;


public class LiDarService extends MicroService {
    private LiDarWorkerTracker liDarTracker;
    private LiDarDataBase database;

    //LiDarWorkerTracker liDarWorkerTracker
    public LiDarService(LiDarWorkerTracker liDarTracker) {
        super("LidarWorkerService");
        this.liDarTracker = liDarTracker;
        database=LiDarDataBase.getInstance();
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, (TickBroadcast broadcast) -> {
            int currentTick = broadcast.getTick();
            onTick(currentTick);
            if (checkForSensorDisconnection()) {
                List<String> faultySensors = List.of(getName());
                sendBroadcast(new CrashedBroadcast("LiDAR disconnected", faultySensors));
                terminate();
            }
        });
        subscribeBroadcast(TerminatedBroadcast.class, (TerminatedBroadcast broadcast) -> {
            onTerminate();
        });
        subscribeBroadcast(CrashedBroadcast.class, crash -> {
            System.out.println(getName() + " terminating due to error: " + crash.getError());
            onTerminate();
            //int currentTick = broadcast.getTick();
            //handleTick(currentTick);
        });
        subscribeEvent(DetectObjectsEvent.class, (DetectObjectsEvent event) -> {
            onDetectedObject(event.getTime(),event.getDetectedObjects());
        });
    }

    private boolean checkForSensorDisconnection() {
        return liDarTracker.getStatus() == STATUS.ERROR;
    }

    public void onTick(int currentTick){
        sendEvent(new TrackedObjectsEvent(liDarTracker.onTick(currentTick)));
    }

    public void onTerminate(){
        if(liDarTracker.getLastTrackedobjects().isEmpty())
            sendBroadcast(new TerminatedBroadcast("LidarService"));
        terminate();
    }
    public void onCrash(){
        //if() need to shut down
    }

    public void onDetectedObject(int currentTime, List<DetectedObject> detectedObjectList){
        for (DetectedObject object: detectedObjectList)
        {
            if(object.getId()!="ERROR") {
                List<List<Double>> doublePoints = database.RetriveCloudPoints(object,currentTime);
                List<CloudPoint> cloudPoints = new ArrayList<>();
                for (List<Double> list : doublePoints)
                    cloudPoints.add(new CloudPoint(list.get(0), list.get(1)));
                TrackedObject trackedObject = new TrackedObject(object.getId(), currentTime, object.getDescription(), cloudPoints);
                liDarTracker.addTrackedObject(trackedObject);
            }
            //else
                //sendBroadcast(new CrashedBroadcast("Lidar",liDarTracker.getId(),"dsa"));
        }

    }

}
