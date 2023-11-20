package com.snwm.englishbot.bot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import com.snwm.englishbot.bot.snwm.Snwm69xServiceImpl;

@Component
public class Snwm69xBot extends TelegramLongPollingBot {

    private final String token;
    private final String username;
    private final Long ADMIN_CHAT_ID;

    @Autowired
    private Snwm69xServiceImpl snwm69xServiceImpl;

    Snwm69xBot(@Value("${SNWM_BOT_TOKEN}") String token,
            @Value("${SNWM_BOT_NAME}") String username,
            @Value("${SNWM_BOT_ADMIN_CHANNEL}") Long chatId) {
        this.token = token;
        this.username = username;
        this.ADMIN_CHAT_ID = chatId;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().getChatId().equals(ADMIN_CHAT_ID)) {
            if (update.getMessage().hasText()) {
                String message = update.getMessage().getText();
                if (message.startsWith("/setadmin ")) {
                    String[] parts = message.split(" ", 2);
                    if (parts.length > 1) {
                        String username = parts[1];
                        snwm69xServiceImpl.handleSetAdminRights(username, this);
                    }
                }
            }
        }
    }

    @Override
    public String getBotUsername() {
        return this.username;
    }

    @Override
    public String getBotToken() {
        return this.token;
    }

}
