package com.snwm.englishbot.service;

public interface StatisticsService {
    String getHandledMessages();

    String getUptime();

    void startMessageProcessing();

    void endMessageProcessing();

    void recordNews(String action);
}
