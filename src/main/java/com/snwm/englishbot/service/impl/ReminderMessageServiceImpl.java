package com.snwm.englishbot.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.snwm.englishbot.entity.ReminderMessage;
import com.snwm.englishbot.repository.ReminderMessageRepository;
import com.snwm.englishbot.service.ReminderMessageService;

@Service
public class ReminderMessageServiceImpl implements ReminderMessageService {

    private ReminderMessage currentReminderMessage;

    @Autowired
    private ReminderMessageRepository reminderMessageRepository;

    public ReminderMessageServiceImpl() {
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
}
