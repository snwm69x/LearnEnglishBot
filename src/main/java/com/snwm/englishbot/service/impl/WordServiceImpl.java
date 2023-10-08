package com.snwm.englishbot.service.impl;

import com.snwm.englishbot.entity.User;
import com.snwm.englishbot.entity.Word;
import com.snwm.englishbot.entity.enums.WordLevel;
import com.snwm.englishbot.repository.UserRepository;
import com.snwm.englishbot.repository.WordRepository;
import com.snwm.englishbot.service.WordService;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class WordServiceImpl implements WordService {

    private final WordRepository wordRepository;
    private final UserRepository userRepository;

    public WordServiceImpl(WordRepository wordRepository, UserRepository userRepository) {
        this.wordRepository = wordRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Word> getAllWordsByUser(Long id) {
        return wordRepository.findWordsByUser(id).orElseThrow(EntityNotFoundException::new);
    }


    @Override
    public void setAllWordToUser(Long id, WordLevel wordLevel) {
        User user = userRepository.findUserByChatId(id);
        user.setWords(wordRepository.findByWordLevel(wordLevel));
        userRepository.save(user);
    }

    @Override
    public Word getRandomWordByUserChatIdAndDeleteIt(Long id) {
        User user = userRepository.findUserByChatId(id);
        List<Word> words = user.getWords();
        if (words.isEmpty()) {
            words = wordRepository.findAll();
        }
        int randomIndex = (int) (Math.random() * words.size());
        Word word = words.get(randomIndex);
        words.remove(randomIndex);
        user.setWords(words);
        userRepository.deleteWordById(user.getChatId(), (long) randomIndex);
        return word;
    }

    @Override
    public Word getWordById(Long id) {
        Optional<Word> optionalWord = wordRepository.findById(id);
        if (optionalWord.isPresent()) {
            return optionalWord.get();
        } else {
            throw new RuntimeException("Word not found");
        }
    }
}
