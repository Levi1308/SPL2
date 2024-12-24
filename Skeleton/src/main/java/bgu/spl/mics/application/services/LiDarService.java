package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
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
        });
        subscribeBroadcast(TerminatedBroadcast.class, (TerminatedBroadcast broadcast) -> {
            //int currentTick = broadcast.getTick();
            //handleTick(currentTick);
        });
        subscribeBroadcast(CrashedBroadcast.class, (CrashedBroadcast broadcast) -> {
            //int currentTick = broadcast.getTick();
            //handleTick(currentTick);
        });
        subscribeEvent(DetectObjectsEvent.class, (DetectObjectsEvent event) -> {
            onDetectedObject(event.getTime(),event.getDetectedObjects());
        });
    }

    public void onTick(int currentTick){

    }
    public void onTerminate(){

    }
    public void onCrash(){

    }
    public void onDetectedObject(int currentTick,List<DetectedObject> detectedObjectList){
        liDarTracker.onDetectObjectsEvent(currentTick,detectedObjectList);
    }

}
