package com.snwm.englishbot.bot.snwm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.snwm.englishbot.bot.Snwm69xBot;
import com.snwm.englishbot.entity.User;
import com.snwm.englishbot.service.UserService;

@Service
public class Snwm69xLogService {

    private final String CHAT_ID;

    @Autowired
    private UserService userService;

    public Snwm69xLogService(@Value("${SNWM_BOT_ADMIN_CHANNEL}") String chatId) {
        this.CHAT_ID = chatId;
    }

    public void sendNewUserMessage(User user, Snwm69xBot snwm69xBot) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(CHAT_ID)
                .replyToMessageId(
                        69)
                .text("Новый пользователь: " + user.getUsername() + " First and Last name: " + user.getFirstName() + " "
                        + user.getLastName())
                .build();
        try {
            snwm69xBot.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void handleSetAdminRights(String username, Snwm69xBot snwm69xBot) {
        userService.setUserAdminRights(username);
        SendMessage sendMessage = SendMessage.builder()
                .chatId(CHAT_ID)
                .replyToMessageId(
                        69)
                .text("Пользователь " + username + " назначен администратором.")
                .build();
        try {
            snwm69xBot.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
