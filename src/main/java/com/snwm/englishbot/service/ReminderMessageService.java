package com.snwm.englishbot.service;

import java.util.List;

import com.snwm.englishbot.entity.ReminderMessage;

public interface ReminderMessageService {
    void addReminderMessage(String message);

    List<ReminderMessage> getAllReminderMessages();

    void setCurrentMessage(ReminderMessage message);

    ReminderMessage getCurrentMessage();

    ReminderMessage getReminderMessageById(Long id);

    void scheduledRemind();
}
