package com.snwm.englishbot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EnglishBotController {

    @GetMapping("/login")
    public String getAdminPage() {
        return "login";
    }
}
