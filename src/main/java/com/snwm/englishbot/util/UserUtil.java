package com.snwm.englishbot.util;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.snwm.englishbot.entity.User;
import com.snwm.englishbot.entity.Word;
import com.snwm.englishbot.repository.UserRepository;
import com.snwm.englishbot.repository.WordRepository;
import com.snwm.englishbot.service.UserUtilService;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@Component
public class UserUtil implements Serializable {
    private static final long serialVersionUID = 2L;

    private User user;
    private List<Word> words;

    @Autowired
    private transient UserUtilService userUtilService;

    @Autowired
    private transient WordRepository wordRepository;

    @Autowired
    private transient UserRepository userRepository;

    public UserUtil(User user, List<Word> words) {
        this.user = user;
        this.words = new ArrayList<>(words);
    }

    public UserUtil() {
        // default constructor
    }

    // @PostConstruct
    // public void init() {
    //     if (!new File(
    //             "C:\\Users\\snwm1337\\Downloads\\englishbot\\englishbot\\src\\main\\java\\com\\snwm\\englishbot\\data\\user_util"
    //                     + File.separator + user.getChatId() + ".ser")
    //             .exists()) {
    //         UserUtil userUtil = UserUtil.builder()
    //                 .user(userRepository.findByChatId(user.getChatId()))
    //                 .words(new ArrayList<>(wordRepository.findAll()))
    //                 .build();
    //         userUtilService.saveUserUtil(userUtil);
    //     }
    // }

    public Word getRandomWord() {
        List<Word> words = this.words;
        if (words.isEmpty()) {
            this.words = new ArrayList<>(wordRepository.findAll());
        }
        if (!words.isEmpty()) {
            Word word = words.get((int) (Math.random() * words.size()));
            words.remove(word);
            UserUtil userUtil = UserUtil.builder()
                        .user(user)
                        .words(words)
                        .build();
            userUtilService.saveUserUtil(userUtil);
            return word;
        }
        return null;
    }

    public void updateWords(List<Word> newWords) {
        this.words = new ArrayList<>(newWords);
        userUtilService.saveUserUtil(this);
    }
}
