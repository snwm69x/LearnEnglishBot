package com.snwm.englishbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.snwm.englishbot.entity.Word;

@Repository
public interface WordRepository extends JpaRepository<Word, Long> {
    
}
