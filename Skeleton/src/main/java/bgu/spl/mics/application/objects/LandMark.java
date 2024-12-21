package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a landmark in the environment map.
 * Landmarks are identified and updated by the FusionSlam service.
 */
public class LandMark {
   private String id;
   private String description;
   private List<CloudPoint> coordinates;
   public LandMark(String id,String description,CloudPoint point)
   {
      this.id=id;
      this.description=description;
      coordinates=new ArrayList<>();
      coordinates.add(point);
   }
   public LandMark(TrackedObject object)
   {
      this.id= object.getId();
      this.description= object.getDescription();
      coordinates=new ArrayList<>(object.getCoordinate());
   }
   public String getId() {
      return id;
   }

   public String getDescription() {
      return description;
   }

   public List<CloudPoint> getCoordinates() {
      return coordinates;
   }
   public void UpdateCoordinates(List<CloudPoint> points){
      double sumx=0,sumy=0,sumz=0;
      for(CloudPoint cp:coordinates){
         sumx+=cp.getX();
         sumy+=cp.getY();
      }
      //continue implement
   }
}
