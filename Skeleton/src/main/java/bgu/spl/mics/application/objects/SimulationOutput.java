package bgu.spl.mics.application.objects;


import java.util.List;
import java.util.Map;

public class SimulationOutput {
    private StatisticalFolder statistics = StatisticalFolder.getInstance();;
    private Map<String, LandMark> landmarks;
    private ErrorDetails error;

    public SimulationOutput(StatisticalFolder statistics, Map<String, LandMark> landmarks, ErrorDetails error) {
        this.statistics = statistics;
        this.landmarks = landmarks;
        this.error = error;
    }

}
