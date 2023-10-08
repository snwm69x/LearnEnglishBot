package com.snwm.englishbot.service.impl;

import java.time.LocalDateTime;

import com.snwm.englishbot.entity.*;
import org.springframework.stereotype.Service;

import com.snwm.englishbot.repository.UserWordStatsRepository;
import com.snwm.englishbot.service.UserWordStatsService;

@Service
public class UserWordStatsServiceImpl implements UserWordStatsService {

    private final UserWordStatsRepository userWordStatsRepository;

    public UserWordStatsServiceImpl(UserWordStatsRepository userWordStatsRepository) {
        this.userWordStatsRepository = userWordStatsRepository;
    }

    @Override
    public void updateWordStats(User user, Word word, boolean isCorrect) {
        if (userWordStatsRepository.findByUserAndWord(user, word).isEmpty()) {
            UserWordStats userWordStats = UserWordStats.builder()
                    .id(new UserWordStatsId(user.getChatId(), word.getId()))
                    .user(user)
                    .word(word)
                    .correctAttempts(isCorrect ? 1 : 0)
                    .incorrectAttempts(isCorrect ? 0 : 1)
                    .lastAttempt(LocalDateTime.now())
                    .build();
            userWordStatsRepository.save(userWordStats);
        } else {
            UserWordStats lastTry = userWordStatsRepository.findByUserAndWord(user, word).get();
            lastTry.setLastAttempt(LocalDateTime.now());
            if (isCorrect) {
                lastTry.setCorrectAttempts(lastTry.getCorrectAttempts() + 1);
            }
            if (!isCorrect) {
                lastTry.setIncorrectAttempts(lastTry.getIncorrectAttempts() + 1);
            }
            userWordStatsRepository.save(lastTry);
        }
    }

    @Override
    public String getSuccessRateForUser(Long userChatId) {
        int totalCorrectAttempts = userWordStatsRepository.getCorrectAttempt(userChatId);
        int totalAttempts = userWordStatsRepository.getAllAttempt(userChatId);
        return "Total Correct Attempts: " + totalCorrectAttempts +
                ", Total Attempts: " + totalAttempts + " percentage: " +
                (double)((totalCorrectAttempts * 100) / totalAttempts);
    }
}
