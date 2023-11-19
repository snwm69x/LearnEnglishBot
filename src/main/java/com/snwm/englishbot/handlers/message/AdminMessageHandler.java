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
import com.snwm.englishbot.component.KeyboardMaker;
import com.snwm.englishbot.entity.User;
import com.snwm.englishbot.entity.enums.UserType;
import com.snwm.englishbot.handlers.MessageHandler;
import com.snwm.englishbot.service.UserService;
import com.snwm.englishbot.service.impl.StatisticsServiceImpl;

@Component("/admin")
public class AdminMessageHandler implements MessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(AdminMessageHandler.class);
    private static final String ADMIN_PAGE_URL = "http://95.181.173.115/admin";

    @Autowired
    private StatisticsServiceImpl statisticsServiceImpl;
    @Autowired
    private UserService userService;
    @Autowired
    private KeyboardMaker keyboardMaker;

    @Override
    public void handle(Message message, EnglishWordBot bot) {
        statisticsServiceImpl.startMessageProcessing();
        logger.info("Обработка команды /admin для пользователя: {}",
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
        if (user.getUserType().equals(UserType.ADMIN)) {
            SendMessage msg = SendMessage.builder()
                    .chatId(message.getChatId().toString())
                    .text("Admin Dashboard")
                    .build();
            msg.setReplyMarkup(keyboardMaker.getAdminPageButton(ADMIN_PAGE_URL));
            try {
                bot.execute(msg);
                statisticsServiceImpl.recordNews("Пользователь: " + message.getFrom().getUserName() + " с ID: "
                        + message.getChatId() + " запросил Admin Dashboard");
            } catch (TelegramApiException e) {
                System.out.println("Ошибка во время обработки команды '/admin' для пользователя: "
                        + message.getFrom().getUserName());
                statisticsServiceImpl.recordNews("Ошибка во время обработки команды '/admin' для пользователя: "
                        + message.getFrom().getUserName());
                statisticsServiceImpl.setErrors(statisticsServiceImpl.getErrors() + 1);
                e.printStackTrace();
            }
        } else {
            SendMessage msg2 = SendMessage.builder()
                    .chatId(message.getChatId().toString())
                    .text("У вас нету прав Администратора")
                    .build();
            try {
                bot.execute(msg2);
                statisticsServiceImpl.recordNews("Пользователь: " + message.getFrom().getUserName() + " с ID: "
                        + message.getChatId() + " запросил Admin Dashboard без привилегий");
            } catch (TelegramApiException e) {
                statisticsServiceImpl.setErrors(statisticsServiceImpl.getErrors() + 1);
                System.out.println("Ошибка во время обработки команды '/admin' для пользователя: "
                        + message.getFrom().getUserName());
                statisticsServiceImpl.recordNews("Ошибка во время обработки команды '/admin' для пользователя: "
                        + message.getFrom().getUserName());
                e.printStackTrace();
            }
        }
        statisticsServiceImpl.endMessageProcessing();
    }

}
