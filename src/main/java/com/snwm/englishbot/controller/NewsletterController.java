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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        model.addAttribute("activeusers", userService.getAllUsers().size());
        return "newsletter";
    }

    @PostMapping("/sendmessage")
    public String sendNewsletter(@RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "text", required = false) String text,
            RedirectAttributes redirectAttributes) {
        if ((image == null || image.isEmpty()) && (text == null || text.trim().isEmpty())) {
            redirectAttributes.addFlashAttribute("error", "You must provide at least an image or text.");
            return "redirect:/newsletter";
        }
        if (image == null || image.isEmpty()) {
            newsletterServiceImpl.runPromotion(text);
            redirectAttributes.addFlashAttribute("successMsg", newsletterServiceImpl.getSuccessMessages());
            redirectAttributes.addFlashAttribute("failedMsg", newsletterServiceImpl.getFailedMessages());
            redirectAttributes.addFlashAttribute("errorMessages", newsletterServiceImpl.getErrorMessages());
            redirectAttributes.addFlashAttribute("message", "Newsletter sent successfully.");
            redirectAttributes.addFlashAttribute("activeusers", userService.getAllUsers().size());
            return "redirect:/newsletter";
        }
        if (text == null || text.trim().isEmpty()) {
            newsletterServiceImpl.runPromotion(image);
            redirectAttributes.addFlashAttribute("successMsg", newsletterServiceImpl.getSuccessMessages());
            redirectAttributes.addFlashAttribute("failedMsg", newsletterServiceImpl.getFailedMessages());
            redirectAttributes.addFlashAttribute("errorMessages", newsletterServiceImpl.getErrorMessages());
            redirectAttributes.addFlashAttribute("message", "Newsletter sent successfully.");
            redirectAttributes.addFlashAttribute("activeusers", userService.getAllUsers().size());
            return "redirect:/newsletter";
        }
        if (text != null && image != null) {
            newsletterServiceImpl.runPromotion(text, image);
            redirectAttributes.addFlashAttribute("successMsg", newsletterServiceImpl.getSuccessMessages());
            redirectAttributes.addFlashAttribute("failedMsg", newsletterServiceImpl.getFailedMessages());
            redirectAttributes.addFlashAttribute("errorMessages", newsletterServiceImpl.getErrorMessages());
            redirectAttributes.addFlashAttribute("message", "Newsletter sent successfully.");
            redirectAttributes.addFlashAttribute("activeusers", userService.getAllUsers().size());
            return "redirect:/newsletter";
        }
        redirectAttributes.addFlashAttribute("message", "Newsletter sent successfully.");
        return "redirect:/newsletter";
    }

}
