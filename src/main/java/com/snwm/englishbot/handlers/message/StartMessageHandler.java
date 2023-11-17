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
import com.snwm.englishbot.service.impl.AdminControllerServiceImpl;

@Component("/start")
public class StartMessageHandler implements MessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(StartMessageHandler.class);

    @Autowired
    private AdminControllerServiceImpl adminControllerServiceImpl;
    @Autowired
    private UserService userService;
    @Autowired
    private KeyboardMaker keyboardMaker;

    @Transactional
    @Override
    public void handle(Message message, EnglishWordBot bot) {
        adminControllerServiceImpl.startMessageProcessing();
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
            adminControllerServiceImpl.setNewUsers(adminControllerServiceImpl.getNewUsers() + 1);
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
            adminControllerServiceImpl.setErrors(adminControllerServiceImpl.getErrors() + 1);
            logger.error("Error while sending start message: {}", e.getMessage());
        }
        adminControllerServiceImpl.endMessageProcessing();
        adminControllerServiceImpl
                .recordNews("Новый пользователь: " + message.getFrom().getUserName() + " начал использовать бота");
    }

}
