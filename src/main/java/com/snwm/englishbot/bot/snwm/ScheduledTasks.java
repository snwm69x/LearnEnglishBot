package com.snwm.englishbot.bot.snwm;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.snwm.englishbot.bot.snwm.dto.steamwebapiPlayerInfo.PlayerSummaryResponse;

@Component
@EnableScheduling
public class ScheduledTasks {

    private final Snwm69xSteamWebApiPlayerService playerService;
    private final Snwm69xDotaUserTrackService dotaUserTrackService;

    private PlayerSummaryResponse lastSummary;

    public ScheduledTasks(Snwm69xSteamWebApiPlayerService playerService,
            Snwm69xDotaUserTrackService dotaUserTrackService) {
        this.playerService = playerService;
        this.dotaUserTrackService = dotaUserTrackService;
    }

    // @Scheduled(fixedRate = 450000) // 1800000 milliseconds = 30 minutes
    @Scheduled(fixedRate = 50000)
    public void checkPlayerSummary() {
        PlayerSummaryResponse currentSummary = playerService.getPlayerSummary("76561198081248816");
        if (lastSummary != null && !lastSummary.equals(currentSummary)) {
            try {
                dotaUserTrackService.updateDotaInfoMessage(currentSummary);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

        lastSummary = currentSummary;
    }
}