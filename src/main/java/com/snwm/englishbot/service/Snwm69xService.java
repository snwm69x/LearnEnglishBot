package com.snwm.englishbot.service;

import com.snwm.englishbot.bot.Snwm69xBot;
import com.snwm.englishbot.entity.User;

public interface Snwm69xService {
    void sendNewUserMessage(User user, Snwm69xBot snwm69xBot);

    void handleSetAdminRights(String username, Snwm69xBot snwm69xBot);

    void sendRecentAction(String message, Snwm69xBot snwm69xBot);
}
