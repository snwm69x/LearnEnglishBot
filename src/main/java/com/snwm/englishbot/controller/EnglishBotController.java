package com.snwm.englishbot.controller;

import com.snwm.englishbot.entity.Word;
import com.snwm.englishbot.service.WordService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EnglishBotController {

    @GetMapping("/login")
    public String getAdminPage() {
        return "login";
    }
}
