package bgu.spl.mics.application;

import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.StampedDetectedObjects;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReaderJsonCamera {
    String path;
    Map<String,Map<Integer, StampedDetectedObjects>> cameras;
    Map<String,Integer> numberObject;

    public ReaderJsonCamera(String path) {
        this.path = path;
        cameras = new HashMap<>();
        numberObject=new HashMap<>();
        loadData();
    }

    public void loadData() {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            Gson gson = new Gson();
            StringBuilder jsonBuilder = new StringBuilder();

            // Read the JSON file line-by-line
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line.trim());
            }

            // Parse the JSON into a JsonObject
            JsonObject cameraData = gson.fromJson(jsonBuilder.toString(), JsonObject.class);

            for (Map.Entry<String, JsonElement> cameraEntry : cameraData.entrySet()) {
                String cameraKey = cameraEntry.getKey();
                Map<Integer, StampedDetectedObjects> stampedDetectedObjects=new HashMap<>();
                JsonArray cameraEntries = cameraEntry.getValue().getAsJsonArray();
                numberObject.put(cameraKey,0);
                for (JsonElement entryElement : cameraEntries) {
                    JsonObject entry = entryElement.getAsJsonObject();
                    int time = entry.get("time").getAsInt();
                    JsonArray detectedObjects = entry.getAsJsonArray("detectedObjects");
                    List<DetectedObject> detectedObjectList = new ArrayList<>();
                    for (JsonElement objectElement : detectedObjects) {
                        JsonObject detectedObject = objectElement.getAsJsonObject();
                        String id = detectedObject.get("id").getAsString();
                        String description = detectedObject.get("description").getAsString();
                        DetectedObject obj = new DetectedObject(id, description);
                        detectedObjectList.add(obj);
                        numberObject.put(cameraKey, numberObject.get(cameraKey) + 1);
                    }
                    StampedDetectedObjects stampedobj = stampedDetectedObjects.getOrDefault(time, null);
                    if (stampedobj != null) {
                        stampedobj.AddDetectedObject(detectedObjectList);
                    } else {
                        stampedobj = new StampedDetectedObjects(time, detectedObjectList);
                        stampedDetectedObjects.put(time, stampedobj);
                    }
                }
                cameras.put(cameraKey,stampedDetectedObjects);
            }
        } catch (IOException e) {
            //e.printStackTrace();
            throw new RuntimeException("Error reading the camera data file.", e);
        }
    }

    public Map<Integer,StampedDetectedObjects> getStampedDetectedObjects(String camera) {
        return cameras.get(camera);
    }

    public Integer getNumberObject(String camera) {
        return numberObject.get(camera);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ReaderJsonCamera{")
                .append("path='").append(path).append('\'')
                .append(", cameras={");

        for (Map.Entry<String, Map<Integer, StampedDetectedObjects>> cameraEntry : cameras.entrySet()) {
            builder.append("\n  Camera=").append(cameraEntry.getKey())
                    .append(": ");

            Map<Integer, StampedDetectedObjects> stampedObjects = cameraEntry.getValue();
            for (Map.Entry<Integer, StampedDetectedObjects> entry : stampedObjects.entrySet()) {
                builder.append("\n    Time=").append(entry.getKey())
                        .append(": ").append(entry.getValue().toString());
            }
        }
        builder.append("\n}}");

        return builder.toString();
    }


}
