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

   public LandMark(String id,String description,List<CloudPoint>location)
   {
      this.id=id;
      this.description=description;
      this.coordinates= location;
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

   public void UpdateCoordinates(List<CloudPoint> points) {
      int minSize = Math.min(coordinates.size(), points.size());

      for (int i = 0; i < minSize; i++) {
         CloudPoint existing = coordinates.get(i);
         CloudPoint incoming = points.get(i);
         existing.setX((existing.getX() + incoming.getX()) / 2);
         existing.setY((existing.getY() + incoming.getY()) / 2);
      }

      if (points.size() > coordinates.size()) {
         for (int i = minSize; i < points.size(); i++) {
            coordinates.add(points.get(i));
         }
      }
   }



}
