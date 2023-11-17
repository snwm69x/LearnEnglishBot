package com.snwm.englishbot.handlers;

import org.telegram.telegrambots.meta.api.objects.Update;

import com.snwm.englishbot.bot.EnglishWordBot;

public interface CallbackHandler {
    void handle(Update update, EnglishWordBot bot);
}
