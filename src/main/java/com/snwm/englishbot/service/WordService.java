package com.snwm.englishbot.service;

import com.snwm.englishbot.entity.Word;
import com.snwm.englishbot.entity.enums.WordLevel;

import java.util.List;

public interface WordService {
    // Получить все слова принадлежащие пользователю.
    List<Word> getAllWordsByUser(Long id);

    // Установить все слова пользователю.
    void setAllWordToUser(Long id, WordLevel wordLevel);

    // Получить случайное слово пользователя и удалить его из списка слов пользователя.
    Word getRandomWordByUserChatIdAndDeleteIt(Long id);

    Word getWordById(Long id);
}
