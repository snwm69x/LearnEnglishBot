package com.snwm.englishbot.bot;

import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class Snwm69xBot extends TelegramLongPollingBot {

    private final String token;
    private final String username;

    Snwm69xBot(@Value("${SNWM_BOT_TOKEN}") String token,
            @Value("${SNWM_BOT_NAME}") String username) {
        this.token = token;
        this.username = username;
    }

    @Override
    public void onUpdateReceived(Update update) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onUpdateReceived'");
    }

    @Override
    public String getBotUsername() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBotUsername'");
    }

    @Override
    public String getBotToken() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBotToken'");
    }

}
