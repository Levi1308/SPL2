package bgu.spl.mics.application.services;

import bgu.spl.mics.Event;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.PoseEvent;
import bgu.spl.mics.application.objects.CloudPoint;
import bgu.spl.mics.application.objects.GPSIMU;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.Pose;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * PoseService is responsible for maintaining the robot's current pose (position and orientation)
 * and broadcasting PoseEvents at every tick.
 */
public class PoseService extends MicroService {
    private GPSIMU gpsimu;


    public PoseService(GPSIMU gpsimu) {
        super("PoseService");
        this.gpsimu = gpsimu;

    }

    public void setPoses(List<Pose> poses) {
        this.gpsimu.setPoses(poses);
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, (TickBroadcast broadcast) -> {
            int currentTick = broadcast.getTick();
            System.out.println(getName() + " received tick " + currentTick);
            this.gpsimu.setCurrentTick(broadcast.getTick());
            onTick(currentTick);
        });

        subscribeBroadcast(TerminatedBroadcast.class, (TerminatedBroadcast broadcast) -> {
            System.out.println("PoseService received TerminatedBroadcast. Terminating.");
            terminate();
        });

        subscribeBroadcast(CrashedBroadcast.class, (CrashedBroadcast broadcast) -> {
            System.out.println("PoseService received CrashedBroadcast. Terminating.");
            terminate();
        });

        System.out.println(getName() + " initialized and waiting for ticks.");
    }


    public void onTick(int currentTick) {
        if (!gpsimu.detectAll()) {
            Pose pose = gpsimu.onTick(currentTick);
            PoseEvent poseEvent = new PoseEvent(pose);
            sendEvent(poseEvent);
            System.out.println("Pose sent at tick " + currentTick);
        } else {
            System.out.println("All Pose detected. terminating at tick" + currentTick);
            terminate();
        }
    }


}
