package bgu.spl.mics.application.services;

import bgu.spl.mics.Event;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.LandMark;
import bgu.spl.mics.application.objects.Pose;
import bgu.spl.mics.application.objects.TrackedObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * FusionSlamService integrates data from multiple sensors to build and update
 * the robot's global map.
 * <p>
 * This service receives TrackedObjectsEvents from LiDAR workers and PoseEvents from the PoseService,
 * transforming and updating the map with new landmarks.
 */
public class FusionSlamService extends MicroService {
    private final FusionSlam fusionSlam;
    private final ConcurrentLinkedQueue<TrackedObjectsEvent> postponedEvents;

    /**
     * Constructor for FusionSlamService.
     *
     * @param fusionSlam The FusionSLAM object responsible for managing the global map.
     */
    public FusionSlamService(FusionSlam fusionSlam) {
        super("FusionSlamService");
        this.fusionSlam = fusionSlam;
        this.postponedEvents = new ConcurrentLinkedQueue<>();
    }

    /**
     * Initializes the FusionSlamService.
     * Registers the service to handle TrackedObjectsEvents, PoseEvents, and TickBroadcasts,
     * and sets up callbacks for updating the global map.
     */
    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, tickBroadcast -> {

            if (MessageBusImpl.getInstance().getNumberofSensors() - 2 != MessageBusImpl.getInstance().getNumberofTerminated())
                fusionSlam.setTick(tickBroadcast.getTick());
            else
                sendBroadcast(new TerminatedBroadcast("FusionSlamService"));
        });

        // Handle TrackedObjectsEvent
        subscribeEvent(TrackedObjectsEvent.class, event -> {
            List<TrackedObject> trackedObjects = event.getTrackedObjects();
            if(onTrackedObjectEvent(event.getTrackedObjects()))
                postponedEvents.add(event);
        });

        // Handle PoseEvent
        subscribeEvent(PoseEvent.class, event -> {
            onPoseEvent(event);
        });

        subscribeBroadcast(TerminatedBroadcast.class, terminated -> {
            System.out.println("FusionSlamService received termination signal. Shutting down.");
            terminate();
        });

        subscribeBroadcast(CrashedBroadcast.class, crash -> {
            System.out.println("FusionSlamService received crash broadcast. Shutting down.");
            terminate();
        });
    }

    public FusionSlam getFusionSlam() {
        return fusionSlam;
    }

    public boolean onTrackedObjectEvent(List<TrackedObject> trackedObjects) {
        if (trackedObjects == null || trackedObjects.isEmpty())
            System.out.println("No tracked objects received at tick " + fusionSlam.getTick());
        else {
            int trackedTime = trackedObjects.get(0).getTime();
            int poseTime = fusionSlam.getCurrentPose().getTime();

            if (poseTime >= trackedTime) {
                System.out.println("FusionSlamService processing tracked objects at tick " + fusionSlam.getTick());
                fusionSlam.doMapping(trackedObjects);
            } else {
                System.out.println("Tracking postponed until pose update at tick " + trackedTime);
                return true;
            }
        }
        return false;
    }

        public void onPoseEvent(PoseEvent event){
            Pose updatedPose = fusionSlam.addPose(event.getPose());
            fusionSlam.setCurrentPose(updatedPose);
            System.out.println("Pose updated to: " + updatedPose.getX() + ", " + updatedPose.getY());

            // Process postponed events if they match the updated pose time
            postponedEvents.removeIf(e -> {
                int trackedTime = e.getTrackedObjects().get(0).getTime();
                if (updatedPose.getTime() >= trackedTime) {
                    System.out.println("Mapped postponed tracked objects at tick " + trackedTime);
                    fusionSlam.doMapping(e.getTrackedObjects());
                    return true; // Remove from list
                }
                return false;
            });
            complete(event, updatedPose);
        }

}
