package ma.enset.exercice3.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleData {
    private String vehicleId;
    private double speed;
    private double latitude;
    private double longitude;
    private double distanceToNextObstacle;
    private Instant timestamp;

    // Parse from string format: <VehicleID>|<Speed>|<Latitude>|<Longitude>|<DistanceToNextObstacle>|<Timestamp>
    public static VehicleData fromString(String value) {
        String[] parts = value.split("\\|");
        if (parts.length != 6) {
            throw new IllegalArgumentException("Invalid vehicle data format");
        }

        VehicleData data = new VehicleData();
        data.setVehicleId(parts[0]);
        data.setSpeed(Double.parseDouble(parts[1]));
        data.setLatitude(Double.parseDouble(parts[2]));
        data.setLongitude(Double.parseDouble(parts[3]));
        data.setDistanceToNextObstacle(Double.parseDouble(parts[4]));
        data.setTimestamp(Instant.parse(parts[5]));
        return data;
    }
} 