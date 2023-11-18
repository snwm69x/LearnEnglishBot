package com.snwm.englishbot.service.impl;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.snwm.englishbot.service.StatisticsService;

import lombok.Getter;
import lombok.Setter;

@Service
@Getter
@Setter
public class StatisticsServiceImpl implements StatisticsService {

    private static final long startTime = System.currentTimeMillis();
    private int handledMessages = 0;
    private int handledCallbacks = 0;
    private int errors = 0;
    private LinkedList<String> recentNews = new LinkedList<>();
    private long totalResponseTime = 0;
    private long currentStartTime = 0;
    private int newUsers = 0;
    private long lastResponseTime = 0;

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
        recentNews.add(action);
    }

}
