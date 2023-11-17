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
import com.snwm.englishbot.service.impl.AdminControllerServiceImpl;

@Component("unknown")
public class UnknownMessageHandler implements MessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(UnknownMessageHandler.class);

    @Autowired
    private AdminControllerServiceImpl adminControllerServiceImpl;

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
            adminControllerServiceImpl.setErrors(adminControllerServiceImpl.getErrors() + 1);
            logger.error("Error while sending unknown command message: {}", e.getMessage());
        }
        adminControllerServiceImpl
                .recordNews("Неизвестная команда от пользователя: " + message.getFrom().getUserName() + " Сообщение: "
                        + message.getText());
    }

}
