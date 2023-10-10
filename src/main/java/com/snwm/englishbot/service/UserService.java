package com.snwm.englishbot.service;

import com.snwm.englishbot.entity.User;

import java.util.List;

import org.telegram.telegrambots.meta.api.objects.Message;

public interface UserService {

    // Получение пользователя по ChatID
    User getUserByChatId(Long id);

    // Создание нового пользователя / регистрация
    void createNewUser(Message message);

    // Очистка слов пользователя
    void deleteUserWordsByChatId(Long id);

    // Сохранение User в базу данных
    void saveUser(User user);

    List<User> getAllUsers();
}
