package com.snwm.englishbot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.snwm.englishbot.entity.User;
import com.snwm.englishbot.service.UserService;
import com.snwm.englishbot.service.impl.AdminControllerServiceImpl;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private final AdminControllerServiceImpl adminControllerServiceImpl;

    @Autowired
    private final UserService userService;

    public AdminController(AdminControllerServiceImpl adminControllerServiceImpl, UserService userService) {
        this.adminControllerServiceImpl = adminControllerServiceImpl;
        this.userService = userService;
    }

    @GetMapping
    public String getAdminPage(Model model) {
        Authentication auth = (Authentication) SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        model.addAttribute("user", user);
        model.addAttribute("uptime", adminControllerServiceImpl.getUptime());
        model.addAttribute("handledmessages", adminControllerServiceImpl.getHandledMessages());
        model.addAttribute("errors", adminControllerServiceImpl.getErrors());
        model.addAttribute("newusers", adminControllerServiceImpl.getNewUsers());
        model.addAttribute("allusers", userService.getAllUsers().size());
        model.addAttribute("recentnews", adminControllerServiceImpl.getRecentNews());
        model.addAttribute("averageresponsetime", adminControllerServiceImpl.getAverageResponseTime());
        return "admin";
    }
}
