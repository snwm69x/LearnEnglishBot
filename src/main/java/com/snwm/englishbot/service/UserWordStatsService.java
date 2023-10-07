package com.snwm.englishbot.service;

import com.snwm.englishbot.entity.User;
import com.snwm.englishbot.entity.Word;

public interface UserWordStatsService {
    void updateWordStats(User user, Word word, boolean isCorrect);

    String getSuccessRateForUser(Long userChatId);
}
