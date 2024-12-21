package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;
import bgu.spl.mics.application.objects.Camera.*;
/**
 * Represents the robot's GPS and IMU system.
 * Provides information about the robot's position and movement.
 */

public class GPSIMU {
    private int currentTick;
    private STATUS status;
    private List<Pose> poseList;
    public GPSIMU(){
        currentTick=0;
        poseList=new ArrayList<>();
    }

    public int getCurrentTick() {
        return currentTick;
    }

    public List<Pose> getPoseList() {
        return poseList;
    }

    public STATUS getStatus() {
        return status;
    }

    public void setCurrentTick(int currentTick) {
        this.currentTick = currentTick;
    }

    public void setStatus(STATUS status) {
        this.status=status;
    }
}
