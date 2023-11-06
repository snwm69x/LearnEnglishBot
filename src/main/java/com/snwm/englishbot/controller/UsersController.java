package com.snwm.englishbot.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.snwm.englishbot.entity.User;
import com.snwm.englishbot.entity.UserWordStats;
import com.snwm.englishbot.model.UserStatsSummary;
import com.snwm.englishbot.service.UserService;
import com.snwm.englishbot.service.UserWordStatsService;

@Controller
@RequestMapping("/users")
public class UsersController {

    @Autowired
    private final UserService userService;
    @Autowired
    private final UserWordStatsService userWordStatsService;

    public UsersController(UserService userService, UserWordStatsService userWordStatsService) {
        this.userService = userService;
        this.userWordStatsService = userWordStatsService;
    }

    @GetMapping
    public String getUsersPage(Model model) {
        Authentication auth = (Authentication) SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        model.addAttribute("user", user);
        return "users";
    }

    @GetMapping("/search")
    public String search(@RequestParam("search") String search, Model model) {
        List<User> users = new ArrayList<>();
        if (search == null || search.isEmpty()) {
            users = userService.getAllUsers();
        } else {
            users = userService.getUserByUsername(search);
        }
        Map<User, UserStatsSummary> userstats = new HashMap<>();
        for (User user : users) {
            List<UserWordStats> userWordStats = userWordStatsService.getStatsByUser(user);
            UserStatsSummary summary = new UserStatsSummary();
            LocalDateTime lastAttempt = null;
            for (UserWordStats stats : userWordStats) {
                summary.setCorrectAttempts(summary.getCorrectAttempts() + stats.getCorrectAttempts());
                summary.setIncorrectAttempts(summary.getIncorrectAttempts() + stats.getIncorrectAttempts());
                if (lastAttempt == null || stats.getLastAttempt().isAfter(lastAttempt)) {
                    lastAttempt = stats.getLastAttempt();
                }
            }
            summary.setLastAttempt(lastAttempt);
            if (summary.getCorrectAttempts() + summary.getIncorrectAttempts() > 0) {
                summary.setSuccessRate((double) summary.getCorrectAttempts()
                        / (summary.getCorrectAttempts() + summary.getIncorrectAttempts()));
            }
            userstats.put(user, summary);
        }
        model.addAttribute("users", users);
        model.addAttribute("userstats", userstats);
        return "users";
    }

}
