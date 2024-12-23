package bgu.spl.mics.application.services;

import bgu.spl.mics.Event;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.PoseEvent;
import bgu.spl.mics.application.objects.GPSIMU;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.Pose;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * PoseService is responsible for maintaining the robot's current pose (position and orientation)
 * and broadcasting PoseEvents at every tick.
 */
public class PoseService extends MicroService {
 private GPSIMU gpsimu;
 List<Pose> poses;
    public PoseService(GPSIMU gpsimu) {
        super("PoseService");
        this.gpsimu=gpsimu;
        poses=new ArrayList<>();
        loadData();
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, (TickBroadcast broadcast) -> {

            int currentTick = broadcast.getTick();
            this.gpsimu.setCurrentTick(broadcast.getTick());
            onTick(currentTick);

        });
    }

    public void onTick(int currentTick){
        gpsimu.setCurrentTick(currentTick);
        try{
            Pose temp=poses.get(currentTick);
            gpsimu.addPose(temp);
            PoseEvent poseEvent=new PoseEvent(temp);
            sendEvent(poseEvent);
        }
        catch(Exception ex)
        {
            //finish with all th poses;
        }
    }
    public void loadData() {
        try {
            Gson gson = new Gson();
            Type PoseListType = new TypeToken<List<Pose>>() {}.getType();
            poses = gson.fromJson(new FileReader("pose_data.json"), PoseListType);
        }
        catch (Exception ex){

        }
    }
}
