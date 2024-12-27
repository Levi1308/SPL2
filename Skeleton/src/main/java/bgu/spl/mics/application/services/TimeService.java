package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.GurionRockRunner;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.ErrorDetails;
import bgu.spl.mics.application.objects.SimulationOutput;
import bgu.spl.mics.application.objects.StatisticalFolder;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static bgu.spl.mics.application.GurionRockRunner.saveOutputFile;

/**
 * TimeService acts as the global timer for the system, broadcasting TickBroadcast messages
 * at regular intervals and controlling the simulation's duration.
 */
public class TimeService extends MicroService {
     private int TickTime;
     private int Duration;
    private StatisticalFolder statisticalFolder = StatisticalFolder.getInstance();

    /**
     * Constructor for TimeService.
     *
     * @param TickTime  The duration of each tick in milliseconds.
     * @param Duration  The total number of ticks before the service terminates.
     */
    public TimeService(int TickTime, int Duration) {
        super("TimeService");
        this.TickTime = TickTime;
        this.Duration = Duration;
    }

    /**
     * Initializes the TimeService.
     * Starts broadcasting TickBroadcast messages and terminates after the specified duration.
     */
    @Override
    protected void initialize() {
        int[] ticks = {0};
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            if (ticks[0] < Duration) {
                sendBroadcast(new TickBroadcast(ticks[0]++));
                statisticalFolder.incrementRuntime();
            } else {
                sendBroadcast(new TerminatedBroadcast(getName()));
                scheduler.shutdown();
                terminate();
            }
        }, 0, TickTime, TimeUnit.MILLISECONDS);
        subscribeBroadcast(CrashedBroadcast.class, crash -> {
            System.out.println("Simulation stopping due to sensor failure: " + crash.getError());
            generateErrorOutputFile(crash.getError(), crash.getFaultySensors());
            terminate();
        });

    }
    public  void generateErrorOutputFile(String error, List<String> faultySensors) {

        ErrorDetails errorDetails = new ErrorDetails(error, faultySensors, Map.of(), List.of(), statisticalFolder);

        SimulationOutput output = new SimulationOutput(statisticalFolder, null, errorDetails);

        String outputPath = "example_input/output.json";
        saveOutputFile(output, outputPath);
    }
}



