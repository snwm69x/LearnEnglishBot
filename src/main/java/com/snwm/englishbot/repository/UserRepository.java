package com.snwm.englishbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.snwm.englishbot.entity.User;

import java.util.Optional;
import java.util.Set;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByChatId(Long chatId);
//    @Modifying
//    @Query("update User u " +
//            "set u.wordList = ?1" +
//            " where u.id = ?2")
//    User updateUserByWordList(Long userId, Set<Word> words);

    Optional<User> findUserById(Long id);
}
