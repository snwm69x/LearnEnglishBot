package com.snwm.englishbot.handlers.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.snwm.englishbot.bot.EnglishWordBot;
import com.snwm.englishbot.component.KeyboardMaker;
import com.snwm.englishbot.entity.User;
import com.snwm.englishbot.handlers.MessageHandler;
import com.snwm.englishbot.service.UserService;
import com.snwm.englishbot.service.impl.StatisticsServiceImpl;

@Component("/start")
public class StartMessageHandler implements MessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(StartMessageHandler.class);

    @Autowired
    private StatisticsServiceImpl statisticsServiceImpl;
    @Autowired
    private UserService userService;
    @Autowired
    private KeyboardMaker keyboardMaker;

    @Transactional
    @Override
    public void handle(Message message, EnglishWordBot bot) {
        statisticsServiceImpl.startMessageProcessing();
        logger.info("Обработка команды /start для пользователя: {}",
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
        User user = userService.getUserByChatId(message.getChatId());
        if (user == null) {
            statisticsServiceImpl.setNewUsers(statisticsServiceImpl.getNewUsers() + 1);
            userService.createNewUser(message);

        }
        SendMessage startMessage = SendMessage.builder()
                .chatId(message.getChatId().toString())
                .text("Привет, я бот для изучения английского языка. \nНажимай на кнопку 'Новое слово', выполняй задания. \nПоднимай рейтинг и соревнуйся с другими людьми.")
                .replyMarkup(keyboardMaker.getMainKeyboard())
                .build();
        try {
            bot.execute(startMessage);
        } catch (TelegramApiException e) {
            statisticsServiceImpl.setErrors(statisticsServiceImpl.getErrors() + 1);
            logger.error("Error while sending start message: {}", e.getMessage());
        }
        statisticsServiceImpl.endMessageProcessing();
        String username = message.getFrom().getUserName() != null ? message.getFrom().getUserName()
                : message.getFrom().getFirstName() + " " + message.getFrom().getLastName();
        statisticsServiceImpl.recordNews("Пользователь " + username + " начал общение с ботом.");
    }

}
