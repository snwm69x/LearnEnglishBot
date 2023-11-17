package com.snwm.englishbot.handlers.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.snwm.englishbot.bot.EnglishWordBot;
import com.snwm.englishbot.component.KeyboardMaker;
import com.snwm.englishbot.handlers.MessageHandler;
import com.snwm.englishbot.service.impl.AdminControllerServiceImpl;

@Component("Выбрать сложность ⚙️")
public class ChooseDifficultMessageHandler implements MessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(ChooseDifficultMessageHandler.class);

    @Autowired
    private AdminControllerServiceImpl adminControllerServiceImpl;
    @Autowired
    private KeyboardMaker keyboardMaker;

    @Override
    public void handle(Message message, EnglishWordBot bot) {
        adminControllerServiceImpl.startMessageProcessing();
        logger.info("Смена сложности для пользователя: {}",
                message.getFrom().getUserName());
        SendChatAction sendChatAction = SendChatAction.builder()
                .chatId(message.getChatId().toString())
                .action("typing")
                .build();
        try {
            bot.execute(sendChatAction);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        SendMessage startMessage = new SendMessage();
        startMessage.setChatId(message.getChatId().toString());
        startMessage.setText("Выбери сложность:");
        InlineKeyboardMarkup keyboard = keyboardMaker.getDifficultLevelKeyboard();
        startMessage.setReplyMarkup(keyboard);
        startMessage.disableNotification();
        try {
            bot.execute(startMessage);
        } catch (TelegramApiException e) {
            adminControllerServiceImpl.setErrors(adminControllerServiceImpl.getErrors() + 1);
            logger.error("Error while sending start message: {}", e.getMessage());
        }
        adminControllerServiceImpl.endMessageProcessing();
        adminControllerServiceImpl
                .recordNews("Пользователь: " + message.getFrom().getUserName() + " запросил выбор сложности");
    }

}
