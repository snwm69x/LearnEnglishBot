package com.snwm.englishbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.snwm.englishbot.entity.User;
import com.snwm.englishbot.entity.UserWordStats;
import com.snwm.englishbot.entity.UserWordStatsId;
import com.snwm.englishbot.entity.Word;

import java.util.List;
import java.util.Optional;


public interface UserWordStatsRepository extends JpaRepository<UserWordStats, UserWordStatsId> {
    List<UserWordStats> findByUser(User user);
    List<UserWordStats> findByWord(Word word);
    Optional<UserWordStats> findByUserAndWord(User user, Word word);
}
