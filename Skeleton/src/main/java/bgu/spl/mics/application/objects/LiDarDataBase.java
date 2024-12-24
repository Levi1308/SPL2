package bgu.spl.mics.application.objects;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * LiDarDataBase is a singleton class responsible for managing LiDAR data.
 * It provides access to cloud point data and other relevant information for tracked objects.
 */
public class LiDarDataBase {
    private class LidarDataBaseHolder{
        private static LiDarDataBase instance = new LiDarDataBase();
    }

    ConcurrentHashMap<Integer, StampedCloudPoints> cloudPointsMap;
    ConcurrentHashMap<Integer, StampedDetectedObjects> stampedDetectedObjectsMap;

    private LiDarDataBase(){
        cloudPointsMap = new ConcurrentHashMap<>();
        stampedDetectedObjectsMap=new ConcurrentHashMap<>();
        loadData();
    }

    public static synchronized LiDarDataBase getInstance() {
        return LidarDataBaseHolder.instance;
    }

    public void addNewDetectedObjects(List<DetectedObject> listObject,int currentTime){
        StampedDetectedObjects stampedDetectObj=stampedDetectedObjectsMap.getOrDefault(currentTime,null);
        if(stampedDetectObj==null){
            stampedDetectObj=new StampedDetectedObjects(currentTime,listObject);
            stampedDetectedObjectsMap.put(currentTime,stampedDetectObj);
        }
        else
            stampedDetectObj.AddDetectedObject(listObject);
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
                StampedCloudPoints temp = cloudPointsMap.getOrDefault(time, null);
                if (temp == null) {
                    temp = new StampedCloudPoints(id, time);
                    temp.AddCloudPoint(cloudPointslist);
                    cloudPointsMap.put(time,temp);
                } else
                    temp.AddCloudPoint(cloudPointslist);
            }
        }  catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

