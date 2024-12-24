package bgu.spl.mics.application.services;

import bgu.spl.mics.Event;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.PoseEvent;
import bgu.spl.mics.application.objects.CloudPoint;
import bgu.spl.mics.application.objects.GPSIMU;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.Pose;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * PoseService is responsible for maintaining the robot's current pose (position and orientation)
 * and broadcasting PoseEvents at every tick.
 */
public class PoseService extends MicroService {
 private GPSIMU gpsimu;
 List<Pose> poses;
    public PoseService(GPSIMU gpsimu) {
        super("PoseService");
        this.gpsimu=gpsimu;
        poses=new ArrayList<>();
        loadData();
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, (TickBroadcast broadcast) -> {
            int currentTick = broadcast.getTick();
            this.gpsimu.setCurrentTick(broadcast.getTick());
            onTick(currentTick);

        });
    }

    public void onTick(int currentTick){
        gpsimu.setCurrentTick(currentTick);
        try{
            Pose temp=poses.get(currentTick);
            gpsimu.addPose(temp);
            PoseEvent poseEvent=new PoseEvent(temp);
            sendEvent(poseEvent);
        }
        catch(Exception ex)
        {
            //finish with all th poses;
        }
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
                    int x = jsonObject.get("x").getAsInt();
                    int y = jsonObject.get("y").getAsInt();
                    int yaw = jsonObject.get("yaw").getAsInt();
                    Pose p=new Pose(x,y,yaw,time);
                    poses.add(p);

                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
