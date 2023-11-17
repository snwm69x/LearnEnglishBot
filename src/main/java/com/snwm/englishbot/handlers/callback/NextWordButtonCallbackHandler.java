package com.snwm.englishbot.handlers.callback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.snwm.englishbot.bot.EnglishWordBot;
import com.snwm.englishbot.handlers.CallbackHandler;
import com.snwm.englishbot.handlers.message.NewWordMessageHandler;
import com.snwm.englishbot.service.impl.AdminControllerServiceImpl;

@Component("btn")
public class NextWordButtonCallbackHandler implements CallbackHandler {

    private static final Logger logger = LoggerFactory.getLogger(NextWordButtonCallbackHandler.class);

    @Autowired
    private AdminControllerServiceImpl adminControllerServiceImpl;
    @Autowired
    private NewWordMessageHandler newWordMessageHandler;

    @Override
    public void handle(Update update, EnglishWordBot bot) {
        AnswerCallbackQuery answerCallbackQuery = AnswerCallbackQuery.builder()
                .callbackQueryId(update.getCallbackQuery().getId())
                .showAlert(false)
                .text("Следующее слово")
                .cacheTime(2)
                .build();
        try {
            bot.execute(answerCallbackQuery);
        } catch (TelegramApiException e) {
            adminControllerServiceImpl.setErrors(adminControllerServiceImpl.getErrors() + 1);
            logger.error("Error while sending answer callback query: {}", e.getMessage());
        }
        newWordMessageHandler.handle(update.getCallbackQuery().getMessage(), bot);
    }

}
