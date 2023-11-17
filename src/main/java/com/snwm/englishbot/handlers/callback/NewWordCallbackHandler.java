package com.snwm.englishbot.handlers.callback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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
import com.snwm.englishbot.service.impl.AdminControllerServiceImpl;

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
    private AdminControllerServiceImpl adminControllerServiceImpl;

    @Override
    public void handle(Update update, EnglishWordBot bot) {
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
            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            List<InlineKeyboardButton> row = new ArrayList<>();
            List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setCallbackData("btn");
            button.setText("Правильно");
            EditMessageText editMessageText = new EditMessageText();
            editMessageText.setChatId(update.getCallbackQuery().getMessage().getChatId().toString());
            editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
            editMessageText.setText("<b>" + word.getWord() + "</b> " + word.getTranscription() + " - "
                    + word.getTranslation().toString());
            editMessageText.enableHtml(true);
            editMessageText.setReplyMarkup(markup);
            row.add(button);
            keyboard.add(row);
            markup.setKeyboard(keyboard);
            AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
            answerCallbackQuery.setCallbackQueryId(update.getCallbackQuery().getId());
            answerCallbackQuery.setShowAlert(false);
            answerCallbackQuery.setText("Ваш рейтинг: " + user.getRating().toString());
            answerCallbackQuery.setCacheTime(2);

            try {
                bot.execute(editMessageText);
                bot.execute(answerCallbackQuery);
                adminControllerServiceImpl
                        .recordNews("Пользователь: " + update.getCallbackQuery().getFrom().getUserName()
                                + " ответил правильно на слово: " + word.getWord());
            } catch (TelegramApiException e) {
                adminControllerServiceImpl.setErrors(adminControllerServiceImpl.getErrors() + 1);
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
                    adminControllerServiceImpl.setErrors(adminControllerServiceImpl.getErrors() + 1);
                    throw new IllegalArgumentException("Unexpected value: " + user.getWordLevel());
            }
            userWordStatsService.updateWordStats(user, word, false);
            EditMessageText editMessageText = new EditMessageText();
            editMessageText.setChatId(update.getCallbackQuery().getMessage().getChatId().toString());
            editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
            editMessageText.enableHtml(true);
            editMessageText.setText("<b>" + word.getWord() + "</b> " + word.getTranscription() + " - "
                    + word.getTranslation().toString());
            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
            List<InlineKeyboardButton> row = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText("Неверно");
            button.setCallbackData("btn");
            row.add(button);
            keyboard.add(row);
            markup.setKeyboard(keyboard);
            editMessageText.setReplyMarkup(markup);
            AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
            answerCallbackQuery.setCallbackQueryId(update.getCallbackQuery().getId());
            answerCallbackQuery.setShowAlert(false);
            answerCallbackQuery.setText("Ваш рейтинг: " + user.getRating().toString());
            answerCallbackQuery.setCacheTime(2);
            try {
                bot.execute(editMessageText);
                bot.execute(answerCallbackQuery);
                adminControllerServiceImpl
                        .recordNews("Пользователь: " + update.getCallbackQuery().getFrom().getUserName()
                                + " ответил неправильно на слово: " + word.getWord());
            } catch (TelegramApiException e) {
                adminControllerServiceImpl.setErrors(adminControllerServiceImpl.getErrors() + 1);
                logger.error("Error while editing message reply markup: {}", e.getMessage());
            }
        }
        userService.saveUser(user);
        adminControllerServiceImpl.setHandledCallbacks(adminControllerServiceImpl.getHandledCallbacks() + 1);
        lastWords.add(wordId);
        userLastWordMap.put(userId, lastWords);
    }

}
