package bgu.spl.mics.application.objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Map;

public class SimulationOutput {
    private StatisticalFolder statistics;
    private Map<String, LandMark> landmarks;

    public SimulationOutput( Map<String, LandMark> landmarks) {
        this.statistics = StatisticalFolder.getInstance();
        this.landmarks = landmarks;
    }

    // Serialize SimulationOutput to JSON
    public String toJson() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }

    // Getters for landmarks and statistics
    public Map<String, LandMark> getLandmarks() {
        return landmarks;
    }

    public StatisticalFolder getStatistics() {
        return statistics;
    }
}
