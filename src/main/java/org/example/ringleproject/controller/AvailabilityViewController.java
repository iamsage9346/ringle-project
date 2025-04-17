package org.example.ringleproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AvailabilityViewController {

    @GetMapping("/availability")
    public String availabilityView() {
        return "availability";
    }
}
