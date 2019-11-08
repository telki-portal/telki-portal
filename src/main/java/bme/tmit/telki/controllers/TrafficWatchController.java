package bme.tmit.telki.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TrafficWatchController {

    @GetMapping("/")
    public String greeting(Model model) {
        model.addAttribute("name", "Hunor");
        return "main";
    }
}
