package com.snwm.englishbot.service.impl;

import com.snwm.englishbot.entity.User;
import com.snwm.englishbot.entity.Word;
import com.snwm.englishbot.repository.UserRepository;
import com.snwm.englishbot.repository.WordRepository;
import com.snwm.englishbot.service.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
public class WordServiceImpl implements WordService {

    private final WordRepository wordRepository;
    private final UserRepository userRepository;

    @Autowired
    public WordServiceImpl(WordRepository wordRepository, UserRepository userRepository) {
        this.wordRepository = wordRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Word> getAllWordsByUser(Long id) {
//        User user = userRepository.findUserById(id).orElseThrow(EntityNotFoundException::new);
//        return user.getWords();
        return wordRepository.findWordsByUsers(id).orElseThrow(EntityNotFoundException::new);
    }
}
