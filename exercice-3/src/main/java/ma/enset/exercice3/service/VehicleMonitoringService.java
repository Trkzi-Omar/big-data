package ma.enset.exercice3.service;

import ma.enset.exercice3.model.VehicleData;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VehicleMonitoringService {

    private static final double SPEED_LIMIT = 80.0; // km/h
    private static final double DISTANCE_THRESHOLD = 5.0; // meters

    @Autowired
    void buildPipeline(StreamsBuilder streamsBuilder) {
        // Read from vehicle_data topic
        KStream<String, String> vehicleStream = streamsBuilder.stream(
            "vehicle_data", 
            Consumed.with(Serdes.String(), Serdes.String())
        );

        // Process speed alerts
        vehicleStream
            .filter((key, value) -> {
                try {
                    VehicleData data = VehicleData.fromString(value);
                    return data.getSpeed() > SPEED_LIMIT;
                } catch (Exception e) {
                    return false;
                }
            })
            .mapValues(value -> {
                VehicleData data = VehicleData.fromString(value);
                return String.format("%s|%.1f|%.6f|%.6f|Overspeeding|%s",
                    data.getVehicleId(),
                    data.getSpeed(),
                    data.getLatitude(),
                    data.getLongitude(),
                    data.getTimestamp()
                );
            })
            .to("speed_alerts", Produced.with(Serdes.String(), Serdes.String()));

        // Process obstacle alerts
        vehicleStream
            .filter((key, value) -> {
                try {
                    VehicleData data = VehicleData.fromString(value);
                    return data.getDistanceToNextObstacle() < DISTANCE_THRESHOLD;
                } catch (Exception e) {
                    return false;
                }
            })
            .mapValues(value -> {
                VehicleData data = VehicleData.fromString(value);
                return String.format("%s|%.1f|Obstacle too close!",
                    data.getVehicleId(),
                    data.getDistanceToNextObstacle()
                );
            })
            .to("obstacle_alerts", Produced.with(Serdes.String(), Serdes.String()));
    }
} 