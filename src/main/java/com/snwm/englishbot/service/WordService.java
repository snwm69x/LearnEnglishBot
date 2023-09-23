package com.snwm.englishbot.service;

import com.snwm.englishbot.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WordService {
    @Autowired
    private WordRepository wordRepository;


}
