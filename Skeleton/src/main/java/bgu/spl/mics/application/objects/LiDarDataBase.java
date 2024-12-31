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
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * LiDarDataBase is a singleton class responsible for managing LiDAR data.
 * It provides access to cloud point data and other relevant information for tracked objects.
 */
public class LiDarDataBase {
    private class LidarDataBaseHolder{
        private static LiDarDataBase instance = new LiDarDataBase();
    }
    ConcurrentLinkedQueue<StampedCloudPoints> cloudPoints;
    private int numberObjects;
    private LiDarDataBase(){
        cloudPoints = new ConcurrentLinkedQueue<>();
        numberObjects=0;
    }

    public int sizeOfCloudPoints(){
        return cloudPoints.size();
    }

    public static synchronized LiDarDataBase getInstance() {
        return LidarDataBaseHolder.instance;
    }

    public List<List<Double>> RetriveCloudPoints(DetectedObject object,int currentTime){
        for(StampedCloudPoints s:cloudPoints) {
            if (s.getId().equals(object.getId()) && s.getTime()==currentTime)
                return s.getCloudPoints();
        }
        return new ArrayList<>();
    }

    public synchronized int getNumberObjects() {
        return numberObjects;
    }

    public synchronized void DecreaseNumberObjects(){
        numberObjects--;
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
             // Process each JSON object in the array
            for (JsonElement element : jsonArray) {
                JsonObject jsonObject = element.getAsJsonObject();
                // Extract components
                int time = jsonObject.get("time").getAsInt();
                String id = jsonObject.get("id").getAsString();
                JsonArray cloudPointsArray = jsonObject.get("cloudPoints").getAsJsonArray();
                List<List<Double>> cloudPointslist = new ArrayList<>();
                for (JsonElement cPoint : cloudPointsArray) {
                    JsonArray point = cPoint.getAsJsonArray();
                    double x = point.get(0).getAsDouble();
                    double y = point.get(1).getAsDouble();
                    List<Double> doubleList=new ArrayList<>();
                    doubleList.add(x);
                    doubleList.add(y);
                    cloudPointslist.add(doubleList);
                }
                numberObjects++;
                StampedCloudPoints temp = new StampedCloudPoints(id, time);
                temp.AddCloudPoint(cloudPointslist);
                cloudPoints.add(temp);
                cloudPointslist.clear();
                if(cloudPoints.size()==13)
                    cloudPoints.toString();
            }
        }  catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("LiDarDataBase{\n");

        // Iterate over each StampedCloudPoints in the cloudPoints set
        for (StampedCloudPoints cloudPoint : cloudPoints) {
            builder.append("  id='").append(cloudPoint.getId()).append("': ")
                    .append(cloudPoint.toString()).append("\n");
        }

        builder.append("}");
        return builder.toString();
    }
    public boolean detectAll(){
        return numberObjects==0;
    }
}

