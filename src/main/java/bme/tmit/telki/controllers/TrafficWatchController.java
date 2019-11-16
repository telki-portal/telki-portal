package bme.tmit.telki.controllers;

import bme.tmit.telki.data.InfluxDBConnection;
import bme.tmit.telki.data.TrafficInfoEntry;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * MVC controller for application.
 */
@Controller
public class TrafficWatchController {

    /**
     * Handles GET requests for the path "/" ("localhost:8080/")
     * @param model Provided by Spring
     * @return view name as String (resources/templates/[view_name].html)
     */
    @GetMapping("/")
    public String greeting(Model model) {
        model.addAttribute("name", "Hunor");    // model attributes are accessible from views
        return "main";
    }

    @GetMapping("/traffic")
    public String trafficPage(Model model) {
        List<TrafficInfoEntry> entries = InfluxDBConnection.getEntries();
        model.addAttribute("entries", entries);

        return "traffic";
    }
}
