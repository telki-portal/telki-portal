package bme.tmit.telki;

import bme.tmit.telki.data.InfluxDBConnection;
import bme.tmit.telki.distance_matrix.DistanceMatrixClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import static bme.tmit.telki.data.InfluxDBConnection.getWeekHourly;

@SpringBootApplication
@EnableScheduling
public class TelkiPortalApplication {

	public static final Logger LOG = LoggerFactory.getLogger(InfluxDBConnection.class);
	public static DistanceMatrixClient distanceMatrixClient = null;

	public static void main(String[] args) {
		SpringApplication.run(TelkiPortalApplication.class, args);
		distanceMatrixClient = new DistanceMatrixClient(); //starts scheduled tasks
		//getWeekHourly(DistanceMatrixClient.place.telki_center, DistanceMatrixClient.place.szell_kalman);
	}

}
