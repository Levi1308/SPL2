package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * LiDarDataBase is a singleton class responsible for managing LiDAR data.
 * It provides access to cloud point data and other relevant information for tracked objects.
 */
public class LiDarDataBase {
     private List<StampedCloudPoints> cloudPoints;
     private static LiDarDataBase Instance=null;
    /*
     * Returns the singleton instance of LiDarDataBase.
     *
     * @param filePath The path to the LiDAR data file.
     * @return The singleton instance of LiDarDataBase.
     */
    private LiDarDataBase(){
        cloudPoints=new ArrayList<>();
    }
    public static LiDarDataBase getInstance(String filePath) {
         if(Instance==null){
             Instance=new LiDarDataBase();
             return Instance;
         }
         else
             return Instance;
    }
    public List<StampedCloudPoints> getCloudPoints() {
        return cloudPoints;
    }
    public void AddStampedCloudPoints(StampedCloudPoints newPoint){
        cloudPoints.add(newPoint);
    }
}
