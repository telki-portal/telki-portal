package bme.tmit.telki.controllers;

import bme.tmit.telki.data.InfluxDBConnection;
import bme.tmit.telki.data.TrafficInfoEntry;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static bme.tmit.telki.TelkiPortalApplication.LOG;
import static bme.tmit.telki.TelkiPortalApplication.distanceMatrixClient;
import static bme.tmit.telki.distance_matrix.DistanceMatrixClient.parsePlace;
import static bme.tmit.telki.distance_matrix.DistanceMatrixClient.place.telki_center;

/**
 * MVC controller.
 */
@Controller
public class TrafficWatchController {

    /**
     * Handles GET requests for the path "/" ("localhost:8080/")
     *
     * @param model Provided by Spring
     * @return view name as String (resources/templates/[view_name].html)
     */
    @GetMapping("/")
    public String trafficPage(Model model) {
        LOG.debug("GET '/'");
        List<TrafficInfoEntry> entries = InfluxDBConnection.getEntries();
        model.addAttribute("entries", entries);

        return "traffic";
    }

    @GetMapping("/telki_petofi")
    public String telkiPetofi(Model model) {
        return "telki_petofi";
    }

    @GetMapping("/telki_szell")
    public String telkiSzell(Model model) {
        return "telki_szell";
    }

    @GetMapping("/petofi_telki")
    public String petofiTelki(Model model) {
        return "petofi_telki";
    }

    @GetMapping("/szell_telki")
    public String szellTelki(Model model) {
        return "szell_telki";
    }

    @GetMapping("/request")
    public String forceRequest(Model model) {
        LOG.debug("GET '/request'");
        distanceMatrixClient.requestTelkiBudapest();
        return "main";
    }

    @GetMapping("/graph")
    public String trafficgraph(Model model) {
        LOG.debug("GET '/'");
        List<TrafficInfoEntry> entries = InfluxDBConnection.getEntries();
        model.addAttribute("entries", entries);

        return "trafficgraph";
    }

    @RequestMapping(value = "/route", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TrafficInfoEntry>> out(@RequestParam String dest) {
        LOG.debug("GET '/route'");
        List<TrafficInfoEntry> entries = InfluxDBConnection
                .getRouteInfo(telki_center, parsePlace(dest));

        return new ResponseEntity<>(entries, HttpStatus.OK);
    }
}
