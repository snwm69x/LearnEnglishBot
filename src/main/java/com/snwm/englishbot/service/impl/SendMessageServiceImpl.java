// package com.snwm.englishbot.service.impl;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;
// import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
// import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

// import com.snwm.englishbot.bot.EnglishWordBot;
// import com.snwm.englishbot.service.SendMessageService;

// @Service
// public class SendMessageServiceImpl implements SendMessageService {

//     @Autowired
//     private EnglishWordBot englishWordBot;

//     public SendMessageServiceImpl(EnglishWordBot englishWordBot) {
//         this.englishWordBot = englishWordBot;
//     }
    
//     @Override
//     public void sendMessage(String chatId, String message) {
//         SendMessage msg = SendMessage.builder()
//                         .chatId(chatId)
//                         .text(message)
//                         .build();
//             try {
//                 englishWordBot.execute(msg);
//             } catch (TelegramApiException e) {
//                 e.printStackTrace();
//             }
//     }
// }
