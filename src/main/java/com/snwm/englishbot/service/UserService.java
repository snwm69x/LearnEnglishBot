package com.snwm.englishbot.service;

import org.telegram.telegrambots.meta.api.objects.Message;

import com.snwm.englishbot.entity.User;

public interface UserService {
    User findUserByChatId(Long id);
    void createNewUser(Message message);
}
