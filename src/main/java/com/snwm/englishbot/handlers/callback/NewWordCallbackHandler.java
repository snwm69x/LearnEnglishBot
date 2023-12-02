package com.snwm.englishbot.handlers.callback;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.snwm.englishbot.bot.EnglishWordBot;
import com.snwm.englishbot.entity.User;
import com.snwm.englishbot.entity.Word;
import com.snwm.englishbot.handlers.CallbackHandler;
import com.snwm.englishbot.service.UserService;
import com.snwm.englishbot.service.UserWordStatsService;
import com.snwm.englishbot.service.WordService;
import com.snwm.englishbot.service.impl.StatisticsServiceImpl;

@Component("nw")
public class NewWordCallbackHandler implements CallbackHandler {

    private static final Logger logger = LoggerFactory.getLogger(NewWordCallbackHandler.class);
    private final Map<Long, LinkedList<Long>> userLastWordMap = new HashMap<>();

    @Autowired
    private WordService wordService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserWordStatsService userWordStatsService;
    @Autowired
    private StatisticsServiceImpl statisticsServiceImpl;

    @Transactional
    @Override
    public void handle(Update update, EnglishWordBot bot) {
        // for logs in tg
        var callbackQuery = update.getCallbackQuery();
        var from = callbackQuery.getFrom();

        String username = from.getUserName();
        if (username == null) {
            String firstName = from.getFirstName() != null ? from.getFirstName() : "";
            String lastName = from.getLastName() != null ? from.getLastName() : "";
            username = (firstName + " " + lastName).trim();
        }
        //
        Long userId = update.getCallbackQuery().getFrom().getId();
        Long wordId = Long.parseLong(update.getCallbackQuery().getData().split(":")[1]);
        LinkedList<Long> lastWords = userLastWordMap.getOrDefault(userId, new LinkedList<>());
        if (lastWords.contains(wordId)) {
            return;
        }
        if (lastWords.size() == 5) {
            lastWords.removeFirst();
        }
        logger.info("Обработка ответа пользователя: {} На слово: {}",
                update.getCallbackQuery().getFrom().getUserName(), wordService.getWordById(wordId).getWord());

        String[] data = update.getCallbackQuery().getData().split(":");
        User user = userService.getUserByChatId(update.getCallbackQuery().getMessage().getChatId());
        Word word = wordService.getWordById(Long.parseLong(data[1]));
        Long correctAnswer = Long.parseLong(data[1]);
        Long userAnswer = Long.parseLong(data[2]);
        if (correctAnswer.equals(userAnswer)) {
            switch (user.getWordLevel()) {
                case A1:
                    user.setRating(user.getRating() + 1);
                    break;
                case A2:
                    user.setRating(user.getRating() + 2);
                    break;
                case B1:
                    user.setRating(user.getRating() + 3);
                    break;
                case B2:
                    user.setRating(user.getRating() + 4);
                    break;
                case C1:
                    user.setRating(user.getRating() + 5);
                    break;
                case C2:
                    user.setRating(user.getRating() + 6);
                    break;
                default:
                    throw new IllegalArgumentException("Unexpected value: " + user.getWordLevel());
            }
            userWordStatsService.updateWordStats(user, word, true);
            InlineKeyboardButton button = InlineKeyboardButton.builder()
                    .text("Правильно")
                    .callbackData("btn")
                    .build();
            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            markup.setKeyboard(Arrays.asList(Collections.singletonList(button)));
            EditMessageText editMessageText = EditMessageText.builder()
                    .chatId(update.getCallbackQuery().getMessage().getChatId().toString())
                    .messageId(update.getCallbackQuery().getMessage().getMessageId())
                    .text("<b>" + word.getWord() + "</b> " + word.getTranscription() + " - "
                            + word.getTranslation().toString())
                    .replyMarkup(markup)
                    .build();
            editMessageText.enableHtml(true);
            AnswerCallbackQuery answerCallbackQuery = AnswerCallbackQuery.builder()
                    .callbackQueryId(update.getCallbackQuery().getId())
                    .showAlert(false)
                    .text("Ваш рейтинг: " + user.getRating().toString())
                    .cacheTime(2)
                    .build();
            try {
                bot.execute(editMessageText);
                bot.execute(answerCallbackQuery);
                statisticsServiceImpl
                        .recordNews("Пользователь: " + username
                                + " ответил правильно на слово: " + word.getWord());
            } catch (TelegramApiException e) {
                statisticsServiceImpl.setErrors(statisticsServiceImpl.getErrors() + 1);
                logger.error("Error while editing message reply markup: {}", e.getMessage());
            }
        } else {
            switch (user.getWordLevel()) {
                case A1:
                    user.setRating(user.getRating() - 1);
                    break;
                case A2:
                    user.setRating(user.getRating() - 2);
                    break;
                case B1:
                    user.setRating(user.getRating() - 3);
                    break;
                case B2:
                    user.setRating(user.getRating() - 4);
                    break;
                case C1:
                    user.setRating(user.getRating() - 5);
                    break;
                case C2:
                    user.setRating(user.getRating() - 6);
                    break;
                default:
                    statisticsServiceImpl.setErrors(statisticsServiceImpl.getErrors() + 1);
                    throw new IllegalArgumentException("Unexpected value: " + user.getWordLevel());
            }
            userWordStatsService.updateWordStats(user, word, false);
            InlineKeyboardButton button = InlineKeyboardButton.builder()
                    .text("Неверно")
                    .callbackData("btn")
                    .build();

            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            markup.setKeyboard(Arrays.asList(Collections.singletonList(button)));

            EditMessageText editMessageText = EditMessageText.builder()
                    .chatId(update.getCallbackQuery().getMessage().getChatId().toString())
                    .messageId(update.getCallbackQuery().getMessage().getMessageId())
                    .text("<b>" + word.getWord() + "</b> " + word.getTranscription() + " - "
                            + word.getTranslation().toString())
                    .replyMarkup(markup)
                    .build();
            editMessageText.enableHtml(true);

            AnswerCallbackQuery answerCallbackQuery = AnswerCallbackQuery.builder()
                    .callbackQueryId(update.getCallbackQuery().getId())
                    .showAlert(false)
                    .text("Ваш рейтинг: " + user.getRating().toString())
                    .cacheTime(2)
                    .build();
            try {
                bot.execute(editMessageText);
                bot.execute(answerCallbackQuery);
                statisticsServiceImpl
                        .recordNews("Пользователь: " + username
                                + " ответил неправильно на слово: " + word.getWord());
            } catch (TelegramApiException e) {
                statisticsServiceImpl.setErrors(statisticsServiceImpl.getErrors() + 1);
                logger.error("Error while editing message reply markup: {}", e.getMessage());
            }
        }
        userService.saveUser(user);
        statisticsServiceImpl.setHandledCallbacks(statisticsServiceImpl.getHandledCallbacks() + 1);
        lastWords.add(wordId);
        userLastWordMap.put(userId, lastWords);
    }

}
