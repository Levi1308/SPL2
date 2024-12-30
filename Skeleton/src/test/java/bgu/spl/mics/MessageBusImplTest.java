package bgu.spl.mics;

import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.GPSIMU;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;
import bgu.spl.mics.application.services.CameraService;
import bgu.spl.mics.application.services.LiDarService;
import bgu.spl.mics.application.services.PoseService;
import bgu.spl.mics.application.services.TimeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MessageBusImplTest {
    MessageBusImpl messageBus;
    CameraService cameraService;
    LiDarService liDarService;
    PoseService poseService;
    TimeService timeService;

    @BeforeEach
    void setUp() {
        messageBus = MessageBusImpl.getInstance();
        cameraService = new CameraService(new Camera(1, 2));
        liDarService = new LiDarService(new LiDarWorkerTracker(1, 2));
        poseService = new PoseService(new GPSIMU());
        timeService = new TimeService(1, 30);
    }

    @AfterEach
    void tearDown() {
        messageBus.getQueues().clear(); // Clear queues to ensure isolation between tests
    }

    @Test
    void subscribeEvent() {
        // Test subscribing to an event
        messageBus.subscribeEvent(DetectObjectsEvent.class, cameraService);

        // Verify the subscription
        assertTrue(messageBus.getEventSubscribers().get(DetectObjectsEvent.class).contains(cameraService));
    }

    @Test
    void subscribeBroadcast() {
        // Test subscribing to a broadcast
        messageBus.subscribeBroadcast(CrashedBroadcast.class, cameraService);

        // Verify the subscription
        assertTrue(messageBus.getBroadcastSubscribers().get(CrashedBroadcast.class).contains(cameraService));
    }

    @Test
    void complete() {
        // Test completing an event
        messageBus.subscribeEvent(DetectObjectsEvent.class, cameraService);
        DetectObjectsEvent event = new DetectObjectsEvent(new ArrayList<>(), 2);
        Future<List<DetectedObject>> future = messageBus.sendEvent(event);

        // Complete the event
        messageBus.complete(event, new ArrayList<>());

        // Assert that the future is resolved
        assertTrue(future.isDone());
        assertNotNull(future.get());
    }

    @Test
    void sendBroadcast() {
        messageBus.subscribeBroadcast(CrashedBroadcast.class, cameraService);
        // Test sending a broadcast
        CrashedBroadcast broadcast = new CrashedBroadcast(
                2,
                "Error occurred",
                "Camera1"
        );

        messageBus.sendBroadcast(broadcast);

        // Verify the broadcast is received by all subscribers
        for (MicroService service : messageBus.getBroadcastSubscribers().get(CrashedBroadcast.class)) {
            assertTrue(service instanceof CameraService); // Check that the broadcast was received by cameraService
        }
    }

    @Test
    void sendEvent() {
        // Test sending an event
        messageBus.subscribeEvent(DetectObjectsEvent.class, cameraService);
        DetectObjectsEvent event = new DetectObjectsEvent(new ArrayList<>(), 2);
        Future<List<DetectedObject>> future = messageBus.sendEvent(event);

        assertNotNull(future);
    }

    @Test
    void register() {
        // Test registering a microservice
        messageBus.register(cameraService);
        assertTrue(messageBus.getQueues().containsKey(cameraService));
    }

    @Test
    void unregister() {
        // Test unregistering a microservice
        messageBus.unregister(cameraService);
        assertFalse(messageBus.getQueues().containsKey(cameraService));
    }

    @Test
    void awaitMessage() throws InterruptedException {
        // Register cameraService before subscribing or waiting for messages
        messageBus.register(cameraService);
        messageBus.subscribeBroadcast(TickBroadcast.class, cameraService);

        TickBroadcast tickBroadcast = new TickBroadcast(2);
        messageBus.sendBroadcast(tickBroadcast);

        // Simulate awaiting the message
        Message message = messageBus.awaitMessage(cameraService);

        // Assert that the correct message was received
        assertEquals(TickBroadcast.class, message.getClass());
    }


}
