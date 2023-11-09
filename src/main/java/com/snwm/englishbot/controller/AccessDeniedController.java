package com.snwm.englishbot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/access-denied")
public class AccessDeniedController {

    @GetMapping
    public String getPage() {
        return "accessdenied";
    }
}
