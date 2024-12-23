package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

/**
 * CrashedBroadcast notifies all services about a sensor failure.
 * It includes details about the type of sensor, its ID, and the reason for the failure.
 */
public class CrashedBroadcast implements Broadcast {
    private final String sensorType;
    private final String sensorId;
    private final String errorDescription;

    /**
     * Constructor for CrashedBroadcast.
     *
     * @param sensorType       Type of the sensor (e.g., Camera, LiDAR).
     * @param sensorId         Unique identifier of the sensor.
     * @param errorDescription Description of the error causing the crash.
     */
    public CrashedBroadcast(String sensorType, String sensorId, String errorDescription) {
        this.sensorType = sensorType;
        this.sensorId = sensorId;
        this.errorDescription = errorDescription;
    }

    /**
     * @return The type of the sensor that crashed.
     */
    public String getSensorType() {
        return sensorType;
    }

    /**
     * @return The ID of the sensor that crashed.
     */
    public String getSensorId() {
        return sensorId;
    }

    /**
     * @return A description of the error.
     */
    public String getErrorDescription() {
        return errorDescription;
    }

}

