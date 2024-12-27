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
    Map<Integer, StampedDetectedObjects> stampedDetectedObjects;


    public ReaderJsonCamera(String path) {
        this.path = path;
        stampedDetectedObjects = new HashMap<>();

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
                JsonArray cameraEntries = cameraEntry.getValue().getAsJsonArray();
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
                    }
                    StampedDetectedObjects stampedobj = stampedDetectedObjects.getOrDefault(time, null);
                    if (stampedobj != null) {
                        stampedobj.AddDetectedObject(detectedObjectList);
                    } else {
                        stampedobj = new StampedDetectedObjects(time, detectedObjectList);
                        stampedDetectedObjects.put(time, stampedobj);
                    }
                }
            }
        } catch (IOException e) {
            //e.printStackTrace();
            throw new RuntimeException("Error reading the camera data file.", e);
        }
    }

    public Map<Integer,StampedDetectedObjects> getStampedDetectedObjects() {
        return stampedDetectedObjects;
    }
}
