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
    ConcurrentHashMap<Integer, StampedCloudPoints> stampedCloudPoints;

    //LiDarWorkerTracker liDarWorkerTracker
    public LiDarService(LiDarWorkerTracker liDarTracker) {
        super("LidarWorkerService");
        this.liDarTracker = liDarTracker;
        stampedCloudPoints = new ConcurrentHashMap<>();
        loadData();
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
        subscribeEvent(DetectObjectsEvent.class, (DetectObjectsEvent broadcast) -> {
            //int currentTick = broadcast.getTick();
            //handleTick(currentTick);
        });
    }

    public void onTick(int currentTick){ .

    }

    public void loadData() {
        try (BufferedReader reader = new BufferedReader(new FileReader("example input/lidar_data.json"))) {
            Gson gson = new Gson();
            StringBuilder jsonBuilder = new StringBuilder();

            // Read the JSON file line-by-line
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line.trim());
            }

            // Parse the full JSON string into a JsonArray
            JsonArray jsonArray = gson.fromJson(jsonBuilder.toString(), JsonArray.class);
            List<CloudPoint> cloudPointslist = new ArrayList<>();
            // Process each JSON object in the array
            for (JsonElement element : jsonArray) {
                JsonObject jsonObject = element.getAsJsonObject();
                // Extract components
                int time = jsonObject.get("time").getAsInt();
                String id = jsonObject.get("id").getAsString();
                JsonArray cloudPointsArray = jsonObject.get("cloudPoints").getAsJsonArray();
                for (JsonElement j : cloudPointsArray) {
                    CloudPoint c = new CloudPoint(cloudPointsArray.get(0).getAsInt(), cloudPointsArray.get(1).getAsInt(), cloudPointsArray.get(2).getAsInt());
                    cloudPointslist.add(c);
                }
                StampedCloudPoints temp = stampedCloudPoints.getOrDefault(time, null);
                if (temp == null) {
                    temp = new StampedCloudPoints(id, time);
                    temp.AddCloudPoint(cloudPointslist);
                } else
                    temp.AddCloudPoint(cloudPointslist);
                stampedCloudPoints.put(time,temp);

            }
        }  catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
