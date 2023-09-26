package com.snwm.englishbot.bot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.snwm.englishbot.entity.Word;
import com.snwm.englishbot.service.UserService;
import com.snwm.englishbot.service.WordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class EnglishWordBot extends TelegramLongPollingBot {
    private static final Logger logger = LoggerFactory.getLogger(EnglishWordBot.class);
    private final String token;
    private final String username;
    private final Map<String, List<Word>> wordsCache = new HashMap<>();

    @Autowired
    private WordService wordService;
    @Autowired
    private UserService userService;

    EnglishWordBot(@Value("${bot.token}") String token, @Value("${bot.username}") String username) {
        this.token = token;
        this.username = username;
    }

    @PostConstruct
    public void start() {
        logger.info("username: {}, token: {}", username, token);
    }

    @PreDestroy
    public void destroy() {
        logger.info("username: {}, token: {} stopped working", username, token);
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public void onUpdateReceived(Update update) {
        // Обработка первого сообщения пользователя
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            if (message.getText().equals("/start")) {

                if (userService.findUserByChatId(message.getChatId()) == null) {
                    userService.createNewUser(message);
                    wordService.setAllWord(message.getChatId());
                }

                SendMessage startMessage = new SendMessage();
                startMessage.setChatId(message.getChatId().toString());
                startMessage.setText("Привет, я бот для изучения английского языка. Выбери действие:");
                // Создание клавиатуры
                ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
                markup.setResizeKeyboard(true);
                markup.setOneTimeKeyboard(false);
                List<KeyboardRow> keyboard = new ArrayList<>();
                // Добавление кнопок
                KeyboardRow row1 = new KeyboardRow();
                KeyboardButton button_wordplay = new KeyboardButton();
                button_wordplay.setText("Новое слово");
                row1.add(button_wordplay);
                KeyboardButton button_info = new KeyboardButton();
                button_info.setText("О боте");
                row1.add(button_info);
                keyboard.add(row1);
                markup.setKeyboard(keyboard);
                startMessage.setReplyMarkup(markup);
                try {
                    execute(startMessage);
                } catch (TelegramApiException e) {
                    logger.error("Error while sending start message: {}", e.getMessage());
                }
            }
            // Обработка команды "О боте"
            if (message.getText().equals("О боте")) {
                SendMessage helpMessage = new SendMessage();
                helpMessage.setChatId(message.getChatId().toString());
                helpMessage.setText(
                        "snwm");
                try {
                    execute(helpMessage);
                } catch (TelegramApiException e) {
                    logger.error("Error while sending help message: {}", e.getMessage());
                }
            }

            // Обработка команды "Новое слово"
            if (message.getText().equals("Новое слово")) {
                handleNewWordCommand(message);
            }
        }
    }

    private void handleNewWordCommand(Message message) {
        // Если кэш пуст, то заполняем его словами из базы данных.
        // но всё равно слова в ответах могут повторится
        List<Word> words = wordsCache.get(message.getChatId().toString());
        if(words == null) {
            words = wordService.getAllWordsInDb();
            wordsCache.put(message.getChatId().toString(), words);
        }
        Word word = wordService.getRandomWordByUserIdAndDeleteIt(message.getChatId());
        wordsCache.get(message.getChatId().toString()).remove(word);
        List<String> options = new ArrayList<>();
        List<Word> tempWords = wordsCache.get(message.getChatId().toString());
        for(int i = 0; i<3; i++) {
            int randomIndex = (int) (Math.random() * wordsCache.size());
            options.add(tempWords.get(randomIndex).getTranslation());
            tempWords.remove(randomIndex);
        }
        options.add(word.getTranslation());
        Collections.shuffle(options);
        int correctOptionId = options.indexOf(word.getTranslation());
        SendPoll sendPoll = new SendPoll();
        sendPoll.setType("quiz");
        sendPoll.setChatId(message.getChatId().toString());
        sendPoll.setQuestion("Слово: " + word.getWord() + "\nТранскрипция: " + word.getTranscription());
        sendPoll.setOptions(options);
        sendPoll.setCorrectOptionId(correctOptionId);

        try {
            execute(sendPoll);
        } catch (TelegramApiException e) {
            logger.error("Error while sending word message: {}", e.getMessage());
        }
    }

}
