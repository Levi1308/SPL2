package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.StampedDetectedObjects;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


/**
 * CameraService is responsible for processing data from the camera and
 * sending DetectObjectsEvents to LiDAR workers.
 * 
 * This service interacts with the Camera object to detect objects and updates
 * the system's StatisticalFolder upon sending its observations.
 */
public class CameraService extends MicroService {
    private final Camera camera;
    private int lastEventTick;
    /**
     * Constructor for CameraService.
     *
     * @param camera The Camera object that this service will use to detect objects.
     */
    public CameraService(Camera camera) {
        super("CameraService_" + camera.getId());
        this.camera = camera;
        this.lastEventTick = 0;
    }

    /**
     * Initializes the CameraService.
     * Registers the service to handle TickBroadcasts and sets up callbacks for sending
     * DetectObjectsEvents.
     */
    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, tickBroadcast -> {
            this.camera.setTick(tickBroadcast.getTick());
            sendDetectObjectsEvents();
        });

        subscribeBroadcast(TerminatedBroadcast.class, terminated -> {
            terminate();
        });

        subscribeBroadcast(CrashedBroadcast.class, crash -> {
            if (crash.getSensorType().equals("Camera") && crash.getSensorId().equals(camera.getId())) {
                terminate();
            }
        });

    }

    /**
     * Sends DetectObjectsEvent if the current tick matches the camera's frequency.
     */
    private void sendDetectObjectsEvents() {
        if (this.camera.getTick() - lastEventTick >= camera.getFrequency()) {
            List<DetectedObject> detectedObjects = readDetectedObjectsFromJSON(this.camera.getTick());
            if (!detectedObjects.isEmpty()) {
                sendEvent(new DetectObjectsEvent(detectedObjects,this.camera.getTick()));
                lastEventTick = this.camera.getTick();
                System.out.println("Camera " + camera.getId() + " sent DetectObjectsEvent at tick " + this.camera.getTick());
                camera.addDetectedObjects(detectedObjects, this.camera.getTick());
            }
        }
    }

    /**
     * Reads detected objects from the camera's JSON data file.
     *
     * @param tick The current tick to fetch detected objects for.
     * @return List of detected objects.
     */
    private List<DetectedObject> readDetectedObjectsFromJSON(int tick) {
        List<DetectedObject> detectedObjects = new ArrayList<>();
        try (FileReader reader = new FileReader("example_input_2/camera_data.json")) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<StampedDetectedObjects>>() {}.getType();
            List<StampedDetectedObjects> stampedList = gson.fromJson(reader, type);


            for (StampedDetectedObjects stamped : stampedList) {
                if (stamped.getTime() == tick) {
                    detectedObjects.addAll(stamped.getDetectedObjects());
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading camera data at tick: " + tick);
            e.printStackTrace();
        }
        return detectedObjects;
    }

    private void handleError() {
        sendBroadcast(new CrashedBroadcast("Camera", "camera1", "Camera disconnected"));
        terminate();
    }




}
