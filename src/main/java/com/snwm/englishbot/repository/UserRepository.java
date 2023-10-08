package com.snwm.englishbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.snwm.englishbot.entity.User;

import javax.transaction.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByChatId(Long id);
    @Modifying
    @Transactional
    @Query(value = "DELETE from user_words where user_id = :user_id and word_id = :word_id", nativeQuery = true)
    void deleteWordById(@Param("user_id") Long userId, @Param("word_id") Long id);
}
