package com.snwm.englishbot.service;

import com.snwm.englishbot.entity.Word;
import java.util.List;

public interface WordService {
    // Получить все слова принадлежащие пользователю.
    List<Word> getAllWordsByUser(Long id);

    // Получить все слова из базы данных.
    List<Word> getAllWordsInDb();

    // Установить все слова пользователю.
    void setAllWordToUser(Long id);

    // Получить случайное слово пользователя и удалить его из списка слов пользователя.
    Word getRandomWordByUserChatIdAndDeleteIt(Long id);

    // Получить случайное слово, которое не совпадает с правильным ответом.
    Word getRandomWord(Word correctWord);

    Word getWordByTranslation(String translation);

    Word getWordById(Long id);
}
