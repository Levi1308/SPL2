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
    private List<Pose> poses;

    public GPSIMU(){
        currentTick=0;
        poses=new ArrayList<>();
        status=STATUS.UP;
    }

    public int getCurrentTick() {
        return currentTick;
    }

    public List<Pose> getPoseList() {
        return poses;
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
    public void addPose(Pose pose){
        poses.add(pose);
    }

    public void setPoses(List<Pose> poses) {
        this.poses = poses;
    }
}
