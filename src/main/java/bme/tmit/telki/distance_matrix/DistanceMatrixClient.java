package bme.tmit.telki.distance_matrix;

import bme.tmit.telki.data.InfluxDBConnection;
import bme.tmit.telki.data.TrafficInfoEntry;
import com.google.maps.DistanceMatrixApi;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.LatLng;
import com.google.maps.model.TrafficModel;
import com.google.maps.model.TravelMode;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

import static bme.tmit.telki.TelkiPortalApplication.LOG;

/**
 * Komment:
 * <p>
 * Kérés URLként:
 * https://maps.googleapis.com/maps/api/distancematrix/json?origins=Seattle&destinations=San+Francisco&key=AIzaSyBcJixlXHjzXIc0McRXeGtaEDp_jQh9Ef0
 * <p>
 * Válasz JSON formában PostMan-el megnézve:
 * {
 *    "destination_addresses": [
 *       "San Francisco, CA, USA"
 *    ],
 *    "origin_addresses": [
 *       "Seattle, WA, USA"
 *    ],
 *    "rows": [
 *       {
 *          "elements": [
 *             {
 *                "distance": {
 *                   "text": "1,300 km",
 *                   "value": 1299735
 *                },
 *                "duration": {
 *                   "text": "12 hours 38 mins",
 *                   "value": 45466
 *                },
 *                "status": "OK"
 *             }
 *          ]
 *       }
 *    ],
 *    "status": "OK"
 * }
 */

@Component
public class DistanceMatrixClient {
    public enum place {
        telki_center, szell_kalman, petofi_hid_budai
    }

    public static place parsePlace(String str) {
        switch (str) {
            case "telki_center":
                return place.telki_center;
            case "szell_kalman":
                return place.szell_kalman;
            case "petofi_hid_budai":
                return place.petofi_hid_budai;
        }
        return null;
    }

    private final LatLng telki_center = new LatLng(47.5468889, 18.8283889);     // https://www.google.com/maps/@47.5475926,18.8278371,16.5z/data=!4m5!3m4!1s0x0:0x0!8m2!3d47.5468889!4d18.8283889
    private final LatLng szell_kalman = new LatLng(47.5077219, 19.0227367);     // https://www.google.com/maps/@47.507037,19.0216853,17z/data=!4m5!3m4!1s0x4741dea0e74e6c29:0xcbcc12dee29046e7!8m2!3d47.5077219!4d19.0227367
    private final LatLng petofi_hid_budai = new LatLng(47.4769369, 19.0594951); // https://www.google.com/maps/@47.4769405,19.0573064,17z/data=!3m1!4b1!4m5!3m4!1s0x4741ddab5b203951:0xf3231c10b7b810b1!8m2!3d47.4769369!4d19.0594951

    public void sendApiRequest() {
        LOG.info("Sending google api request");

        DistanceMatrix distanceMatrix = DistanceMatrixApi.newRequest(GeoApiContextProvider.getContext())
                .origins(telki_center)
                .destinations(szell_kalman, petofi_hid_budai)
                .mode(TravelMode.DRIVING)
                .departureTime(Instant.now())
                .trafficModel(TrafficModel.BEST_GUESS)
                .awaitIgnoreError();

        /*
        System.out.println(distanceMatrix.toString());
        for (DistanceMatrixRow row :distanceMatrix.rows) {
            System.out.println(row.toString());
            for (DistanceMatrixElement element : row.elements) {
                System.out.println("InTraffic: " + element.durationInTraffic + ", Időtartam: " + element.duration +", Távolság: " + element.distance);
            }
        }

        List<TrafficInfoEntry> responses = new ArrayList<>();
        responses.add(new TrafficInfoEntry(null, "telki_center", "szell_kalman", distanceMatrix.rows[0].elements[0].durationInTraffic.inSeconds/60));
        responses.add(new TrafficInfoEntry(null, "telki_center", "petofi_hid_budai", distanceMatrix.rows[0].elements[1].durationInTraffic.inSeconds/60));

        for (TrafficInfoEntry entry : responses) {
            InfluxDBConnection.saveEntry(entry);
        }

        InfluxDBConnection.saveEntry(
                new TrafficInfoEntry(null, "telki_center", "szell_kalman", distanceMatrix.rows[0].elements[0].durationInTraffic.inSeconds/60));
        InfluxDBConnection.saveEntry(
                new TrafficInfoEntry(null, "telki_center", "petofi_hid_budai", distanceMatrix.rows[0].elements[1].durationInTraffic.inSeconds/60));
         */
    }

    public void requestTelkiBudapest() {
        LOG.info("Sending google api request");
        DistanceMatrix distanceMatrix = DistanceMatrixApi.newRequest(GeoApiContextProvider.getContext())
                .origins(telki_center)
                .destinations(szell_kalman, petofi_hid_budai)
                .mode(TravelMode.DRIVING)
                .departureTime(Instant.now())
                .trafficModel(TrafficModel.BEST_GUESS)
                .awaitIgnoreError();
        InfluxDBConnection.saveEntry(
                new TrafficInfoEntry(null, "telki_center", "szell_kalman", distanceMatrix.rows[0].elements[0].durationInTraffic.inSeconds / 60,
                        String.valueOf(LocalDateTime.now().getDayOfWeek().getValue()),
                        String.valueOf(LocalDateTime.now().getHour())));
        InfluxDBConnection.saveEntry(
                new TrafficInfoEntry(null, "telki_center", "petofi_hid_budai", distanceMatrix.rows[0].elements[1].durationInTraffic.inSeconds / 60,
                        String.valueOf(LocalDateTime.now().getDayOfWeek().getValue()),
                        String.valueOf(LocalDateTime.now().getHour())));
    }

    public void requestBudapestTelki() {
        LOG.info("Sending google api request");
        DistanceMatrix distanceMatrix = DistanceMatrixApi.newRequest(GeoApiContextProvider.getContext())
                .origins(szell_kalman, petofi_hid_budai)
                .destinations(telki_center)
                .mode(TravelMode.DRIVING)
                .departureTime(Instant.now())
                .trafficModel(TrafficModel.BEST_GUESS)
                .awaitIgnoreError();
        InfluxDBConnection.saveEntry(
                new TrafficInfoEntry(null, "szell_kalman", "telki_center", distanceMatrix.rows[0].elements[0].durationInTraffic.inSeconds / 60,
                        String.valueOf(LocalDateTime.now().getDayOfWeek().getValue()),
                        String.valueOf(LocalDateTime.now().getHour())));
        InfluxDBConnection.saveEntry(
                new TrafficInfoEntry(null, "petofi_hid_budai", "telki_center", distanceMatrix.rows[1].elements[0].durationInTraffic.inSeconds / 60,
                        String.valueOf(LocalDateTime.now().getDayOfWeek().getValue()),
                        String.valueOf(LocalDateTime.now().getHour())));
    }

    @Scheduled(cron = "0 0/20 22-23 * * ?")
    public void Telki_Budapest_22_00() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        //System.out.println("Telki_Budapest_22_00: " + formatter.format(date));
        LOG.info("[SCHEDULED TASK] Telki_Budapest_22_00: " + formatter.format(date));
        requestTelkiBudapest();

    }

    @Scheduled(cron = "0 0/20 0-3 * * ?")
    public void Telki_Budapest_00_04() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        //System.out.println("Telki_Budapest_00_04: " + formatter.format(date));
        LOG.info("[SCHEDULED TASK] Telki_Budapest_00_04: " + formatter.format(date));
        requestTelkiBudapest();
    }

    @Scheduled(cron = "0 0/10 4-5 * * ?")
    public void Telki_Budapest_04_06() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        //System.out.println("Telki_Budapest_04_06: " + formatter.format(date));
        LOG.info("[SCHEDULED TASK] Telki_Budapest_04_06: " + formatter.format(date));
        requestTelkiBudapest();
    }

    @Scheduled(cron = "0 0/6 6-11 * * ?")
    public void Telki_Budapest_06_12() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        //System.out.println("Telki_Budapest_06_12: " + formatter.format(date));
        LOG.info("[SCHEDULED TASK] Telki_Budapest_06_12: " + formatter.format(date));
        requestTelkiBudapest();
    }

    @Scheduled(cron = "0 0/10 12-21 * * ?")
    public void Telki_Budapest_12_22() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        //System.out.println("Telki_Budapest_12_22: " + formatter.format(date));
        LOG.info("[SCHEDULED TASK] Telki_Budapest_12_22: " + formatter.format(date));
        requestTelkiBudapest();
    }

    @Scheduled(cron = "0 0/20 0-5 * * ?")
    public void Budapest_Telki_00_06() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        //System.out.println("Budapest_Telki_00_06: " + formatter.format(date));
        LOG.info("[SCHEDULED TASK] Budapest_Telki_00_06: " + formatter.format(date));
        requestBudapestTelki();
    }

    @Scheduled(cron = "0 0/10 6-13 * * ?")
    public void Budapest_Telki_06_14() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        //System.out.println("Budapest_Telki_06_14: " + formatter.format(date));
        LOG.info("[SCHEDULED TASK] Budapest_Telki_06_14: " + formatter.format(date));
        requestBudapestTelki();
    }

    @Scheduled(cron = "0 0/6 14-19 * * ?")
    public void Budapest_Telki_14_20() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        //System.out.println("Budapest_Telki_14_20: " + formatter.format(date));
        LOG.info("[SCHEDULED TASK] Budapest_Telki_14_20: " + formatter.format(date));
        requestBudapestTelki();
    }

    @Scheduled(cron = "0 0/10 20-23 * * ?")
    public void Budapest_Telki_20_24() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        //System.out.println("Budapest_Telki_20_24: " + formatter.format(date));
        LOG.info("[SCHEDULED TASK] Budapest_Telki_20_24: " + formatter.format(date));
        requestBudapestTelki();
    }

    //https://dzone.com/articles/running-on-time-with-springs-scheduled-tasks?fbclid=IwAR3HXEYVq4u_ftT3qzJX8oxa4GoO8lMlUUlbJA5vw40vaDHS2ruePbtYSwc

    // https://github.com/googlemaps/google-maps-services-java
    // https://developers.google.com/maps/documentation/distance-matrix/start
    // https://developers.google.com/maps/documentation/distance-matrix/intro#traffic-model

    //int value = entry.getTime().atZone(ZoneId.systemDefault()).getDayOfWeek().getValue(); //from 1 (Monday) to 7 (Sunday).
}
