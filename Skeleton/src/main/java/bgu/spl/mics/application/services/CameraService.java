package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.DetectedObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


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

            if (checkForSensorDisconnection()) {
                sendBroadcast(new CrashedBroadcast(tickBroadcast.getTick(),"camera disconnected","Camera"+camera.getId()));
                LastFrame.getInstance().setDetectedObjects(camera.getDetectedObjectstill(tickBroadcast.getTick()));
                terminate();
            }
        });

        subscribeBroadcast(TerminatedBroadcast.class, terminated -> {
            System.out.println(getName() + " received TerminatedBroadcast. Terminating.");
            terminate();
        });

        subscribeBroadcast(CrashedBroadcast.class, crash -> {
            System.out.println(getName() + " received crash broadcast. Shutting down.");
            ErrorDetails.getInstance().setError(
                    crash.getError(),
                    crash.getFaultySensor(),
                    FusionSlam.getInstance().getPosesTill(crash.getTime())
            );
            if(LastFrame.getInstance().getDetectedObjects().isEmpty())
                LastFrame.getInstance().setDetectedObjects(camera.getDetectedObjectstill(crash.getTime()));
            terminate();

        });

        System.out.println(getName() + " initialized and ready to process TickBroadcasts.");
    }


    private boolean checkForSensorDisconnection() {
       return this.camera.getStatus() == STATUS.ERROR;
    }

    /**
     * Sends DetectObjectsEvent if the current tick matches the camera's frequency.
     */
    private void sendDetectObjectsEvents() {
        int tickDifference = camera.getTick() - lastEventTick;
        if (tickDifference >= camera.getFrequency()) {
            List<DetectedObject> detectedObjects = camera.getDetectedObjects(camera.getTick());
            DetectedObject error=AnErrorOccured(detectedObjects);
            if(error==null)
            {
                if (!detectedObjects.isEmpty()) {
                    System.out.println("Camera " + camera.getId() + " detected " + detectedObjects.size() + " objects at tick " + camera.getTick());
                    sendEvent(new DetectObjectsEvent(detectedObjects, this.camera.getTick()));
                    lastEventTick = camera.getTick();
                    camera.addDetectedObjects(detectedObjects, camera.getTick());

                    System.out.println("DetectObjectsEvent sent by Camera " + camera.getId() + " at tick " + camera.getTick());
                } else {
                    System.out.println("Camera " + camera.getId() + " detected no objects at tick " + camera.getTick());
                }
            }
            else
            {
                sendBroadcast(new CrashedBroadcast(camera.getTick(),"camera disconnected","Camera"+camera.getId()));
                terminate();
                return;
            }

        } else {
            System.out.println("Camera " + camera.getId() + " skipping DetectObjectsEvent. Tick difference: " + tickDifference);
        }
    }
    public DetectedObject AnErrorOccured(List<DetectedObject> objectList)
    {
        for(DetectedObject d: objectList)
        {
            if(d.getId().equals("ERROR"))
                return d;
        }
        return null;
    }














}