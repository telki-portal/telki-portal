package bme.tmit.telki;

import bme.tmit.telki.distance_matrix.DistanceMatrixClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TelkiPortalApplication {

	public static DistanceMatrixClient distanceMatrixClient = null;

	public static void main(String[] args) {
		SpringApplication.run(TelkiPortalApplication.class, args);

		distanceMatrixClient = new DistanceMatrixClient(); //starts scheduled tasks
		//distanceMatrixClient.sendApiRequest();
        /*
        distanceMatrixClient.Telki_Budapest_22_00();
        distanceMatrixClient.Telki_Budapest_00_04();
        distanceMatrixClient.Telki_Budapest_04_06();
        distanceMatrixClient.Telki_Budapest_06_12();
        distanceMatrixClient.Telki_Budapest_12_22();

        distanceMatrixClient.Budapest_Telki_00_06();
        distanceMatrixClient.Budapest_Telki_06_14();
        distanceMatrixClient.Budapest_Telki_14_20();
        distanceMatrixClient.Budapest_Telki_20_24();
        */
	}

}
