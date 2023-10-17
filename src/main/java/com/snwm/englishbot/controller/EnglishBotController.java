package com.snwm.englishbot.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.snwm.englishbot.entity.User;
import com.snwm.englishbot.entity.UserWordStats;
import com.snwm.englishbot.service.UserService;
import com.snwm.englishbot.service.UserWordStatsService;

@Controller
public class EnglishBotController {

    @Autowired
    private final UserService userService;

    @Autowired
    private final UserWordStatsService userWordStatsService;

    public EnglishBotController(UserService userService, UserWordStatsService userWordStatsService) {
        this.userService = userService;
        this.userWordStatsService = userWordStatsService;
    }

    @GetMapping("/login")
    public String getLoginPage() {
        return "login";
    }

    @GetMapping("/admin")
    public String getAdminPage(Model model) {
        List<User> users = userService.getAllUsers();
        Map<User, UserWordStats> userstats = new HashMap<>();
        for (User user : users) {
            UserWordStats userWordStats = userWordStatsService.getStatsByUser(user);
            userstats.put(user, userWordStats);
        }
        model.addAttribute("users", users);
        model.addAttribute("userstats", userstats);
        return "admin";
    }
}
