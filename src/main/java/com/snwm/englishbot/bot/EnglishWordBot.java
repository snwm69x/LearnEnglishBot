package com.snwm.englishbot.bot;

import java.util.Map;
import com.snwm.englishbot.handlers.CallbackHandler;
import com.snwm.englishbot.handlers.MessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class EnglishWordBot extends TelegramLongPollingBot {

    private static final Logger logger = LoggerFactory.getLogger(EnglishWordBot.class);
    private final String token;
    private final String username;

    @Autowired
    private Map<String, MessageHandler> messageHandlers;
    @Autowired
    private Map<String, CallbackHandler> callbackHandlers;

    EnglishWordBot(@Value("${PROD_BOT_TOKEN}") String token,
            @Value("${PROD_BOT_NAME}") String username) {
        this.token = token;
        this.username = username;
    }

    @PostConstruct
    public void start() {
        logger.info("username: {}, token: {}", username, token);
    }

    @PreDestroy
    public void destroy() {
        logger.info("username: {}, token: {} stopped working", username, token);
    }

    @Override
    public void onUpdateReceived(Update update) {
        // Обработка команд
        if (update.hasMessage() && update.getMessage().hasText()) {
            MessageHandler handler = messageHandlers.get(update.getMessage().getText());
            if (handler != null) {
                handler.handle(update.getMessage(), this);
            } else {
                handler = messageHandlers.get("unknown");
                handler.handle(update.getMessage(), this);
            }
        }
        // Обработка Callback
        if (update.hasCallbackQuery()) {
            CallbackHandler handler = callbackHandlers.get(update.getCallbackQuery().getData().split(":")[0]);
            if (handler != null) {
                handler.handle(update, this);
            }
        }
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public String getBotUsername() {
        return username;
    }

}