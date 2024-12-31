package bgu.spl.mics.application.objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Map;

public class SimulationOutput {
    private StatisticalFolder Statistics;
    private Map<String, LandMark> landMarks;

    public SimulationOutput( Map<String, LandMark> landmarks) {
        this.Statistics = StatisticalFolder.getInstance();
        this.landMarks = landmarks;
    }

    // Serialize SimulationOutput to JSON
    public String toJson() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }

    // Getters for landmarks and statistics
    public Map<String, LandMark> getLandmarks() {
        return landMarks;
    }

    public StatisticalFolder getStatistics() {
        return Statistics;
    }
}
