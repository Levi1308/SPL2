package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an object tracked by the LiDAR.
 * This object includes information about the tracked object's ID, description, 
 * time of tracking, and coordinates in the environment.
 */
public class TrackedObject {
   private String id;
   private int time;
   private String description;
   private List<CloudPoint> coordinates;

   public TrackedObject(String id,int time,String description,List<CloudPoint> coordinate){
       this.id=id;
       this.time=time;
       this.description=description;
       this.coordinates=new ArrayList<>(coordinate);
   }

    public String getId() {
        return id;
    }

    public int getTime() {
        return time;
    }

    public String getDescription() {
        return description;
    }

    public List<CloudPoint> getCoordinate() {
        return coordinates;
    }
    public void updateCoordinates(List<CloudPoint> newCoordinates){
       this.coordinates.clear();
       this.coordinates=newCoordinates;
    }
    @Override
    public String toString() {
        return "TrackedObject{" +
                "id='" + id + '\'' +
                ", time=" + time +
                ", description='" + description + '\'' +
                ", coordinates=" + coordinates.toString() +
                '}';
    }

}