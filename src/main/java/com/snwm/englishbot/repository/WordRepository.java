package com.snwm.englishbot.repository;

import com.snwm.englishbot.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface WordRepository extends JpaRepository<Word, Long> {
    @Query("select w.id from Word w where w.userData = ?#{id}")
    Set<Word> findAll(Long id);
}