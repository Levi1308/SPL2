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
            if (checkForSensorDisconnection()) {
                sendBroadcast(new CrashedBroadcast(currentTick,"LiDar disconnected","LiDar"+liDarTracker.getId()));
                LastFrame.getInstance().setLidarCloudPoints(liDarTracker.getCloudPointstill(currentTick));
                terminate();
            }
            onTick(currentTick);
        });
        subscribeBroadcast(TerminatedBroadcast.class, (TerminatedBroadcast broadcast) -> {
            onTerminate();
        });
        subscribeBroadcast(CrashedBroadcast.class, crash -> {
            System.out.println(getName() + " terminating due to error: " + crash.getError());
            if(LastFrame.getInstance().getLidarCloudPoints().isEmpty()){
                LastFrame.getInstance().setLidarCloudPoints(liDarTracker.getCloudPointstill(crash.getTime()));
            }
            onTerminate();
        });
        subscribeEvent(DetectObjectsEvent.class, (DetectObjectsEvent event) -> {
            onDetectedObject(event.getTime(),event.getDetectedObjects());
        });
    }

    private boolean checkForSensorDisconnection() {
        return liDarTracker.getStatus() == STATUS.ERROR;
    }

    public void onTick(int currentTick){
        if(!database.detectAll())
            sendEvent(new TrackedObjectsEvent(liDarTracker.onTick(currentTick)));
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
        DetectedObject error=liDarTracker.onDetectedObject(currentTime,detectedObjectList);
        if(error!=null){
            String faultySensor = "LiDar Disconnected";
            sendBroadcast(new CrashedBroadcast(currentTime,error.getDescription(), faultySensor));
            terminate();
        }
    }
}
