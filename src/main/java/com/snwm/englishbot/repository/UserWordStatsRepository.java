package com.snwm.englishbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
    
    @Query(value = "SELECT "+
    " SUM(correct_attempts) AS total_correct_attempts, " +
    "SUM(correct_attempts + incorrect_attempts) AS total_attempts " +
    "FROM " +
    "user_word_stats " + 
    "WHERE " + 
    "user_id = :userChatId",nativeQuery=true)
    Long[] getSuccessRateForUser(@Param("userChatId") Long userChatId);
}
