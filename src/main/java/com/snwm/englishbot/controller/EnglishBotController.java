package com.snwm.englishbot.controller;

import com.snwm.englishbot.entity.User;
import com.snwm.englishbot.entity.Word;
import com.snwm.englishbot.service.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EnglishBotController {

    private final WordService wordService;

    @Autowired
    public EnglishBotController(WordService wordService) {
        this.wordService = wordService;
    }

    @GetMapping("/getAllWords/{userId}")
    public List<Word> getAllWordsByUser(@PathVariable(value = "userId") Long id) {
        return wordService.getAllWordsByUser(id);
    }
}
