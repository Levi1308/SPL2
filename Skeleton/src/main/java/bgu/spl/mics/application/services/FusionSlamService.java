package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.LandMark;
import bgu.spl.mics.application.objects.Pose;
import bgu.spl.mics.application.objects.TrackedObject;

import java.util.List;
import java.util.Map;

/**
 * FusionSlamService integrates data from multiple sensors to build and update
 * the robot's global map.
 * 
 * This service receives TrackedObjectsEvents from LiDAR workers and PoseEvents from the PoseService,
 * transforming and updating the map with new landmarks.
 */
public class FusionSlamService extends MicroService {
    private final FusionSlam fusionSlam;

    /**
     * Constructor for FusionSlamService.
     *
     * @param fusionSlam The FusionSLAM object responsible for managing the global map.
     */
    public FusionSlamService(FusionSlam fusionSlam) {
        super("FusionSlamService");
        this.fusionSlam = fusionSlam;
    }

    /**
     * Initializes the FusionSlamService.
     * Registers the service to handle TrackedObjectsEvents, PoseEvents, and TickBroadcasts,
     * and sets up callbacks for updating the global map.
     */
    @Override
    protected void initialize() {

        subscribeBroadcast(TickBroadcast.class, tickBroadcast -> {
            fusionSlam.setTick(tickBroadcast.getTick());
        });


        subscribeEvent(TrackedObjectsEvent.class, event -> {
            fusionSlam.doMapping(event.getTrackedObjects());
        });

        subscribeEvent(PoseEvent.class, event -> {
            Pose updatedPose = fusionSlam.updatePose(event.getPose());
            complete(event, updatedPose);
        });


        subscribeBroadcast(TerminatedBroadcast.class, terminated -> {
            terminate();
        });


        subscribeBroadcast(CrashedBroadcast.class, crash -> {
            terminate();
        });

    }

}
