package com.snwm.englishbot.handlers.message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.snwm.englishbot.bot.EnglishWordBot;
import com.snwm.englishbot.component.KeyboardMaker;
import com.snwm.englishbot.entity.User;
import com.snwm.englishbot.entity.Word;
import com.snwm.englishbot.entity.enums.WordLevel;
import com.snwm.englishbot.handlers.MessageHandler;
import com.snwm.englishbot.service.UserService;
import com.snwm.englishbot.service.WordService;
import com.snwm.englishbot.service.impl.AdminControllerServiceImpl;

@Component("Новое слово 💭")
public class NewWordMessageHandler implements MessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(NewWordMessageHandler.class);
    private final Random random = new Random();

    @Autowired
    private AdminControllerServiceImpl adminControllerServiceImpl;
    @Autowired
    private UserService userService;
    @Autowired
    private KeyboardMaker keyboardMaker;
    @Autowired
    private WordService wordService;

    @Override
    public void handle(Message message, EnglishWordBot bot) {

        Long userChatId = message.getChatId();
        adminControllerServiceImpl.startMessageProcessing();
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
                    .build();
            sendMessage.setReplyMarkup(keyboardMaker.getDifficultLevelKeyboard());
            try {
                bot.execute(sendMessage);
                return;
            } catch (TelegramApiException e) {
                adminControllerServiceImpl.setErrors(adminControllerServiceImpl.getErrors() + 1);
                e.printStackTrace();
            }
        }
        if (user.getWords().isEmpty()) {
            wordService.set30WordsToUser(userChatId, user.getWordLevel());
        }
        if (random.nextBoolean()) {
            findTranslation(userChatId, bot);
        } else {
            findWordByTranslation(userChatId, bot);
        }
    }

    private void findTranslation(Long userChatId, EnglishWordBot bot) {
        Word word = wordService.getRandomWordByUserChatIdAndDeleteIt(userChatId);
        List<Word> words = wordService.getAllWordsByTypeAndLevel(word.getWordType(), word.getWordLevel());
        List<Word> options = new ArrayList<>();
        while (options.size() != 3) {
            int randomIndex = (int) (Math.random() * words.size());
            Word randomWord = words.get(randomIndex);
            if (!randomWord.equals(word) && !options.contains(randomWord)) {
                options.add(randomWord);
                words.remove(randomIndex);
            }
        }
        options.add(word);
        Collections.shuffle(options);
        SendMessage newWordMessage = new SendMessage();
        newWordMessage.disableNotification();
        newWordMessage.enableHtml(true);
        newWordMessage.setChatId(userChatId.toString());
        newWordMessage.setText("<b>" + word.getWord() + "</b> " + word.getTranscription());
        InlineKeyboardMarkup inlineKeyboardMarkup = keyboardMaker.getNewWordKeyboard(word, options,
                false);
        newWordMessage.setReplyMarkup(inlineKeyboardMarkup);
        try {
            bot.execute(newWordMessage);
        } catch (TelegramApiException e) {
            adminControllerServiceImpl.setErrors(adminControllerServiceImpl.getErrors() + 1);
            System.out.println(
                    "Ошибка во время обработки/отправки сообщения в команде NewWord методе findTranslation. Word: "
                            + word.getWord()
                            + " Options: " + options.toString());
            e.printStackTrace();
        }
        adminControllerServiceImpl.endMessageProcessing();
        adminControllerServiceImpl
                .recordNews(
                        "Пользователь " + userChatId.toString() + " запросил новое слово " + word.getWord());
    }

    private void findWordByTranslation(Long userChatId, EnglishWordBot bot) {
        Word word = wordService.getRandomWordByUserChatIdAndDeleteIt(userChatId);
        List<Word> words = wordService.getAllWordsByTypeAndLevel(word.getWordType(), word.getWordLevel());
        List<Word> options = new ArrayList<>();
        while (options.size() != 3) {
            int randomIndex = (int) (Math.random() * words.size());
            Word randomWord = words.get(randomIndex);
            if (!randomWord.equals(word) && !options.contains(randomWord)) {
                options.add(randomWord);
                words.remove(randomIndex);
            }
        }
        options.add(word);
        Collections.shuffle(options);
        SendMessage newWordMessage = new SendMessage();
        newWordMessage.disableNotification();
        newWordMessage.enableHtml(true);
        newWordMessage.setChatId(userChatId.toString());
        newWordMessage.setText(
                "<b>" + word.getTranslation().get((int) (Math.random() * word.getTranslation().size())) + "</b>");
        InlineKeyboardMarkup inlineKeyboardMarkup = keyboardMaker.getNewWordKeyboard(word, options, true);
        newWordMessage.setReplyMarkup(inlineKeyboardMarkup);
        try {
            bot.execute(newWordMessage);
        } catch (TelegramApiException e) {
            adminControllerServiceImpl.setErrors(adminControllerServiceImpl.getErrors() + 1);
            System.out.println("Error while sending message in method findWordByTranslation. Word: " + word.getWord()
                    + " Options: " + options.toString());
            e.printStackTrace();
        }
        adminControllerServiceImpl.endMessageProcessing();
        adminControllerServiceImpl
                .recordNews("Пользователь " + userChatId.toString() + " запросил новое слово "
                        + word.getTranslation().toString());
    }

}
