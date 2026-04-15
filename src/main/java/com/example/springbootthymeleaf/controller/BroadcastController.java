package com.example.springbootthymeleaf.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BroadcastController {

	@GetMapping("/broadcast")
    public String home() {
        return "broadcast";
    }
	
}
