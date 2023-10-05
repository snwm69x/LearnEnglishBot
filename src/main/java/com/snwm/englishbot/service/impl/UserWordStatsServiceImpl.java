package com.snwm.englishbot.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.snwm.englishbot.entity.User;
import com.snwm.englishbot.entity.UserWordStats;
import com.snwm.englishbot.entity.UserWordStatsId;
import com.snwm.englishbot.entity.Word;
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
        if(!userWordStatsRepository.findByUserAndWord(user, word).isPresent()){
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
        UserWordStats lasttry = userWordStatsRepository.findByUserAndWord(user, word).get();
        lasttry.setLastAttempt(LocalDateTime.now());
        if(isCorrect){
            lasttry.setCorrectAttempts(lasttry.getCorrectAttempts() + 1);
        }
        if(!isCorrect){
            lasttry.setIncorrectAttempts(lasttry.getIncorrectAttempts() + 1);
        }
        userWordStatsRepository.save(lasttry);
        }
    }
    
}
