package com.snwm.englishbot.service;

import com.snwm.englishbot.entity.Word;

import java.util.List;

public interface WordService {
    List<Word> getAllWordsByUser(Long id);
    List<Word> getAllWordsInDb();

    void setAllWord(Long id);
}
