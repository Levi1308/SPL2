package bgu.spl.mics.application;


import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;



/**
 * The main entry point for the GurionRock Pro Max Ultra Over 9000 simulation.
 * <p>
 * This class initializes the system and starts the simulation by setting up
 * services, objects, and configurations.
 * </p>
 */
public class GurionRockRunner {

    /**
     * The main method of the simulation.
     * This method sets up the necessary components, parses configuration files,
     * initializes services, and starts the simulation.
     *
     * @param args Command-line arguments. The first argument is expected to be the path to the configuration file.
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please provide the config file path.");
            return;
        }
        List<Camera> cameraList=new ArrayList<>();
        List<LiDarWorkerTracker> liDarWorkerTrackerList=new ArrayList<>();

        String configFile = args[0];
        try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
            Gson gson = new Gson();
            StringBuilder jsonBuilder = new StringBuilder();

            // Read the JSON file line-by-line
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line.trim());
            }
            File file = new File(configFile);
            String parentDir = file.getParent();

            // Parse the full JSON string into a JsonObject
            JsonObject configObject = gson.fromJson(jsonBuilder.toString(), JsonObject.class);

            // Extracting data from the "Cameras" section
            JsonObject cameras = configObject.getAsJsonObject("Cameras");
            JsonArray camerasConfigurations = cameras.getAsJsonArray("CamerasConfigurations");
            String cameraDataPath = cameras.get("camera_datas_path").getAsString();
            String path=parentDir.concat(cameraDataPath);
            path=path.replaceFirst("[.]","");
            ReaderJsonCamera readerCamera=new ReaderJsonCamera(path);

            // Iterate through the CameraConfigurations array
            for (JsonElement cameraElement : camerasConfigurations) {
                JsonObject cameraConfig = cameraElement.getAsJsonObject();
                int cameraId = cameraConfig.get("id").getAsInt();
                int cameraFrequency = cameraConfig.get("frequency").getAsInt();
                String cameraKey = cameraConfig.get("camera_key").getAsString();
                Camera c=new Camera(cameraId,cameraFrequency);
                c.setDetectedObjectsList(readerCamera.getStampedDetectedObjects());
                cameraList.add(c);
            }

            for(Camera c: cameraList)
            {
                CameraService cameraService=new CameraService(c);
            }

            // Extracting data from the "LidarWorkers" section
            JsonObject lidarWorkers = configObject.getAsJsonObject("LidarWorkers");
            JsonArray lidarConfigurations = lidarWorkers.getAsJsonArray("LidarConfigurations");
            String lidarDataPath = lidarWorkers.get("lidars_data_path").getAsString();
            path=parentDir.concat(lidarDataPath);
            path=path.replaceFirst("[.]","");
            LiDarDataBase liDarDataBase=LiDarDataBase.getInstance();
            liDarDataBase.loadData(path);

            // Iterate through the LidarConfigurations array
            for (JsonElement lidarElement : lidarConfigurations) {
                JsonObject lidarConfig = lidarElement.getAsJsonObject();
                int lidarId = lidarConfig.get("id").getAsInt();
                int lidarFrequency = lidarConfig.get("frequency").getAsInt();
                LiDarWorkerTracker lidar=new LiDarWorkerTracker(lidarId,lidarFrequency);
                liDarWorkerTrackerList.add(lidar);
            }
            for(LiDarWorkerTracker lidar: liDarWorkerTrackerList)
            {
                LiDarService lidarService=new LiDarService(lidar);
            }

            String poseJsonFile = configObject.get("poseJsonFile").getAsString();
            path=parentDir.concat(poseJsonFile);
            path=path.replaceFirst("[.]","");
            ReaderJsonPose readerJsonPose=new ReaderJsonPose(path);

            PoseService poseService=new PoseService(new GPSIMU());
            poseService.setPoses(readerJsonPose.getPoses());

            int tickTime = configObject.get("TickTime").getAsInt();
            int duration = configObject.get("Duration").getAsInt();


            TimeService timeService=new TimeService(tickTime,duration);
            FusionSlamService fusionSlamService=new FusionSlamService(new FusionSlam());

            //start simulation
            timeService.run();

        } catch (IOException e) {

        }
    }
}
