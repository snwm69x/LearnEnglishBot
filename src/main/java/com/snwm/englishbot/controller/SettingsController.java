package com.snwm.englishbot.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.snwm.englishbot.entity.User;

@Controller
@RequestMapping("/settings")
public class SettingsController {

    @Value("${PROD_BOT_TOKEN}")
    private String BOT_TOKEN;

    @Value("${PROD_BOT_NAME}")
    private String BOT_USERNAME;

    @GetMapping
    public String getSettingsPage(Model model) {
        Authentication auth = (Authentication) SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        model.addAttribute("user", user);
        model.addAttribute("botToken", BOT_TOKEN);
        model.addAttribute("botUsername", BOT_USERNAME);
        return "settings";
    }

}
