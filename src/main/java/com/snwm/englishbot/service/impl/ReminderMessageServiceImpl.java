package com.snwm.englishbot.service.impl;

import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.snwm.englishbot.bot.EnglishWordBot;
import com.snwm.englishbot.entity.ReminderMessage;
import com.snwm.englishbot.entity.User;
import com.snwm.englishbot.repository.ReminderMessageRepository;
import com.snwm.englishbot.service.ReminderMessageService;
import com.snwm.englishbot.service.UserService;

@Service
@EnableScheduling
public class ReminderMessageServiceImpl implements ReminderMessageService {

    private ReminderMessage currentReminderMessage;

    @Autowired
    private ReminderMessageRepository reminderMessageRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private EnglishWordBot englishWordBot;

    @PostConstruct
    public void init() {
        Optional<ReminderMessage> reminderMessage = reminderMessageRepository.findRandom();
        if (reminderMessage.isPresent()) {
            this.currentReminderMessage = reminderMessage.get();
        } else {
            this.currentReminderMessage = new ReminderMessage();
        }
    }

    @Override
    public ReminderMessage getCurrentMessage() {
        return this.currentReminderMessage;
    }

    @Override
    public void setCurrentMessage(ReminderMessage message) {
        this.currentReminderMessage = message;
    }

    @Override
    public List<ReminderMessage> getAllReminderMessages() {
        return reminderMessageRepository.findAll();
    }

    @Override
    public void addReminderMessage(String message) {
        ReminderMessage reminderMessage = ReminderMessage.builder().message(message).build();
        reminderMessageRepository.save(reminderMessage);
    }

    @Override
    public ReminderMessage getReminderMessageById(Long id) {
        return reminderMessageRepository.findById(id).orElse(null);
    }

    @Scheduled(cron = "0 0 19 */3 * *")
    public void scheduledRemind() {
        List<User> users = userService.getAllUsers();
        for (User user : users) {
            SendMessage sendMessage = SendMessage.builder()
                    .chatId(user.getChatId().toString())
                    .text(currentReminderMessage.getMessage())
                    .build();
            try {
                englishWordBot.execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }

        }
    }
}
