package bgu.spl.mics.application;


import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;





/**
 * The main entry point for the GurionRock Pro Max Ultra Over 9000 simulation.
 * <p>
 * This class initializes the system and starts the simulation by setting up
 * services, objects, and configurations.
 * </p>
 */
public class GurionRockRunner {


    public static void saveOutputFile(SimulationOutput output, String outputPath) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(outputPath)) {
            gson.toJson(output, writer);
            System.out.println("Simulation results saved to: " + outputPath);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to save simulation results.");
        }
    }
    public static void saveErrorFile(ErrorDetails errorDetails, String outputPath) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(outputPath)) {
            gson.toJson(errorDetails, writer);
            System.out.println("Error details saved to: " + outputPath);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to save error details.");
        }
    }
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
        List<Thread> threads = new ArrayList<>();
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
            path = parentDir + File.separator + cameraDataPath.replaceFirst("^\\.", "");


            ReaderJsonCamera readerCamera=new ReaderJsonCamera(path);

            //System.out.println(readerCamera.toString());
            // Iterate through the CameraConfigurations array
            for (JsonElement cameraElement : camerasConfigurations) {
                JsonObject cameraConfig = cameraElement.getAsJsonObject();
                int cameraId = cameraConfig.get("id").getAsInt();
                int cameraFrequency = cameraConfig.get("frequency").getAsInt();
                String cameraKey = cameraConfig.get("camera_key").getAsString();
                Camera c=new Camera(cameraId,cameraFrequency);
                CameraService cameraService = new CameraService(c);
                c.setDetectedObjectsMap(readerCamera.getStampedDetectedObjects(cameraKey),readerCamera.getNumberObject(cameraKey));
                Thread cameraThread = new Thread(cameraService);
                threads.add(cameraThread);
                cameraThread.start();
            }




            // Extracting data from the "LidarWorkers" section
            JsonObject lidarWorkers = configObject.getAsJsonObject("LidarWorkers");
            JsonArray lidarConfigurations = lidarWorkers.getAsJsonArray("LidarConfigurations");
            String lidarDataPath = lidarWorkers.get("lidars_data_path").getAsString();
            path=parentDir.concat(lidarDataPath);
            path = parentDir + File.separator + lidarDataPath.replaceFirst("^\\.", "");
            LiDarDataBase liDarDataBase=LiDarDataBase.getInstance();
            liDarDataBase.loadData(path);

            //System.out.println(liDarDataBase.toString());

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
                LiDarService lidarService = new LiDarService(lidar);
                Thread lidarThread = new Thread(lidarService);
                threads.add(lidarThread);
                lidarThread.start();
            }

            String poseJsonFile = configObject.get("poseJsonFile").getAsString();
            path=parentDir.concat(poseJsonFile);
            path = parentDir + File.separator + poseJsonFile.replaceFirst("^\\.", "");
            ReaderJsonPose readerJsonPose=new ReaderJsonPose(path);

            //System.out.println(readerJsonPose.toString());
            PoseService poseService=new PoseService(new GPSIMU());
            poseService.setPoses(readerJsonPose.getPoses());
            Thread poseThread = new Thread(poseService);
            threads.add(poseThread);
            poseThread.start();

            int tickTime = configObject.get("TickTime").getAsInt();
            int duration = configObject.get("Duration").getAsInt();


            TimeService timeService=new TimeService(tickTime,duration);
            FusionSlamService fusionSlamService=new FusionSlamService(FusionSlam.getInstance());
            Thread fusionSlamThread = new Thread(fusionSlamService);
            threads.add(fusionSlamThread);
            fusionSlamThread.start();

            //start simulation
            Thread timeThread = new Thread(timeService);
            threads.add(timeThread);
            timeThread.start();


            for (Thread t : threads) {
                try {
                    t.join();
                    System.out.println(t.getName() + " has finished.");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


            if (ErrorDetails.getInstance().getError() != null) {
                SimulationOutput output = new SimulationOutput(FusionSlam.getInstance().getLandmarks());
                ErrorDetails.getInstance().setSimulationOutput(output);
                // Write error to error_output.json
                saveErrorFile(ErrorDetails.getInstance(), parentDir+File.separator+"output_file.json");
                
            }
             else {
                // Write simulation output to output_file.json
                SimulationOutput output = new SimulationOutput(FusionSlam.getInstance().getLandmarks());
                saveOutputFile(output, parentDir + File.separator + "output_file.json");
                
                
            }



        } catch (IOException e) {
            System.out.println("Reached exeption");
        }

    }
}
