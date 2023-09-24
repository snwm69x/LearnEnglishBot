package com.snwm.englishbot.repository;

import com.snwm.englishbot.entity.User;
import com.snwm.englishbot.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface WordRepository extends JpaRepository<Word, Long> {
//    @Query("select w.id from Word w where w.userData = ?#{id}")
//    Set<Word> findAll(Long id);

    @Query(value = "select z.id, z.transcription, z.translation, z.word from (words as w " +
            "join user_words as uw on w.id=uw.word_id and uw.user_id = :userId) as z",
            nativeQuery = true)
    Optional<List<Word>> findWordsByUsers(@Param("userId") Long userId);

//    Optional<List<Word>> findWordsByUsers(User user);
}