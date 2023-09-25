package com.snwm.englishbot.service;

import com.snwm.englishbot.entity.User;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface UserService {
    User findUserByChatId(Long id);
    void createNewUser(Message message);
}
