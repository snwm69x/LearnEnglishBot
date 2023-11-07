package com.snwm.englishbot.service;

import com.snwm.englishbot.entity.Word;
import com.snwm.englishbot.entity.enums.WordLevel;
import com.snwm.englishbot.entity.enums.WordType;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WordService {

    // Получить все слова принадлежащие пользователю.
    List<Word> getAllWordsByUser(Long id);

    // Установить все слова пользователю.
    void setAllWordToUser(Long id, WordLevel wordLevel);

    public void set30WordsToUser(Long id, WordLevel wordLevel);

    // Получить случайное слово пользователя и удалить его из списка слов
    // пользователя.
    Word getRandomWordByUserChatIdAndDeleteIt(Long id);

    // Поиск слова по его ID
    Word getWordById(Long id);

    // Получение всех слов относящихся к WordType
    List<Word> getAllWordsByType(WordType wordType);

    // Получение всех слов по wordType и wordLevel
    List<Word> getAllWordsByTypeAndLevel(WordType wordType, WordLevel wordLevel);

    void addWord(Word word);

    Page<Word> getAllWords(Pageable pageable);

    Page<Word> findWord(String word, Pageable pageable);
}
