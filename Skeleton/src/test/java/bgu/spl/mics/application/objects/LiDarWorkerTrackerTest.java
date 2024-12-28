package bgu.spl.mics.application.objects;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LiDarWorkerTrackerTest {
    List<TrackedObject> trackedObjects;
    LiDarWorkerTracker lidar;
    int currentTick=3;
    @BeforeEach
    void setUp() {
        lidar=new LiDarWorkerTracker(1,3);
        trackedObjects = new ArrayList<>();

        trackedObjects.add(new TrackedObject(
                "Wall_1", 2, "East wall section",
                Arrays.asList(new CloudPoint(0.1, 3.7), new CloudPoint(0.2, 3.8))
        ));
        trackedObjects.add(new TrackedObject(
                "Wall_2", 4, "North wall section",
                Arrays.asList(new CloudPoint(-0.5, 5.0), new CloudPoint(-0.4, 5.1))
        ));
        trackedObjects.add(new TrackedObject(
                "Chair_1", 6, "Office chair base",
                Arrays.asList(new CloudPoint(1.9, -1.0), new CloudPoint(2.0, -1.1))
        ));
        trackedObjects.add(new TrackedObject(
                "Table_1", 6, "Dining table top",
                Arrays.asList(new CloudPoint(-2.5, 2.0), new CloudPoint(-2.4, 2.1))
        ));


    }

    @AfterEach
    void tearDown() {
        // Optionally, clear the list after each test
        trackedObjects.clear();
    }

    @Test
    void onTick() {
        // Get the tracked objects processed at currentTick
        List<TrackedObject> trackedAtTick = lidar.onTick(currentTick);

        // Expected objects to be processed at currentTick
        List<TrackedObject> expectedObjects = new ArrayList<>();

        // Objects with time + frequency == currentTick
        for (TrackedObject t : trackedObjects) {
            if (t.getTime() + lidar.getFrequency() == currentTick) {
                expectedObjects.add(t);
            }
        }

        // Assert that the returned list is the same as expected
        assertEquals(expectedObjects.size(), trackedAtTick.size(), "The number of objects returned does not match the expected size.");

        // Verify each object in the result matches the expected ones
        for (TrackedObject expected : expectedObjects) {
            assertTrue(trackedAtTick.contains(expected), "The object " + expected.getId() + " should be in the processed list.");
        }
    }
}