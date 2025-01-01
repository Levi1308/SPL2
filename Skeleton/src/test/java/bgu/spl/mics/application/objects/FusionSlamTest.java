package bgu.spl.mics.application.objects;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FusionSlamTest {
    private FusionSlam fusionSlam;
    private StatisticalFolder statisticalFolder;
    private Pose pose;

    @BeforeEach
    void setUp() {
        fusionSlam = FusionSlam.getInstance();
        statisticalFolder = StatisticalFolder.getInstance();
        pose = new Pose(1, 1, 90, 1);  // Sample pose (x, y, yaw, time)
        fusionSlam.addPose(pose);  // Set the pose for mapping
    }

    @AfterEach
    void tearDown() {
        statisticalFolder.reset();  // Reset statistics after each test
        fusionSlam = null;  // Clear the FusionSlam instance
        pose=null;
    }

    @Test
    void doMapping_NewLandmark() {
        // Prepare tracked objects with new landmarks
        List<TrackedObject> trackedObjects = new ArrayList<>();
        trackedObjects.add(new TrackedObject("Wall_1", 1, "East wall section",
                Arrays.asList(new CloudPoint(0.1, 3.7), new CloudPoint(0.2, 3.8))));

        // Perform mapping
        fusionSlam.doMapping(trackedObjects);

        // Verify that the landmark was added
        assertTrue(fusionSlam.getLandmarks().containsKey("Wall_1"));
        assertEquals(1, statisticalFolder.getNumLandmarks());  // Ensure the count is correct
    }

    @Test
    void doMapping_UpdateLandmark() {
        // Prepare tracked objects with new landmarks
        List<TrackedObject> trackedObjects = new ArrayList<>();
        trackedObjects.add(new TrackedObject("Wall_1", 1, "East wall section",
                Arrays.asList(new CloudPoint(0.1, 3.7), new CloudPoint(0.2, 3.8))));

        // First mapping call (add new landmark)
        fusionSlam.doMapping(trackedObjects);

        // Prepare updated tracked object with the same landmark ID ("Wall_1")
        trackedObjects.clear();
        trackedObjects.add(new TrackedObject("Wall_1", 1, "East wall section",
                Arrays.asList(new CloudPoint(0.3, 3.9), new CloudPoint(0.4, 4.0))));

        // Perform mapping again (update existing landmark)
        fusionSlam.doMapping(trackedObjects);

        // Verify that the landmark was updated
        LandMark updatedLandmark = fusionSlam.getLandmarks().get("Wall_1");
        assertNotNull(updatedLandmark);
        assertEquals(2, updatedLandmark.getCoordinates().size());  // Ensure the coordinates were updated
    }

    @Test
    void doMapping_EmptyCoordinates() {
        // Prepare tracked objects with no coordinates
        List<TrackedObject> trackedObjects = new ArrayList<>();
        trackedObjects.add(new TrackedObject("Wall_1", 1, "East wall section", new ArrayList<>()));

        // Perform mapping
        fusionSlam.doMapping(trackedObjects);

        // Verify that no landmark was added
        assertFalse(fusionSlam.getLandmarks().containsKey("Wall_1"));
        assertEquals(0, statisticalFolder.getNumLandmarks());  // Ensure the count is still 0
    }

    @Test
    void doMapping_NoPoseForTrackedObject() {
        // Prepare tracked objects with a time that doesn't match the current pose
        List<TrackedObject> trackedObjects = new ArrayList<>();
        trackedObjects.add(new TrackedObject("Wall_1", 2, "East wall section",
                Arrays.asList(new CloudPoint(0.1, 3.7), new CloudPoint(0.2, 3.8))));

        // Perform mapping with no matching pose
        fusionSlam.doMapping(trackedObjects);

        // Verify that no landmark was added due to missing pose
        assertFalse(fusionSlam.getLandmarks().containsKey("Wall_1"));
    }

}