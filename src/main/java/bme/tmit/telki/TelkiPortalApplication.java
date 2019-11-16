package bme.tmit.telki;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TelkiPortalApplication {

	public static void main(String[] args) {
		SpringApplication.run(TelkiPortalApplication.class, args);
	}

}
