package com.snwm.englishbot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.snwm.englishbot.entity.PromotedChannel;
import com.snwm.englishbot.entity.User;
import com.snwm.englishbot.service.PromotedChannelService;

@Controller
@RequestMapping("/settings")
public class SettingsController {

    @Value("${PROD_BOT_TOKEN}")
    private String BOT_TOKEN;

    @Value("${PROD_BOT_NAME}")
    private String BOT_USERNAME;

    @Autowired
    private PromotedChannelService promotedChannelService;

    @GetMapping
    public String getSettingsPage(Model model) {
        Authentication auth = (Authentication) SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        model.addAttribute("user", user);
        model.addAttribute("botToken", BOT_TOKEN);
        model.addAttribute("botUsername", BOT_USERNAME);
        model.addAttribute("promotedChannel", promotedChannelService.getChannel());
        return "settings";
    }

    @PostMapping("/update-promoted-channel")
    public String updatePromotedChannel(PromotedChannel promotedChannel, RedirectAttributes redirectAttributes) {
        try {
            promotedChannelService.setChannel(promotedChannel);
            redirectAttributes.addFlashAttribute("message", "Promoted channel updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update promoted channel");
        }
        return "redirect:/settings";
    }

}
