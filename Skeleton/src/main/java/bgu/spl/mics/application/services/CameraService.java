package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.events.DetectObjectsEvent;
import bgu.spl.mics.events.TerminatedBroadcast;
import bgu.spl.mics.events.TickBroadcast;

/**
 * CameraService is responsible for processing data from the camera and
 * sending DetectObjectsEvents to LiDAR workers.
 * 
 * This service interacts with the Camera object to detect objects and updates
 * the system's StatisticalFolder upon sending its observations.
 */
public class CameraService extends MicroService {
    private final Camera camera;
    /**
     * Constructor for CameraService.
     *
     * @param camera The Camera object that this service will use to detect objects.
     */
    public CameraService(Camera camera) {
        super("CameraService_" + camera.getId());
        this.camera = camera;
    }

    /**
     * Initializes the CameraService.
     * Registers the service to handle TickBroadcasts and sets up callbacks for sending
     * DetectObjectsEvents.
     */
    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, tick -> {
            if (camera.shouldSendEvent(tick.getTick())) {
                DetectObjectsEvent event = new DetectObjectsEvent(camera.getDetectedObjects(tick.getTick()));
                Future<?> future = sendEvent(event);

                if (future == null) {
                    System.out.println("No Lidar service available for DetectObjectsEvent at tick " + tick.getTick());
                }

                camera.updateStatistics();
            }
        });

        subscribeBroadcast(TerminatedBroadcast.class, terminated -> {
            System.out.println("CameraService " + getName() + " received termination broadcast.");
            terminate();
        });
    }

}
