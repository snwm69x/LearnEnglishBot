package com.snwm.englishbot.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.snwm.englishbot.bot.EnglishWordBot;
import com.snwm.englishbot.service.SettingsControllerService;

public class SettingsControllerServiceImpl implements SettingsControllerService {

    @Autowired
    private EnglishWordBot englishWordBot;

    @Override
    public User getInfoAboutBot() {
        try {
            return englishWordBot.getMe();
        } catch (TelegramApiException e) {
            return null;
        }
    }

}
