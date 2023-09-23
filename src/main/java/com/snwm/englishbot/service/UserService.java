package com.snwm.englishbot.service;

import com.snwm.englishbot.entity.User;
import com.snwm.englishbot.repository.UserRepository;
import com.snwm.englishbot.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WordRepository wordRepository;

    public User saveUser(Message message) {
        User user = User.builder()
                .chatId(message.getChatId())
                .username(message.getFrom().getUserName())
                .firstName(message.getFrom().getFirstName())
                .lastName(message.getFrom().getLastName())
                .build();
        userRepository.save(user);
        return user;
    }

    public User getIdForChat(long id) {
        return userRepository.findByChatId(id);
    }
}
