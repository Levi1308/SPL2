package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.StatisticalFolder;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


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
        AtomicInteger ticks = new AtomicInteger(0);
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(() -> {
            if (ticks.get() < Duration) {
                sendBroadcast(new TickBroadcast(ticks.getAndIncrement()));
                System.out.println("Tick " + ticks.get() + " broadcasted.");
                statisticalFolder.incrementRuntime();
            } else {
                sendBroadcast(new TerminatedBroadcast(getName()));
                System.out.println("Simulation ended at tick " + ticks.get());
                scheduler.shutdown();
            }
            }, 0, TickTime, TimeUnit.SECONDS);


        subscribeBroadcast(CrashedBroadcast.class, crash -> {
            System.out.println("Simulation stopping due to sensor failure: " + crash.getError());
            scheduler.shutdown();
            terminate();
        });

        subscribeBroadcast(TerminatedBroadcast.class, terminated -> {
            System.out.println(getName() + " received TerminatedBroadcast. Terminating.");
            scheduler.shutdown();
            terminate();
        });
    }
}





