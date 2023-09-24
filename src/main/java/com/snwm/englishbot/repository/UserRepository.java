package com.snwm.englishbot.repository;

import com.snwm.englishbot.entity.Word;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.snwm.englishbot.entity.User;

import java.util.Optional;
import java.util.Set;


@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    User findByChatId(Long chatId);
//    @Modifying
//    @Query("update User u " +
//            "set u.wordList = ?1" +
//            " where u.id = ?2")
//    User updateUserByWordList(Long userId, Set<Word> words);

    Optional<User> findUserById(Long id);
}
