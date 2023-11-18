package com.snwm.englishbot.controller;

import java.util.LinkedList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.snwm.englishbot.entity.User;
import com.snwm.englishbot.service.UserService;
import com.snwm.englishbot.service.impl.StatisticsServiceImpl;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private final StatisticsServiceImpl statisticsServiceImpl;

    @Autowired
    private final UserService userService;

    public AdminController(StatisticsServiceImpl adminControllerServiceImpl, UserService userService) {
        this.statisticsServiceImpl = adminControllerServiceImpl;
        this.userService = userService;
    }

    @GetMapping
    public String getAdminPage(Model model) {
        Authentication auth = (Authentication) SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        LinkedList<String> recentNews = statisticsServiceImpl.getRecentNews();
        // Collections.reverse(recentNews);
        model.addAttribute("user", user);
        model.addAttribute("uptime", statisticsServiceImpl.getUptime());
        model.addAttribute("handledmessages", statisticsServiceImpl.getHandledMessages());
        model.addAttribute("errors", statisticsServiceImpl.getErrors());
        model.addAttribute("newusers", statisticsServiceImpl.getNewUsers());
        model.addAttribute("allusers", userService.getAllUsers().size());
        model.addAttribute("recentnews", recentNews);
        model.addAttribute("averageresponsetime", statisticsServiceImpl.getAverageResponseTime());
        return "admin";
    }
}
