package bgu.spl.mics.application;

import bgu.spl.mics.application.objects.Pose;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReaderJsonPose {
    List<Pose> poses;
    String path;
    ReaderJsonPose(String path){
        this.path=path;
        poses=new ArrayList<>();
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

            // Parse the full JSON string into a JsonArray
            JsonArray jsonArray = gson.fromJson(jsonBuilder.toString(), JsonArray.class);
            // Process each JSON object in the array
            for (JsonElement element : jsonArray) {
                JsonObject jsonObject = element.getAsJsonObject();
                // Extract components
                int time = jsonObject.get("time").getAsInt();
                float x = jsonObject.get("x").getAsFloat();
                float y = jsonObject.get("y").getAsFloat();
                float yaw = jsonObject.get("yaw").getAsFloat();
                Pose p=new Pose(x,y,yaw,time);
                poses.add(p);

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Pose> getPoses() {
        return poses;
    }
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ReaderJsonPose{\n");
        builder.append("  Poses:\n");
        for (Pose pose : poses) {
            builder.append(String.format("    Pose [x=%.2f, y=%.2f, yaw=%.2f, time=%d]\n",
                    pose.getX(), pose.getY(), pose.getYaw(), pose.getTime()));
        }
        builder.append("}");
        return builder.toString();
    }
}
