package com.snwm.englishbot.bot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.snwm.englishbot.entity.User;
import com.snwm.englishbot.entity.Word;
import com.snwm.englishbot.service.UserService;
import com.snwm.englishbot.service.UserWordStatsService;
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
    @Autowired
    private UserWordStatsService userWordStatsService;

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
        // Обработка неизвестных команд
        if (update.hasMessage() && update.getMessage().hasText() && !update.getMessage().getText().equals("/start")
                && !update.getMessage().getText().equals("Новое слово 💬")
                && !update.getMessage().getText().equals("О боте 📝")
                && !update.getMessage().getText().equals("Quiz 📚")) {
            handleUnknownCommand(update.getMessage());
        }
        // Обработка команд
        if (update.hasMessage() && update.getMessage().hasText()) {
            // Обработка команды "/start"
            if (update.getMessage().getText().equals("/start")) {
                handleStartCommand(update.getMessage());
            }
            // Обработка команды "О боте"
            if (update.getMessage().getText().equals("О боте 📝")) {
                handleInfoCommand(update.getMessage());
            }
            // Обработка команды "Quiz"
            if (update.getMessage().getText().equals("Quiz 📚")) {
                handleQuizCommand(update.getMessage());
            }
            // Обработка команды "Новое слово"
            if (update.getMessage().getText().equals("Новое слово 💬")) {
                handleNewWordCommand(update.getMessage());
            }
        }

        if (update.hasCallbackQuery()) {
            // Обработка ответа на команду "Новое слово"
            if (update.getCallbackQuery().getData().startsWith("newword")) {
                handleNewWordCommandResponse(update.getCallbackQuery());
            }
        }
    }

    private void handleQuizCommand(Message message) {
        Word word = wordService.getRandomWordByUserChatIdAndDeleteIt(message.getChatId());
        List<Word> words = wordService.getAllWordsByUser(message.getChatId());
        List<String> options = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            int randomIndex = (int) (Math.random() * wordsCache.size());
            options.add(words.get(randomIndex).getTranslation());
            words.remove(randomIndex);
        }
        options.add(word.getTranslation());
        Collections.shuffle(options);
        int correctOptionId = options.indexOf(word.getTranslation());
        SendPoll sendPoll = new SendPoll();
        sendPoll.setType("quiz");
        sendPoll.setChatId(message.getChatId().toString());
        sendPoll.setQuestion("Слово: " + word.getWord() + "\nТранскрипция: " + word.getTranscription());
        sendPoll.setOptions(options);
        sendPoll.setIsAnonymous(false);
        sendPoll.setCorrectOptionId(correctOptionId);

        try {
            execute(sendPoll);
        } catch (TelegramApiException e) {
            logger.error("Error while sending word message: {}", e.getMessage());
        }
    }

    private void handleInfoCommand(Message message) {
        SendMessage infoMessage = new SendMessage();
        infoMessage.setChatId(message.getChatId().toString());
        infoMessage.setText("Бот для изучения английского языка. Версия 0.1 \n" +
                "Автор: - \n" +
                "GitHub: - \n" +
                "если бот не работает используйте /start");
        try {
            execute(infoMessage);
        } catch (TelegramApiException e) {
            logger.error("Error while sending info message: {}", e.getMessage());
        }
    }

    private void handleStartCommand(Message message) {
        User user = userService.findUserByChatId(message.getChatId());
        if (user == null) {
            userService.createNewUser(message);
            wordService.setAllWordToUser(message.getChatId());
        }
        if (user != null && user.getWords().size() == 0) {
            wordService.setAllWordToUser(message.getChatId());
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
        KeyboardRow row2 = new KeyboardRow();
        KeyboardButton button_quiz = new KeyboardButton();
        button_quiz.setText("Quiz 📚");
        row1.add(button_quiz);
        KeyboardButton button_info = new KeyboardButton();
        button_info.setText("О боте 📝");
        row1.add(button_info);
        KeyboardButton button_wordplay = new KeyboardButton();
        button_wordplay.setText("Новое слово 💬");
        row2.add(button_wordplay);
        keyboard.add(row1);
        keyboard.add(row2);
        markup.setKeyboard(keyboard);
        startMessage.setReplyMarkup(markup);
        try {
            execute(startMessage);
        } catch (TelegramApiException e) {
            logger.error("Error while sending start message: {}", e.getMessage());
        }
    }

    private void handleUnknownCommand(Message message) {
        SendMessage unknownMessage = new SendMessage();
        unknownMessage.setChatId(message.getChatId().toString());
        unknownMessage.setText("Неизвестная команда");
        try {
            execute(unknownMessage);
        } catch (TelegramApiException e) {
            logger.error("Error while sending unknown command message: {}", e.getMessage());
        }
    }

    private void handleNewWordCommand(Message message) {
        Word word = wordService.getRandomWordByUserChatIdAndDeleteIt(message.getChatId());
        List<Word> words = wordService.getAllWordsByUser(message.getChatId());
        List<String> options = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            int randomIndex = (int) (Math.random() * words.size());
            options.add(words.get(randomIndex).getTranslation());
            words.remove(randomIndex);
        }
        options.add(word.getTranslation());
        Collections.shuffle(options);
        String correctAnswer = options.get(options.indexOf(word.getTranslation()));
        SendMessage newWordMessage = new SendMessage();
        newWordMessage.setChatId(message.getChatId().toString());
        newWordMessage.setText("Слово: " + word.getWord() + "\nТранскрипция: " + word.getTranscription());
        // Создание клавиатуры
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText(options.get(0));
        button1.setCallbackData("newword:" + correctAnswer + ":" + options.get(0));
        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setText(options.get(1));
        button2.setCallbackData("newword:" + correctAnswer + ":" + options.get(1));
        InlineKeyboardButton button3 = new InlineKeyboardButton();
        button3.setText(options.get(2));
        button3.setCallbackData("newword:" + correctAnswer + ":" + options.get(2));
        InlineKeyboardButton button4 = new InlineKeyboardButton();
        button4.setText(options.get(3));
        button4.setCallbackData("newword:" + correctAnswer + ":" + options.get(3));
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(button1);
        row1.add(button2);
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(button3);
        row2.add(button4);
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(row1);
        keyboard.add(row2);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(keyboard);
        newWordMessage.setReplyMarkup(inlineKeyboardMarkup);
        try {
            execute(newWordMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void handleNewWordCommandResponse(CallbackQuery callbackQuery) {
        String[] data = callbackQuery.getData().split(":");
        User user = userService.findUserByChatId(callbackQuery.getMessage().getChatId());
        Word word = wordService.getWordByTranslation(data[1]);
        String correctAnswer = data[1];
        String userAnswer = data[2];
        System.out.println(correctAnswer);
        System.out.println(userAnswer);
        if (correctAnswer.equals(userAnswer)) {
            userWordStatsService.updateWordStats(user, word, true);
            EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
            editMessageReplyMarkup.setChatId(callbackQuery.getMessage().getChatId().toString());
            editMessageReplyMarkup.setMessageId(callbackQuery.getMessage().getMessageId());
            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
            List<InlineKeyboardButton> row = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText("Correct Answer");
            button.setCallbackData(callbackQuery.getData());
            row.add(button);
            keyboard.add(row);
            markup.setKeyboard(keyboard);
            editMessageReplyMarkup.setReplyMarkup(markup);
            try {
                execute(editMessageReplyMarkup);
            } catch (TelegramApiException e) {
                logger.error("Error while editing message reply markup: {}", e.getMessage());
            }
        } else {
            userWordStatsService.updateWordStats(user, word, false);
            EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
            editMessageReplyMarkup.setChatId(callbackQuery.getMessage().getChatId().toString());
            editMessageReplyMarkup.setMessageId(callbackQuery.getMessage().getMessageId());
            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
            List<InlineKeyboardButton> row = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText("Wrong Answer");
            button.setCallbackData(callbackQuery.getData());
            row.add(button);
            keyboard.add(row);
            markup.setKeyboard(keyboard);
            editMessageReplyMarkup.setReplyMarkup(markup);
            try {
                execute(editMessageReplyMarkup);
            } catch (TelegramApiException e) {
                logger.error("Error while editing message reply markup: {}", e.getMessage());
            }
        }
    }

}
