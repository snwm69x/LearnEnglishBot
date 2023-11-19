package com.snwm.englishbot.service.impl;

import com.snwm.englishbot.entity.User;
import com.snwm.englishbot.entity.Word;
import com.snwm.englishbot.entity.enums.WordLevel;
import com.snwm.englishbot.entity.enums.WordType;
import com.snwm.englishbot.repository.UserRepository;
import com.snwm.englishbot.repository.WordRepository;
import com.snwm.englishbot.service.WordService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    @Override
    public void setAllWordToUser(Long id, WordLevel wordLevel) {
        User user = userRepository.findUserByChatId(id);
        user.setWords(wordRepository.findByWordLevel(wordLevel));
        userRepository.save(user);
    }

    @Transactional
    @Override
    public void set30WordsToUser(Long id, WordLevel wordLevel) {
        User user = userRepository.findUserByChatId(id);
        user.setWords(wordRepository.find30RandomWordsByWordLevel(wordLevel));
        userRepository.save(user);
    }

    @Transactional
    @Override
    public Word getRandomWordByUserChatIdAndDeleteIt(Long id) {
        User user = userRepository.findUserByChatId(id);
        if (user.getWords().isEmpty()) {
            user.setWords(wordRepository.findByWordLevel(user.getWordLevel()));
        }
        List<Word> words = user.getWords();
        int randomIndex = (int) (Math.random() * words.size());
        Word word = words.get(randomIndex);
        userRepository.deleteWordById(user.getChatId(), word.getId());
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

    @Override
    public List<Word> getAllWordsByType(WordType wordType) {
        return wordRepository.findByWordType(wordType);
    }

    @Override
    public List<Word> getAllWordsByTypeAndLevel(WordType wordType, WordLevel wordLevel) {
        return wordRepository.findByWordTypeAndWordLevel(wordType, wordLevel);
    }

    @Override
    public void addWord(Word word) {
        wordRepository.save(word);
    }

    @Override
    public Page<Word> getAllWords(Pageable pageable) {
        return wordRepository.findAll(pageable);
    }

    @Override
    public Page<Word> findWord(String word, Pageable pageable) {
        return wordRepository.findByWord(word, pageable);
    }
}
