package com.snwm.englishbot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/newsletter")
public class NewsletterController {

    @GetMapping
    public String getNewsletterPage() {
        return "newsletter";
    }

}
