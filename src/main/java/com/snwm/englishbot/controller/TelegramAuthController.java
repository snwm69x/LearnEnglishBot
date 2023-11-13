package com.snwm.englishbot.controller;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.snwm.englishbot.entity.User;
import com.snwm.englishbot.entity.enums.UserType;
import com.snwm.englishbot.service.UserService;

@Controller
public class TelegramAuthController {

    @Autowired
    private final UserService userService;
    private final String BOT_TOKEN = "6834770884:AAFemvRgrQmFf9DtPv8yNG0uYLFEoo9lA2M";

    public TelegramAuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login/telegram")
    public String handleTelegramAuth(@RequestParam Map<String, String> params) {

        Map<String, String> sortedParams = new TreeMap<>(params);
        sortedParams.remove("hash");

        StringBuilder sb = new StringBuilder();
        for (Iterator<Map.Entry<String, String>> iterator = sortedParams.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry<String, String> entry = iterator.next();
            sb.append(entry.getKey()).append("=").append(entry.getValue());
            if (iterator.hasNext()) {
                sb.append("\n");
            }
        }
        String dataCheckString = sb.toString();

        MessageDigest digest;
        byte[] secretKey;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            secretKey = digest.digest(BOT_TOKEN.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            System.out.println("error while SHA256");
            e.printStackTrace();
            return "error";
        }

        String hmac = "";
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey, "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hmacBytes = mac.doFinal(dataCheckString.getBytes());
            StringBuilder sb2 = new StringBuilder();
            for (byte b : hmacBytes) {
                sb2.append(String.format("%02x", b));
            }
            hmac = sb2.toString();
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            System.out.println("error while HMAC");
            e.printStackTrace();
            return "error";
        }

        String receivedHash = params.get("hash");
        if (hmac.equals(receivedHash)) {
            User user = userService.getUserByUsername(params.get("username")).get(0);
            if (user.getUserType().equals(UserType.ADMIN)) {
                List<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"));
                Authentication auth = new UsernamePasswordAuthenticationToken(user, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(auth);
                return "redirect:/admin";
            } else {
                return "accessdenied";
            }
        } else {
            return "error";
        }
    }
}
