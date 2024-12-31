package bgu.spl.mics.application.services;

import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.DetectedObject;

import java.util.List;


/**
 * CameraService is responsible for processing data from the camera and
 * sending DetectObjectsEvents to LiDAR workers.
 * This service interacts with the Camera object to detect objects and updates
 * the system's StatisticalFolder upon sending its observations.
 */
public class CameraService extends MicroService {
    private final Camera camera;
    private int lastEventTick;
    private ErrorDetails errorDetails;

    /**
     * Constructor for CameraService.
     *
     * @param camera The Camera object that this service will use to detect objects.
     */
    public CameraService(Camera camera) {
        super("CameraService_" + camera.getId());
        this.camera = camera;
        this.lastEventTick = 0;
        errorDetails = ErrorDetails.getInstance();
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
            onTick();
            if (checkForSensorDisconnection()) {
                errorDetails.setError("camera disconnected", "Camera" + camera.getId(), FusionSlam.getInstance().getPosesTill(tickBroadcast.getTick()));
                errorDetails.addLastCameraFrame("Camera" + camera.getId(), camera.getStampedObjects(tickBroadcast.getTick()));
                sendBroadcast(new CrashedBroadcast(tickBroadcast.getTick(), "camera disconnected", "Camera" + camera.getId()));
                terminate();
            }
        });

        subscribeBroadcast(TerminatedBroadcast.class, terminated -> {
            System.out.println(getName() + " received TerminatedBroadcast. Terminating.");
            terminate();
        });

        subscribeBroadcast(CrashedBroadcast.class, crash -> {
            System.out.println(getName() + " received crash broadcast. Shutting down.");
            //errorDetails.addLastCameraFrame("Camera"+camera.getId(),camera.getDetectedObjects(crash.getTime()));
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

    private void onTick() {
        if (!camera.detectAll()) {
            List<DetectedObject> detectedObjects = camera.onTick();
            if (!detectedObjects.isEmpty()) {
                if (!detectedObjects.get(0).getId().equals("ERROR")) {
                    sendEvent(new DetectObjectsEvent(detectedObjects, this.camera.getTick()));
                    lastEventTick = camera.getTick();
                    System.out.println("Camera sent DetectObjectsEvent" + camera.getId() + " at tick " + camera.getTick());
                } else {
                    errorDetails.setError("camera disconnected", "Camera" + camera.getId(), FusionSlam.getInstance().getPosesTill(camera.getTick()));
                    errorDetails.addLastCameraFrame("Camera" + camera.getId(), camera.getStampedObjects(camera.getTick()));
                    sendBroadcast(new CrashedBroadcast(camera.getTick(), "Camera disconnected", "Camera" + camera.getId()));

                    terminate();
                }
            } else {
                System.out.println("Camera " + camera.getId() + " detected no objects at tick " + camera.getTick());
            }
        }
    else
    {
        System.out.println("Camera " + camera.getId() + " detected all the objects at tick" + camera.getTick());
        terminate();
    }
}















}