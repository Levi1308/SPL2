package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.LiDarTracker;
import bgu.spl.mics.application.objects.TrackedObject;
import bgu.spl.mics.example.messages.DetectObjectsEvent;
import bgu.spl.mics.example.messages.TerminatedBroadcast;
import bgu.spl.mics.example.messages.TickBroadcast;
import bgu.spl.mics.example.messages.TrackedObjectsEvent;

import java.util.List;

/**
 * LiDarService is responsible for processing data from the LiDAR sensor and
 * sending TrackedObjectsEvents to the FusionSLAM service.
 * 
 * This service interacts with the LiDarTracker object to retrieve and process
 * cloud point data and updates the system's StatisticalFolder upon sending its
 * observations.
 */
public class LiDarService extends MicroService {
    private final LiDarTracker liDarTracker;
    /**
     * Constructor for LiDarService.
     *
     * @param liDarTracker The LiDAR tracker object that this service will use to process data.
     */
    public LiDarService(LiDarTracker liDarTracker) {
        super("LiDarService_" + liDarTracker.getId());
        this.liDarTracker = liDarTracker;
    }

    /**
     * Initializes the LiDarService.
     * Registers the service to handle DetectObjectsEvents and TickBroadcasts,
     * and sets up the necessary callbacks for processing data.
     */
    @Override
    protected void initialize() {

        subscribeBroadcast(TickBroadcast.class, tick -> {
            if (liDarTracker.isOperational()) {
                List<TrackedObject> trackedObjects = liDarTracker.trackObjects(tick.getTick());
                if (!trackedObjects.isEmpty()) {
                    sendEvent(new TrackedObjectsEvent(trackedObjects));
                    liDarTracker.updateStatistics(trackedObjects.size());
                }
            }
        });


        subscribeEvent(DetectObjectsEvent.class, event -> {
            List<TrackedObject> trackedObjects = liDarTracker.processDetection(event.getDetectedObjects());
            if (!trackedObjects.isEmpty()) {
                complete(event, trackedObjects);
                sendEvent(new TrackedObjectsEvent(trackedObjects));
                liDarTracker.updateStatistics(trackedObjects.size());
            }
        });


        subscribeBroadcast(TerminatedBroadcast.class, terminated -> {
            System.out.println(getName() + " received termination signal.");
            terminate();
        });
    }
}
}
