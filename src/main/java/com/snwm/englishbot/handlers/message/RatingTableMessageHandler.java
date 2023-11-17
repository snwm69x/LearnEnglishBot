package com.snwm.englishbot.handlers.message;

import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.snwm.englishbot.bot.EnglishWordBot;
import com.snwm.englishbot.entity.User;
import com.snwm.englishbot.handlers.MessageHandler;
import com.snwm.englishbot.service.UserService;
import com.snwm.englishbot.service.impl.AdminControllerServiceImpl;

@Component("Таблица лидеров 🏆")
public class RatingTableMessageHandler implements MessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(RatingTableMessageHandler.class);

    @Autowired
    private AdminControllerServiceImpl adminControllerServiceImpl;
    @Autowired
    private UserService userService;

    @Override
    public void handle(Message message, EnglishWordBot bot) {
        adminControllerServiceImpl.startMessageProcessing();
        logger.info("Пользователь {} запросил Таблицу лидеров", message.getFrom().getUserName());
        SendChatAction sendChatAction = SendChatAction.builder()
                .chatId(message.getChatId().toString())
                .action("typing")
                .build();
        try {
            bot.execute(sendChatAction);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        boolean top10 = false;
        User user = userService.getUserByChatId(message.getChatId());
        List<User> users = userService.getAllUsers();
        users.sort(Comparator.comparing(User::getRating).reversed());
        if (users.indexOf(user) < 10) {
            top10 = true;
        }
        StringBuilder text = new StringBuilder("<b>Таблица лидеров:</b>\n\n");
        for (int i = 0; i < Math.min(users.size(), 10); i++) {
            User usr = users.get(i);
            if (i == 0) {
                text.append("👑 @");
            } else if (i == 1) {
                text.append("🥈 @");
            } else if (i == 2) {
                text.append("🥉 @");
            } else {
                text.append(i + 1).append(". @");
            }
            text.append(usr.getUsername()).append(" - ").append(usr.getRating()).append(" pts\n");
        }
        if (top10) {
            text.append("\nПоздравляем, вы входите в 🔝10 лидеров!🎉");
        } else {
            text.append("\n" + "Ваш рейтинг: ").append(user.getRating());
        }
        SendMessage msg = SendMessage.builder()
                .chatId(message.getChatId().toString())
                .text(text.toString())
                .parseMode(ParseMode.HTML)
                .build();
        msg.disableNotification();
        try {
            bot.execute(msg);
            adminControllerServiceImpl.recordNews("Пользователь: " + message.getFrom().getUserName() + " с ID: "
                    + message.getChatId() + " запросил Таблицу лидеров");
        } catch (TelegramApiException e) {
            System.out.println("Ошибка во время обработки команды 'Таблица лидеров' для пользователя: "
                    + message.getFrom().getUserName());
            adminControllerServiceImpl.setErrors(adminControllerServiceImpl.getErrors() + 1);
            e.printStackTrace();
        }
        adminControllerServiceImpl.endMessageProcessing();
    }

}
