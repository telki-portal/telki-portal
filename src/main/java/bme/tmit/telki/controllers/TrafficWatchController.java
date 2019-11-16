package bme.tmit.telki.controllers;

import bme.tmit.telki.distance_matrix.DistanceMatrixClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
    public String trafficRequest(Model model) {
        model.addAttribute("name", "traffic");

        DistanceMatrixClient distanceMatrixClient = new DistanceMatrixClient();
        //distanceMatrixClient.sendApiRequest();
        distanceMatrixClient.Telki_Budapest_22_00();
        distanceMatrixClient.Telki_Budapest_00_04();
        distanceMatrixClient.Telki_Budapest_04_06();
        distanceMatrixClient.Telki_Budapest_06_12();
        distanceMatrixClient.Telki_Budapest_12_22();

        distanceMatrixClient.Budapest_Telki_00_06();
        distanceMatrixClient.Budapest_Telki_06_14();
        distanceMatrixClient.Budapest_Telki_14_20();
        distanceMatrixClient.Budapest_Telki_20_24();

        return "main";
    }
}
