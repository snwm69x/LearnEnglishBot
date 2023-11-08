package com.snwm.englishbot.service;

import org.springframework.web.multipart.MultipartFile;

public interface NewsletterService {
    void runPromotion(String messageText);

    void runPromotion(MultipartFile imageFile);

    void runPromotion(String messageText, MultipartFile imageFile);
}
