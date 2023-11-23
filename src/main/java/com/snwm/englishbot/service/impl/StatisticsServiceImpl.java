package com.snwm.englishbot.service.impl;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.snwm.englishbot.bot.EnglishWordBot;
import com.snwm.englishbot.service.StatisticsService;

import lombok.Getter;
import lombok.Setter;

@Service
@Getter
@Setter
public class StatisticsServiceImpl implements StatisticsService {

    private static final Long LOGS_CHAT_ID = -1002049685435L;
    private static final Integer topic = 68;
    private static final long startTime = System.currentTimeMillis();
    private int handledMessages = 0;
    private int handledCallbacks = 0;
    private int errors = 0;
    private LinkedList<String> recentNews = new LinkedList<>();
    private long totalResponseTime = 0;
    private long currentStartTime = 0;
    private int newUsers = 0;
    private long lastResponseTime = 0;

    @Autowired
    @Lazy
    private EnglishWordBot englishWordBot;

    @Override
    public String getUptime() {
        long uptimeMillis = System.currentTimeMillis() - startTime;
        long hours = TimeUnit.MILLISECONDS.toHours(uptimeMillis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(uptimeMillis) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(uptimeMillis) % 60;

        return String.format("%d hours, %d minutes, %d seconds", hours, minutes, seconds);
    }

    @Override
    public String getHandledMessages() {
        return String.valueOf(handledMessages);
    }

    @Override
    public void startMessageProcessing() {
        currentStartTime = System.currentTimeMillis();
    }

    @Override
    public void endMessageProcessing() {
        long responseTime = System.currentTimeMillis() - currentStartTime;
        totalResponseTime += responseTime;
        lastResponseTime = responseTime;
        handledMessages++;
    }

    public double getAverageResponseTime() {
        return (double) totalResponseTime / handledMessages / 1000;
    }

    @Override
    public void recordNews(String action) {
        if (recentNews.size() >= 25) {
            recentNews.removeFirst();
        }
        SendMessage sendMessage = SendMessage.builder()
                .chatId(LOGS_CHAT_ID.toString())
                .replyToMessageId(topic)
                .text(action)
                .build();
        try {
            englishWordBot.execute(sendMessage);
        } catch (TelegramApiException e) {
            System.out.println("get problemes :)");
            e.printStackTrace();
        }
        recentNews.add(action);
    }

}
