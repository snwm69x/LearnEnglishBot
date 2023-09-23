package com.snwm.englishbot.bot;

import java.util.ArrayList;
import java.util.List;

import com.snwm.englishbot.entity.User;
import com.snwm.englishbot.service.UserService;
import com.snwm.englishbot.service.WordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
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

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Component
public class EnglishWordBot extends TelegramLongPollingBot {
    private static final Logger logger = LoggerFactory.getLogger(EnglishWordBot.class);
    private final String token;
    private final String username;

    @Autowired
    private WordService wordService;
    @Autowired
    private UserService userService;


    EnglishWordBot(@Value("${bot.token}") String token, @Value("${bot.username}") String username, WordService wordService, UserService userService) {
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
                if (userService.getIdForChat(message.getChatId()) == null) {
                    User user = userService.saveUser(message);
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
                        "Список доступных команд:\n/start - начать работу с ботом\n/newword - получить новое слово\n/info - информация о боте\n\n // created by snwm //");
                try {
                    execute(helpMessage);
                } catch (TelegramApiException e) {
                    logger.error("Error while sending help message: {}", e.getMessage());
                }
            }

            //
            // Обработка команды "Новое слово"
            //
            if (message.getText().equals("Новое слово")) {
                User user = userService.getIdForChat(message.getChatId());
                //String word = userService.getRandomWord(user);
                SendMessage wordMessage = new SendMessage();
                wordMessage.setChatId(message.getChatId().toString());
                //wordMessage.setText("Слово: " + word + "\nТранскрипция: " + word.getTranscription());
                InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
                List<InlineKeyboardButton> row = new ArrayList<>();
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText("Показать перевод");
                //button.setCallbackData("translation:" + word.getTranslation());
                row.add(button);
                keyboard.add(row);
                markup.setKeyboard(keyboard);
                wordMessage.setReplyMarkup(markup);

                try {
                    execute(wordMessage);
                } catch (TelegramApiException e) {
                    logger.error("Error while sending word message: {}", e.getMessage());
                }

            }

        }

        //
        // Обработка нажатия на кнопку перевода слова.
        //
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String[] data = callbackQuery.getData().split(":");
            if (data[0].equals("translation")) {
                String translation = data[1];
                EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
                editMessageReplyMarkup.setChatId(callbackQuery.getMessage().getChatId().toString());
                editMessageReplyMarkup.setMessageId(callbackQuery.getMessage().getMessageId());
                InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
                List<InlineKeyboardButton> row = new ArrayList<>();
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(translation);
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

}
