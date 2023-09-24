package com.snwm.englishbot.service.impl;

import com.snwm.englishbot.entity.User;
import com.snwm.englishbot.repository.UserRepository;
import com.snwm.englishbot.repository.WordRepository;
import com.snwm.englishbot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
public class UserServiceImpl implements UserService {

    private final WordRepository wordRepository;
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(WordRepository wordRepository, UserRepository userRepository) {
        this.wordRepository = wordRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void createNewUser(Message message) {
        User user = User.builder()
                .chatId(message.getChatId())
                .username(message.getFrom().getUserName())
                .firstName(message.getFrom().getFirstName())
                .lastName(message.getFrom().getLastName())
                .build();
        userRepository.save(user);
    }

    @Override
    public User findUserByChatId(Long id) {
        return userRepository.findUserByChatId(id);
    }
}
