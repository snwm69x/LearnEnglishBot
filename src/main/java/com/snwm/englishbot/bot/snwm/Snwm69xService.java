package com.snwm.englishbot.bot.snwm;

import com.snwm.englishbot.bot.Snwm69xBot;
import com.snwm.englishbot.entity.User;

public interface Snwm69xService {
    void sendNewUserMessage(User user, Snwm69xBot snwm69xBot);

    void handleSetAdminRights(String username, Snwm69xBot snwm69xBot);

}
