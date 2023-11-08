package com.snwm.englishbot.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.snwm.englishbot.bot.EnglishWordBot;
import com.snwm.englishbot.entity.User;
import com.snwm.englishbot.service.NewsletterService;
import com.snwm.englishbot.service.UserService;

import lombok.Getter;

@Service
@Getter
public class NewsletterServiceImpl implements NewsletterService {

    private List<String> errorMessages = new ArrayList<>();
    private int successMessages = 0;
    private int failedMessages = 0;

    @Autowired
    private EnglishWordBot englishWordBot;

    @Autowired
    private UserService userService;

    @Override
    public void runPromotion(String messageText) {
        errorMessages.clear();
        successMessages = 0;
        failedMessages = 0;
        List<User> users = userService.getAllUsers();
        for (User usr : users) {
            SendMessage message = SendMessage.builder()
                    .chatId(usr.getChatId().toString())
                    .text(messageText)
                    .build();
            try {
                englishWordBot.execute(message);
                successMessages++;
            } catch (TelegramApiException e) {
                errorMessages.add("Ошибка во время отправки сообщения пользователю: " + usr.getUsername());
                failedMessages++;
                e.printStackTrace();
            }
        }
    }

    @Override
    public void runPromotion(MultipartFile imageFile) {
        errorMessages.clear();
        successMessages = 0;
        failedMessages = 0;
        List<User> users = userService.getAllUsers();
        for (User user : users) {
            try {
                InputFile photo = new InputFile(imageFile.getInputStream(), imageFile.getOriginalFilename());
                SendPhoto sendPhoto = SendPhoto.builder()
                        .chatId(user.getChatId().toString())
                        .photo(photo)
                        .build();
                englishWordBot.execute(sendPhoto);
                successMessages++;
            } catch (TelegramApiException | IOException e) {
                errorMessages.add("Ошибка во время отправки сообщения пользователю: " + user.getUsername());
                failedMessages++;
                e.printStackTrace();
            }

        }

    }

    @Override
    public void runPromotion(String messageText, MultipartFile imageFile) {
        errorMessages.clear();
        successMessages = 0;
        failedMessages = 0;
        List<User> users = userService.getAllUsers();
        for (User user : users) {
            try {
                InputFile photo = new InputFile(imageFile.getInputStream(), imageFile.getOriginalFilename());
                SendPhoto sendPhoto = SendPhoto.builder()
                        .chatId(user.getChatId().toString())
                        .photo(photo)
                        .caption(messageText)
                        .build();
                englishWordBot.execute(sendPhoto);
                successMessages++;
            } catch (TelegramApiException | IOException e) {
                errorMessages.add("Ошибка во время отправки сообщения пользователю: " + user.getUsername());
                failedMessages++;
                e.printStackTrace();
            }
        }
    }

}
