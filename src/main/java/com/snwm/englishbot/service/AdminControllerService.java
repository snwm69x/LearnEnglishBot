package com.snwm.englishbot.service;

public interface AdminControllerService {
    String getHandledMessages();

    String getUptime();

    void startMessageProcessing();

    void endMessageProcessing();

    void recordNews(String action);
}
