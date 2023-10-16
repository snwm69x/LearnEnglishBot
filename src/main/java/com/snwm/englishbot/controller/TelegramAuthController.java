package com.snwm.englishbot.controller;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
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
    public String handleTelegramAuth(@RequestParam Map<String, String> params) {
        // Сортируем GET-параметры по их именам в алфавитном порядке
        Map<String, String> sortedParams = new TreeMap<>(params);
        // Удаляем параметр hash из отсортированных параметров
        sortedParams.remove("hash");

        // Формируем строку в формате name=value
        StringBuilder sb = new StringBuilder();
        for (Iterator<Map.Entry<String, String>> iterator = sortedParams.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry<String, String> entry = iterator.next();
            sb.append(entry.getKey()).append("=").append(entry.getValue());
            if (iterator.hasNext()) {
                sb.append("\n");
            }
        }
        String dataCheckString = sb.toString();

        // Вычисляем SHA256 хеш токена бота
        MessageDigest digest;
        byte[] secretKey;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            secretKey = digest.digest(BOT_TOKEN.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "error";
        }

        // Формируем HMAC с использованием SHA-256
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
            e.printStackTrace();
            return "error";
        }

        // Сравниваем полученный HMAC с hash, который вам прислал Telegram
        String receivedHash = params.get("hash");
        if (hmac.equals(receivedHash)) {
            // Если данные подлинные, вы можете авторизовать пользователя
            User user = userService.getUserByUsername(params.get("username")).get(0);
            if (user.getUserType().equals(UserType.ADMIN)) {
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
