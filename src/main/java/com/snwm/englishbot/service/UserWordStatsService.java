package com.snwm.englishbot.service;



import com.snwm.englishbot.entity.User;
import com.snwm.englishbot.entity.UserWordStats;
import com.snwm.englishbot.entity.Word;

public interface UserWordStatsService {
    
    // Для сохранения статистики
    void updateWordStats(User user, Word word, boolean isCorrect);

    // Для отображения статистики
    int getCorrectAttempt(Long userChatId);
    int getAllAttempt(Long userChatId);
    double getSuccessRate(Long wordId);
    UserWordStats getStatsByUser(User user);
}
