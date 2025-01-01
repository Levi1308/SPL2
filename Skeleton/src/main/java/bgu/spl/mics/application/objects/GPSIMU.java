package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the robot's GPS and IMU system.
 * Provides information about the robot's position and movement.
 */

public class GPSIMU {
    private int currentTick;
    private STATUS status;
    private List<Pose> poses;
    private int sizePoses;
    public GPSIMU() {
        currentTick = 0;
        poses = Collections.synchronizedList(new ArrayList<>());
        status = STATUS.UP;
        sizePoses=0;
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
        this.status = status;
    }

    public void addPose(Pose pose) {
        poses.add(pose);
    }

    public void setPoses(List<Pose> poses) {
        this.poses.addAll(poses);
        sizePoses=poses.size();
    }

    public Pose onTick(int currentTick) {
        setCurrentTick(currentTick);
        Pose temp;
        if (currentTick < poses.size()) {
            temp = poses.get(currentTick);
            sizePoses--;
        } else {
            // Use the last pose repeatedly
            temp = poses.get(poses.size() - 1);
            System.out.println("Repeating last pose at tick " + currentTick);
        }
        return temp;
    }
    public boolean detectAll(){
        return sizePoses==0;
    }
}
