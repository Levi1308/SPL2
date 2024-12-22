package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.GPSIMU;
import bgu.spl.mics.events.TerminatedBroadcast;
import bgu.spl.mics.events.TickBroadcast;

/**
 * PoseService is responsible for maintaining the robot's current pose (position and orientation)
 * and broadcasting PoseEvents at every tick.
 */
public class PoseService extends MicroService {
    private final GPSIMU gpsimu;
    /**
     * Constructor for PoseService.
     *
     * @param gpsimu The GPSIMU object that provides the robot's pose data.
     */
    public PoseService(GPSIMU gpsimu) {
        super("PoseService");
        this.gpsimu = gpsimu;
    }

    /**
     * Initializes the PoseService.
     * Subscribes to TickBroadcast and sends PoseEvents at every tick based on the current pose.
     */
    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, tick -> {
            //Pose currentPose = gpsimu.getCurrentPose();
            //sendEvent(new PoseEvent(currentPose));
            //gpsimu.updateStatistics();
        });

        subscribeBroadcast(TerminatedBroadcast.class, terminated -> {
            System.out.println(getName() + " received termination signal.");
            terminate();
        });
    }
    }
