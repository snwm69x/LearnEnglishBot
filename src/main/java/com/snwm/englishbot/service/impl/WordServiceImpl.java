package com.snwm.englishbot.service.impl;

import com.snwm.englishbot.entity.User;
import com.snwm.englishbot.entity.Word;
import com.snwm.englishbot.repository.UserRepository;
import com.snwm.englishbot.repository.WordRepository;
import com.snwm.englishbot.service.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

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
        return wordRepository.findWordsByUser(id).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public List<Word> getAllWordsInDb() {
        return wordRepository.findAll();
    }

    @Override
    public void setAllWord(Long id) {
        User user = userRepository.findUserByChatId(id);
        user.setWords(wordRepository.findAll());
        userRepository.save(user);
    }

    @Override
    public Word getRandomWordByUserChatIdAndDeleteIt(Long id) {
        User user = userRepository.findUserByChatId(id);
        List<Word> words = user.getWords();
        if(words.isEmpty()) {
            words = wordRepository.findAll();
        }
        int randomIndex = (int) (Math.random() * words.size());
        Word word = words.get(randomIndex);
        words.remove(randomIndex);
        user.setWords(words);
        userRepository.deleteWordById(user.getChatId(), (long)randomIndex);
        return word;
    }
}
