package com.snwm.englishbot.handlers;

import org.telegram.telegrambots.meta.api.objects.Message;

import com.snwm.englishbot.bot.EnglishWordBot;

public interface MessageHandler {
    void handle(Message message, EnglishWordBot bot);
}
