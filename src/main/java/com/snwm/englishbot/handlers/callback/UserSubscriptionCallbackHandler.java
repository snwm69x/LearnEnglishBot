package com.snwm.englishbot.handlers.callback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.snwm.englishbot.bot.EnglishWordBot;
import com.snwm.englishbot.entity.User;
import com.snwm.englishbot.entity.enums.UserType;
import com.snwm.englishbot.handlers.CallbackHandler;
import com.snwm.englishbot.service.PromotedChannelService;
import com.snwm.englishbot.service.UserService;
import com.snwm.englishbot.service.impl.StatisticsServiceImpl;

@Component("checksubscription")
public class UserSubscriptionCallbackHandler implements CallbackHandler {

        private static final Logger logger = LoggerFactory.getLogger(UserSubscriptionCallbackHandler.class);

        @Autowired
        private UserService userService;
        @Autowired
        private PromotedChannelService promotedChannelService;
        @Autowired
        private StatisticsServiceImpl statisticsServiceImpl;

        @Override
        public void handle(Update update, EnglishWordBot bot) {
                String username = update.getCallbackQuery().getFrom().getUserName() != null
                                ? update.getCallbackQuery().getFrom().getUserName()
                                : update.getCallbackQuery().getFrom().getFirstName() + " "
                                                + update.getCallbackQuery().getFrom().getLastName();
                logger.info("Обработка ответа пользователя для получения привилегий: {}",
                                update.getCallbackQuery().getFrom().getUserName());

                User user = userService.getUserByChatId(update.getCallbackQuery().getMessage().getChatId());
                GetChatMember getChatMember = GetChatMember.builder()
                                .chatId(promotedChannelService.getChannel().getChatId().toString())
                                .userId(update.getCallbackQuery().getFrom().getId())
                                .build();
                try {
                        ChatMember chatMember = bot.execute(getChatMember);
                        if (chatMember.getStatus().equals("left") && user.getUserType().equals(UserType.USER)) {
                                AnswerCallbackQuery answerCallbackQuery = AnswerCallbackQuery.builder()
                                                .callbackQueryId(update.getCallbackQuery().getId())
                                                .text("Вы не подписаны на канал "
                                                                + promotedChannelService.getChannel().getChannelLink())
                                                .cacheTime(2)
                                                .build();
                                bot.execute(answerCallbackQuery);
                        } else {
                                user.setUserType(UserType.PREMIUM);
                                userService.saveUser(user);
                                DeleteMessage deleteMessage = DeleteMessage.builder()
                                                .chatId(update.getCallbackQuery().getMessage().getChatId().toString())
                                                .messageId(update.getCallbackQuery().getMessage().getMessageId())
                                                .build();
                                AnswerCallbackQuery answerCallbackQuery = AnswerCallbackQuery.builder()
                                                .callbackQueryId(update.getCallbackQuery().getId())
                                                .text("Вы получили права PREMIUM")
                                                .cacheTime(2)
                                                .build();
                                bot.execute(deleteMessage);
                                bot.execute(answerCallbackQuery);
                        }
                } catch (TelegramApiException e) {
                        statisticsServiceImpl.setErrors(statisticsServiceImpl.getErrors() + 1);
                        e.printStackTrace();
                }

                statisticsServiceImpl.setHandledCallbacks(statisticsServiceImpl.getHandledCallbacks() + 1);
                statisticsServiceImpl
                                .recordNews(
                                                "Пользователь: " + username
                                                                + " подписался на канал");
        }

}
