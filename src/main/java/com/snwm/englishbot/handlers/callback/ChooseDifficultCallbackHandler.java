package com.snwm.englishbot.handlers.callback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.snwm.englishbot.bot.EnglishWordBot;
import com.snwm.englishbot.component.KeyboardMaker;
import com.snwm.englishbot.entity.User;
import com.snwm.englishbot.entity.enums.UserType;
import com.snwm.englishbot.entity.enums.WordLevel;
import com.snwm.englishbot.handlers.CallbackHandler;
import com.snwm.englishbot.service.PromotedChannelService;
import com.snwm.englishbot.service.UserService;
import com.snwm.englishbot.service.WordService;
import com.snwm.englishbot.service.impl.StatisticsServiceImpl;

@Component("difficult")
public class ChooseDifficultCallbackHandler implements CallbackHandler {

    private static final Logger logger = LoggerFactory.getLogger(ChooseDifficultCallbackHandler.class);

    @Autowired
    private UserService userService;
    @Autowired
    private WordService wordService;
    @Autowired
    private PromotedChannelService promotedChannelService;
    @Autowired
    private StatisticsServiceImpl statisticsServiceImpl;
    @Autowired
    private KeyboardMaker keyboardMaker;

    @Transactional
    @Override
    public void handle(Update update, EnglishWordBot bot) {
        logger.info("Обработка ответа пользователя на команду 'Выбрать сложность': {}",
                update.getCallbackQuery().getFrom().getUserName());
        User user = userService.getUserByChatId(update.getCallbackQuery().getMessage().getChatId());
        if (!user.getWords().isEmpty()) {
            userService.deleteUserWordsByChatId(update.getCallbackQuery().getMessage().getChatId());
        }
        String[] data = update.getCallbackQuery().getData().split(":");
        if (data[1].equals("main")) {
            EditMessageText editMessageText = EditMessageText.builder()
                    .chatId(update.getCallbackQuery().getMessage().getChatId().toString())
                    .messageId(update.getCallbackQuery().getMessage().getMessageId())
                    .text("Выбери уровень сложности:")
                    .replyMarkup(keyboardMaker.getDifficultLevelKeyboard())
                    .build();
            try {
                bot.execute(editMessageText);
                return;
            } catch (TelegramApiException e) {
                logger.debug("Error when sending message in ChooseDifficultCallbackHandler: {}",
                        update.getCallbackQuery().getFrom().getUserName());
                statisticsServiceImpl.setErrors(statisticsServiceImpl.getErrors() + 1);
                e.printStackTrace();
            }
        }
        if (data[1].equals("dictionaries")) {
            EditMessageText otherDictionaries = EditMessageText.builder()
                    .chatId(update.getCallbackQuery().getMessage().getChatId().toString())
                    .messageId(update.getCallbackQuery().getMessage().getMessageId())
                    .text("Другие словари:")
                    .replyMarkup(keyboardMaker.getOtherDictionariesKeyboard())
                    .build();
            try {
                bot.execute(otherDictionaries);
                return;
            } catch (TelegramApiException e) {
                logger.debug("Error when sending message in ChooseDifficultCallbackHandler: {}",
                        update.getCallbackQuery().getFrom().getUserName());
                statisticsServiceImpl.setErrors(statisticsServiceImpl.getErrors() + 1);
                e.printStackTrace();
            }
        }
        AnswerCallbackQuery answerCallbackQueryWhenUserPickedDifficult = AnswerCallbackQuery.builder()
                .callbackQueryId(update.getCallbackQuery().getId())
                .text("Выбран уровень сложности: " + data[1])
                .cacheTime(2)
                .build();
        DeleteMessage deleteMessageWithDifficultLevels = DeleteMessage.builder()
                .chatId(update.getCallbackQuery().getMessage().getChatId().toString())
                .messageId(update.getCallbackQuery().getMessage().getMessageId())
                .build();
        WordLevel wordLevel = WordLevel.valueOf(data[1]);

        GetChatMember getChatMember = GetChatMember.builder()
                .chatId(promotedChannelService.getChannel().getChatId().toString())
                .userId(update.getCallbackQuery().getFrom().getId())
                .build();
        try {
            ChatMember chatMember = bot.execute(getChatMember);
            if (chatMember.getStatus().equals("left") && user.getUserType().equals(UserType.PREMIUM)) {
                user.setUserType(UserType.USER);
                userService.saveUser(user);
            }
        } catch (TelegramApiException e) {
            statisticsServiceImpl.setErrors(statisticsServiceImpl.getErrors() + 1);
            e.printStackTrace();
        }

        switch (data[1]) {
            case "A1":
            case "A2":
            case "B1":
                if (user.getUserType().equals(UserType.USER) || user.getUserType().equals(UserType.PREMIUM)
                        || user.getUserType().equals(UserType.ADMIN)) {
                    user.setWordLevel(wordLevel);
                    userService.saveUser(user);
                    wordService.setAllWordToUser(update.getCallbackQuery().getMessage().getChatId(), wordLevel);
                    try {
                        bot.execute(answerCallbackQueryWhenUserPickedDifficult);
                        bot.execute(deleteMessageWithDifficultLevels);
                    } catch (TelegramApiException e) {
                        logger.debug("Error when sending/deleting message in ChooseDifficultCallbackHandler: {}",
                                update.getCallbackQuery().getFrom().getUserName());
                        statisticsServiceImpl.setErrors(statisticsServiceImpl.getErrors() + 1);
                        e.printStackTrace();
                    }
                }
                break;
            case "B2":
            case "C1":
            case "C2":
                if (user.getUserType().equals(UserType.PREMIUM) || user.getUserType().equals(UserType.ADMIN)) {
                    user.setWordLevel(wordLevel);
                    userService.saveUser(user);
                    wordService.setAllWordToUser(update.getCallbackQuery().getMessage().getChatId(), wordLevel);
                    try {
                        bot.execute(answerCallbackQueryWhenUserPickedDifficult);
                        bot.execute(deleteMessageWithDifficultLevels);
                    } catch (TelegramApiException e) {
                        logger.debug("Error when sending/deleting message in ChooseDifficultCallbackHandler: {}",
                                update.getCallbackQuery().getFrom().getUserName());
                        statisticsServiceImpl.setErrors(statisticsServiceImpl.getErrors() + 1);
                        e.printStackTrace();
                    }
                } else {
                    try {
                        bot.execute(deleteMessageWithDifficultLevels);
                    } catch (TelegramApiException e) {
                        logger.debug("Error when deleting message in ChooseDifficultCallbackHandler: {}",
                                update.getCallbackQuery().getFrom().getUserName());
                        statisticsServiceImpl.setErrors(statisticsServiceImpl.getErrors() + 1);
                        e.printStackTrace();
                    }
                    SendMessage msg = SendMessage.builder()
                            .chatId(update.getCallbackQuery().getMessage().getChatId().toString())
                            .text("У вас нет доступа к этому уровню. \nЧтобы разблокировать доступ к сложным уровням, подпишитесь на канал "
                                    + promotedChannelService.getChannel().getChannelLink())
                            .build();
                    msg.setReplyMarkup(keyboardMaker.checkIfUserSubscribedToChannel());
                    try {
                        bot.execute(msg);
                    } catch (TelegramApiException e) {
                        logger.debug("Error when sending message in ChooseDifficultCallbackHandler: {}",
                                update.getCallbackQuery().getFrom().getUserName());
                        statisticsServiceImpl.setErrors(statisticsServiceImpl.getErrors() + 1);
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
        statisticsServiceImpl.setHandledCallbacks(statisticsServiceImpl.getHandledCallbacks() + 1);
        statisticsServiceImpl.recordNews(
                "Пользователь: " + update.getCallbackQuery().getFrom().getUserName() + " выбрал уровень сложности: "
                        + data[1]);
    }

}
