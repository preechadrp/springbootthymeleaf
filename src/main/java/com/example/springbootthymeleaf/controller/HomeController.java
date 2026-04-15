package com.example.springbootthymeleaf.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("message", "สวัสดี Thymeleaf");
        model.addAttribute("users", List.of("Preecha", "Somchai", "Suda"));
        return "home"; // ไปที่ templates/home.html
    }
}