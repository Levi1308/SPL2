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

    ConcurrentHashMap<String, StampedCloudPoints> cloudPointsMap;

    private LiDarDataBase(){
        cloudPointsMap = new ConcurrentHashMap<>();
    }

    public static synchronized LiDarDataBase getInstance() {
        return LidarDataBaseHolder.instance;
    }

    public List<List<Double>> RetriveCloudPoints(DetectedObject object){
        StampedCloudPoints s=cloudPointsMap.getOrDefault(object.getId(),null);
        if(s!=null)
            return s.getCloudPoints();
        return null;
    }



    public void loadData(String path) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            Gson gson = new Gson();
            StringBuilder jsonBuilder = new StringBuilder();

            // Read the JSON file line-by-line
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line.trim());
            }

            // Parse the full JSON string into a JsonArray
            JsonArray jsonArray = gson.fromJson(jsonBuilder.toString(), JsonArray.class);
            List<Double> cloudPointslist = new ArrayList<>();
            // Process each JSON object in the array
            for (JsonElement element : jsonArray) {
                JsonObject jsonObject = element.getAsJsonObject();
                // Extract components
                int time = jsonObject.get("time").getAsInt();
                String id = jsonObject.get("id").getAsString();
                JsonArray cloudPointsArray = jsonObject.get("cloudPoints").getAsJsonArray();

                for (JsonElement j : cloudPointsArray) {
                    cloudPointslist.add(j.getAsDouble());
                }
                StampedCloudPoints temp = cloudPointsMap.getOrDefault(time, null);
                if (temp == null) {
                    temp = new StampedCloudPoints(id, time);
                    temp.AddCloudPoint(cloudPointslist);
                    cloudPointsMap.put(id,temp);
                } else
                    temp.AddCloudPoint(cloudPointslist);
            }
        }  catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

