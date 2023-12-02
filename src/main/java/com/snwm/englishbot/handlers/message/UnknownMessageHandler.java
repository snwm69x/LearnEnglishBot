package com.snwm.englishbot.handlers.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.snwm.englishbot.bot.EnglishWordBot;
import com.snwm.englishbot.handlers.MessageHandler;
import com.snwm.englishbot.service.impl.StatisticsServiceImpl;

@Component("unknown")
public class UnknownMessageHandler implements MessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(UnknownMessageHandler.class);

    @Autowired
    private StatisticsServiceImpl statisticsServiceImpl;

    @Override
    public void handle(Message message, EnglishWordBot bot) {
        SendChatAction sendChatAction = SendChatAction.builder()
                .chatId(message.getChatId().toString())
                .action("typing")
                .build();
        try {
            bot.execute(sendChatAction);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        logger.info("Unknown command by User: {} msg: {}", message.getFrom().getUserName(), message.getText());
        SendMessage unknownMessage = SendMessage.builder()
                .chatId(message.getChatId().toString())
                .text("Неизвестная команда")
                .build();
        try {
            bot.execute(unknownMessage);
        } catch (TelegramApiException e) {
            statisticsServiceImpl.setErrors(statisticsServiceImpl.getErrors() + 1);
            logger.error("Error while sending unknown command message: {}", e.getMessage());
        }
        // for logs in tg
        var from = message.getFrom();

        String username = from.getUserName();
        if (username == null) {
            String firstName = from.getFirstName() != null ? from.getFirstName() : "";
            String lastName = from.getLastName() != null ? from.getLastName() : "";
            username = (firstName + " " + lastName).trim();
        }
        //
        statisticsServiceImpl
                .recordNews("Неизвестная команда от пользователя: " + username + " Сообщение: "
                        + message.getText());
    }

}
