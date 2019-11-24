package bme.tmit.telki.controllers;

import bme.tmit.telki.data.InfluxDBConnection;
import bme.tmit.telki.data.TrafficInfoEntry;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

import static bme.tmit.telki.TelkiPortalApplication.LOG;
import static bme.tmit.telki.TelkiPortalApplication.distanceMatrixClient;

/**
 * MVC controller.
 */
@Controller
public class TrafficWatchController {

    /**
     * Handles GET requests for the path "/" ("localhost:8080/")
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

    @GetMapping("/request")
    public String forceRequest(Model model) {
        LOG.debug("GET '/request'");
        distanceMatrixClient.requestTelkiBudapest();
        return "main";
    }
}
