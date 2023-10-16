package com.snwm.englishbot.controller;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.snwm.englishbot.entity.User;
import com.snwm.englishbot.entity.enums.UserType;
import com.snwm.englishbot.service.UserService;

@RestController
public class TelegramAuthController {

    @Autowired
    private final UserService userService;

    public TelegramAuthController(UserService userService) {
        this.userService = userService;
    }

    private final String BOT_TOKEN = "6566742010:AAHYTvo8_s_CZ95VYzLiz2a6t51PaSiTycY";

    @GetMapping("/login/telegram")
    public String handleTelegramAuth(
            @RequestParam Map<String, String> params) {
        // Сортируем GET-параметры по их именам в алфавитном порядке
        Map<String, String> sortedParams = new TreeMap<>(params);
        // Формируем строку в формате name=value
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
        }
        String data = sb.toString();
        System.out.println(data);

        // Формируем HMAC с использованием SHA-256
        String hmac = "";
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(BOT_TOKEN.getBytes(), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] bytes = mac.doFinal(data.getBytes());
            StringBuilder sb2 = new StringBuilder();
            for (byte b : bytes) {
                sb2.append(String.format("%02x", b));
            }
            hmac = sb2.toString();
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }

        // Сравниваем полученный HMAC с hash, который вам прислал Telegram
        String hash = params.get("hash");
        if (hmac.equals(hash)) {
            // Если данные подлинные, вы можете авторизовать пользователя
            User user = userService.getUserByUsername(params.get("username")).get(0);
                if(user.getUserType().equals(UserType.ADMIN)) {
                    return "redirect:/admin";
                } else {
                    return "errorauth";
                }
        } else {
            // Если данные не подлинные, вы должны вернуть ошибку
            return "error";
        }
    }
}
