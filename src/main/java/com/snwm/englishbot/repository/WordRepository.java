package com.snwm.englishbot.repository;

import com.snwm.englishbot.entity.Word;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface WordRepository extends JpaRepository<Word, Long> {
    @Query(value = "select w from words w where w.word_list is null", nativeQuery = true)
    Set<Word> findAll(Long id);

    @Modifying
    @Query("INSERT INTO words w (w.transcription, w.translation, w.word, w.word_list) " +
            "values (:translation, :transcription, :word, :wordId)")
    @Transactional
    void setStart(@Param("word_list") Long wordId,
                  @Param("translation") String translation,
                  @Param("transcription") String transcription,
                  @Param("word") String word);
    
    List<Word> findAll();
}