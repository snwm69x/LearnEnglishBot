package com.snwm.englishbot.util;

import java.io.Serializable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.snwm.englishbot.entity.User;
import com.snwm.englishbot.entity.Word;
import com.snwm.englishbot.repository.UserRepository;
import com.snwm.englishbot.repository.WordRepository;
import com.snwm.englishbot.service.UserUtilService;

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
        if (words.isEmpty()) {
            this.words = new ArrayList<>(wordRepository.findAll());
        }
        int randomIndex = ThreadLocalRandom.current().nextInt(words.size());
        Word word = words.get(randomIndex);
        words.remove(randomIndex);
        UserUtil userUtil = UserUtil.builder()
                .user(user)
                .words(words)
                .build();
        if (userUtilService != null) {
            userUtilService.saveUserUtil(userUtil);
        }
        return word;
    }

    public void updateWords(List<Word> newWords) {
        this.words = new ArrayList<>(newWords);
        if (userUtilService != null) {
            userUtilService.saveUserUtil(this);
        }
    }
}
