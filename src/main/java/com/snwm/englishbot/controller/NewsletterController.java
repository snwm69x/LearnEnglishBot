package com.snwm.englishbot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.snwm.englishbot.entity.User;
import com.snwm.englishbot.service.UserService;
import com.snwm.englishbot.service.impl.NewsletterServiceImpl;

@Controller
@RequestMapping("/newsletter")
public class NewsletterController {

    @Autowired
    private NewsletterServiceImpl newsletterServiceImpl;
    @Autowired
    private UserService userService;

    @GetMapping
    public String getNewsletterPage(Model model) {
        Authentication auth = (Authentication) SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        model.addAttribute("user", user);
        return "newsletter";
    }

    @PostMapping("/sendmessage")
    public String sendNewsletter(@RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "text", required = false) String text,
            Model model) {
        if ((image == null || image.isEmpty()) && (text == null || text.trim().isEmpty())) {
            model.addAttribute("error", "You must provide at least an image or text.");
            return "newsletter";
        }
        if (image == null || image.isEmpty()) {
            newsletterServiceImpl.runPromotion(text);
            model.addAttribute("successMsg", newsletterServiceImpl.getSuccessMessages());
            model.addAttribute("failedMsg", newsletterServiceImpl.getFailedMessages());
            model.addAttribute("errorMessages", newsletterServiceImpl.getErrorMessages());
            model.addAttribute("message", "Newsletter sent successfully.");
            model.addAttribute("users", userService.getAllUsers().size());
            return "newsletter";
        }
        if (text == null || text.trim().isEmpty()) {
            newsletterServiceImpl.runPromotion(image);
            model.addAttribute("successMsg", newsletterServiceImpl.getSuccessMessages());
            model.addAttribute("failedMsg", newsletterServiceImpl.getFailedMessages());
            model.addAttribute("errorMessages", newsletterServiceImpl.getErrorMessages());
            model.addAttribute("message", "Newsletter sent successfully.");
            model.addAttribute("users", userService.getAllUsers().size());
            return "newsletter";
        }
        if (text != null && image != null) {
            newsletterServiceImpl.runPromotion(text, image);
            model.addAttribute("successMsg", newsletterServiceImpl.getSuccessMessages());
            model.addAttribute("failedMsg", newsletterServiceImpl.getFailedMessages());
            model.addAttribute("errorMessages", newsletterServiceImpl.getErrorMessages());
            model.addAttribute("message", "Newsletter sent successfully.");
            model.addAttribute("users", userService.getAllUsers().size());
            return "newsletter";
        }
        model.addAttribute("message", "Newsletter sent successfully.");
        return "newsletter";
    }

}
