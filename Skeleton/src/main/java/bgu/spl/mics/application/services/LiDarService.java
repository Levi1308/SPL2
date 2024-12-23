package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;
import bgu.spl.mics.application.objects.Pose;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.List;


public class LiDarService extends MicroService {
    private final LiDarWorkerTracker liDarTracker;


    public LiDarService(LiDarWorkerTracker liDarWorkerTracker) {
        super("LidarWorkerService" + liDarWorkerTracker.getId());
        this.liDarTracker = liDarWorkerTracker;
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, (TickBroadcast broadcast) -> {
            //int currentTick = broadcast.getTick();
            //handleTick(currentTick);
        });
        subscribeBroadcast(TerminatedBroadcast.class, (TerminatedBroadcast broadcast) -> {
            //int currentTick = broadcast.getTick();
            //handleTick(currentTick);
        });
        subscribeBroadcast(CrashedBroadcast.class, (CrashedBroadcast broadcast) -> {
            //int currentTick = broadcast.getTick();
            //handleTick(currentTick);
        });
        subscribeEvent(DetectObjectsEvent.class, (DetectObjectsEvent broadcast) -> {
            //int currentTick = broadcast.getTick();
            //handleTick(currentTick);
        });
    }
    public void loadData() {
        try {
            Gson gson = new Gson();
            Type LidarListType = new TypeToken<List<LiDarWorkerTracker>>() {}.getType();
            poses = gson.fromJson(new FileReader("lidar_data.json"), LidarListType);
        }
        catch (Exception ex){

        }
    }
}
