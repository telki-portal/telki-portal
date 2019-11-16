package bme.tmit.telki.distance_matrix;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.model.*;

import java.time.Instant;

/**
 * Komment:
 *
 * Kérés URLként:
 * https://maps.googleapis.com/maps/api/distancematrix/json?origins=Seattle&destinations=San+Francisco&key=AIzaSyBcJixlXHjzXIc0McRXeGtaEDp_jQh9Ef0
 *
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
 *
 *
 */
public class DistanceMatrixClient {

    public void sendApiRequest() {
        System.out.println("starting request");

        final LatLng telki_center = new LatLng(47.5468889, 18.8283889);     // https://www.google.com/maps/@47.5475926,18.8278371,16.5z/data=!4m5!3m4!1s0x0:0x0!8m2!3d47.5468889!4d18.8283889
        final LatLng szell_kalman = new LatLng(47.5077219, 19.0227367);     // https://www.google.com/maps/@47.507037,19.0216853,17z/data=!4m5!3m4!1s0x4741dea0e74e6c29:0xcbcc12dee29046e7!8m2!3d47.5077219!4d19.0227367
        final LatLng petofi_hid_budai = new LatLng(47.4769369, 19.0594951); // https://www.google.com/maps/@47.4769405,19.0573064,17z/data=!3m1!4b1!4m5!3m4!1s0x4741ddab5b203951:0xf3231c10b7b810b1!8m2!3d47.4769369!4d19.0594951

        DistanceMatrix distanceMatrix = DistanceMatrixApi.newRequest(GeoApiContextProvider.getContext())
                .origins(telki_center)
                .destinations(szell_kalman, petofi_hid_budai)
                .mode(TravelMode.DRIVING)
                .departureTime(Instant.now())
                .trafficModel(TrafficModel.BEST_GUESS)
                .awaitIgnoreError();


        System.out.println(distanceMatrix.toString());
        for (DistanceMatrixRow row :distanceMatrix.rows) {
            System.out.println(row.toString());
            for (DistanceMatrixElement element : row.elements) {
                System.out.println("InTraffic: " + element.durationInTraffic + ", Időtartam: " + element.duration +", Távolság: " + element.distance);
            }
        }

    }

    // https://github.com/googlemaps/google-maps-services-java
    // https://developers.google.com/maps/documentation/distance-matrix/start
    // https://developers.google.com/maps/documentation/distance-matrix/intro#traffic-model

}
