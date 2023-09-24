package com.snwm.englishbot.service;

import com.snwm.englishbot.entity.Word;
import com.snwm.englishbot.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

public interface WordService {
    List<Word> getAllWordsByUser(Long id);
}
