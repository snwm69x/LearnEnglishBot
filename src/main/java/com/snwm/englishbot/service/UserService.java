package com.snwm.englishbot.service;

import org.telegram.telegrambots.meta.api.objects.Message;

import com.snwm.englishbot.entity.User;

public interface UserService {
    // Получить пользователя по chatId.
    User findUserByChatId(Long id);

    // Создать нового пользователя.
    void createNewUser(Message message);
}
