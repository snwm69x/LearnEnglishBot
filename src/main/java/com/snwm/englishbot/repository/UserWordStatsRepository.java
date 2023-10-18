package com.snwm.englishbot.repository;

import com.snwm.englishbot.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;



public interface UserWordStatsRepository extends JpaRepository<UserWordStats, UserWordStatsId> {

    Optional<UserWordStats> findByUserAndWord(User user, Word word);

    @Query(value = "SELECT sum(correct_attempts) " +
            "FROM user_word_stats " +
            "WHERE user_id = :userChatId",
            nativeQuery = true)
    int getCorrectAttempt(@Param("userChatId") Long userChatId);
    
    @Query(value = "SELECT sum(correct_attempts) + sum(incorrect_attempts) " +
            "FROM user_word_stats " +
            "WHERE user_id = :userChatId",
            nativeQuery = true)
    int getAllAttempt(@Param("userChatId") Long userChatId);

    List<UserWordStats> findByUser(User user);
}
