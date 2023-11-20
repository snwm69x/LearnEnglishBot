package com.snwm.englishbot.handlers.message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.snwm.englishbot.bot.EnglishWordBot;
import com.snwm.englishbot.component.KeyboardMaker;
import com.snwm.englishbot.entity.User;
import com.snwm.englishbot.entity.Word;
import com.snwm.englishbot.entity.enums.WordLevel;
import com.snwm.englishbot.handlers.MessageHandler;
import com.snwm.englishbot.service.UserService;
import com.snwm.englishbot.service.WordService;
import com.snwm.englishbot.service.impl.StatisticsServiceImpl;

@Component("Новое слово 💭")
public class NewWordMessageHandler implements MessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(NewWordMessageHandler.class);
    private final Random random = new Random();

    @Autowired
    private StatisticsServiceImpl statisticsServiceImpl;
    @Autowired
    private UserService userService;
    @Autowired
    private KeyboardMaker keyboardMaker;
    @Autowired
    private WordService wordService;

    @Transactional
    @Override
    public void handle(Message message, EnglishWordBot bot) {

        Long userChatId = message.getChatId();
        statisticsServiceImpl.startMessageProcessing();
        logger.info("Обработка команды 'Новое слово' для пользователя: {}",
                message.getFrom().getUserName());

        SendChatAction sendChatAction = SendChatAction.builder()
                .chatId(userChatId.toString())
                .action("typing")
                .build();
        try {
            bot.execute(sendChatAction);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        User user = userService.getUserByChatId(userChatId);
        // Если у пользователя не выбрана сложность, предлагает ее выбрать
        if (user.getWordLevel().equals(WordLevel.NONE)) {
            SendMessage sendMessage = SendMessage.builder()
                    .chatId(userChatId.toString())
                    .text("У вас не выбрана сложность.")
                    .replyMarkup(keyboardMaker.getDifficultLevelKeyboard())
                    .build();
            try {
                bot.execute(sendMessage);
                return;
            } catch (TelegramApiException e) {
                statisticsServiceImpl.setErrors(statisticsServiceImpl.getErrors() + 1);
                e.printStackTrace();
            }
        }
        if (random.nextBoolean()) {
            findTranslation(userChatId, bot);
        } else {
            findWordByTranslation(userChatId, bot);
        }
    }

    @Transactional
    protected void findTranslation(Long userChatId, EnglishWordBot bot) {
        Word word = wordService.getRandomWordByUserChatIdAndDeleteIt(userChatId);
        System.out.println("Загаданное слово: " + word.getWord() + "| id: " + word.getId() + "| translate: "
                + word.getTranslation().toString());
        List<Word> words = wordService.getAllWordsByTypeAndLevel(word.getWordType(), word.getWordLevel());
        Set<Word> uniqueWords = new HashSet<>(words);
        if (uniqueWords.size() < words.size()) {
            System.out.println("Список words содержит дубликаты");
        } else {
            System.out.println("Список words не содержит дубликаты");
        }
        words = new ArrayList<>(new HashSet<>(words));
        words.remove(word);
        Collections.shuffle(words);
        Set<Word> optionsSet = new LinkedHashSet<>();
        optionsSet.add(word);
        System.out.println("В optionsSet добавлено слово: " + word.getWord() + "| id: " + word.getId() + "| translate: "
                + word.getTranslation().toString());
        for (Word randomWord : words) {
            if (optionsSet.size() < 4) {
                optionsSet.add(randomWord);
                System.out.println("В optionsSet добавлено слово: " + randomWord.getWord() + "| id: "
                        + randomWord.getId() + "| translate: " + randomWord.getTranslation().toString());
            }
        }
        List<Word> options = new ArrayList<>(optionsSet);
        Collections.shuffle(options);
        SendMessage newWordMessage = SendMessage.builder()
                .disableNotification(true)
                .chatId(userChatId.toString())
                .text("<b>" + word.getWord() + "</b> " + word.getTranscription())
                .replyMarkup(keyboardMaker.getNewWordKeyboard(word, options, false))
                .build();
        newWordMessage.enableHtml(true);
        try {
            bot.execute(newWordMessage);
        } catch (TelegramApiException e) {
            statisticsServiceImpl.setErrors(statisticsServiceImpl.getErrors() + 1);
            System.out.println(
                    "Ошибка во время обработки/отправки сообщения в команде NewWord методе findTranslation. Word: "
                            + word.getWord()
                            + " Options: " + optionsSet.toString());
            e.printStackTrace();
        }
        statisticsServiceImpl.endMessageProcessing();
        statisticsServiceImpl
                .recordNews(
                        "Пользователь " + userChatId.toString() + " запросил новое слово " + word.getWord());
    }

    @Transactional
    protected void findWordByTranslation(Long userChatId, EnglishWordBot bot) {
        Word word = wordService.getRandomWordByUserChatIdAndDeleteIt(userChatId);
        System.out.println("Загаданное слово: " + word.getWord() + "| id: " + word.getId() + "| translate: "
                + word.getTranslation().toString());
        List<Word> words = wordService.getAllWordsByTypeAndLevel(word.getWordType(), word.getWordLevel());
        Set<Word> uniqueWords = new HashSet<>(words);
        if (uniqueWords.size() < words.size()) {
            System.out.println("Список words содержит дубликаты");
        } else {
            System.out.println("Список words не содержит дубликаты");
        }
        words = new ArrayList<>(new HashSet<>(words));
        words.remove(word);
        Collections.shuffle(words);
        Set<Word> optionsSet = new LinkedHashSet<>();
        optionsSet.add(word);
        System.out.println("В optionsSet добавлено слово: " + word.getWord() + "| id: " + word.getId() + "| translate: "
                + word.getTranslation().toString());
        for (Word randomWord : words) {
            if (optionsSet.size() < 4) {
                optionsSet.add(randomWord);
                System.out.println("В optionsSet добавлено слово: " + randomWord.getWord() + "| id: "
                        + randomWord.getId() + "| translate: " + randomWord.getTranslation().toString());
            }
        }
        List<Word> options = new ArrayList<>(optionsSet);
        Collections.shuffle(options);
        SendMessage newWordMessage = SendMessage.builder()
                .disableNotification(true)
                .chatId(userChatId.toString())
                .text("<b>" + word.getTranslation().get((int) (Math.random() * word.getTranslation().size())) + "</b>")
                .replyMarkup(keyboardMaker.getNewWordKeyboard(word, options, true))
                .build();
        newWordMessage.enableHtml(true);
        try {
            bot.execute(newWordMessage);
        } catch (TelegramApiException e) {
            statisticsServiceImpl.setErrors(statisticsServiceImpl.getErrors() + 1);
            System.out.println("Error while sending message in method findWordByTranslation. Word: " + word.getWord()
                    + " Options: " + options.toString());
            e.printStackTrace();
        }
        statisticsServiceImpl.endMessageProcessing();
        statisticsServiceImpl
                .recordNews("Пользователь " + userChatId.toString() + " запросил новое слово "
                        + word.getTranslation().toString());
    }

}
