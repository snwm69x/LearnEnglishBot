package com.snwm.englishbot.bot.snwm;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.snwm.englishbot.bot.snwm.dto.steamwebapiPlayerInfo.PlayerSummaryResponse;

@Component
public class ScheduledTasks {

    private final Snwm69xSteamWebApiPlayerService playerService;
    private final Snwm69xDotaUserTrackService dotaUserTrackService;

    private PlayerSummaryResponse lastSummary;

    public ScheduledTasks(Snwm69xSteamWebApiPlayerService playerService,
            Snwm69xDotaUserTrackService dotaUserTrackService) {
        this.playerService = playerService;
        this.dotaUserTrackService = dotaUserTrackService;
    }

    @Scheduled(fixedRate = 1800000) // 1800000 milliseconds = 30 minutes
    public void checkPlayerSummary() {
        PlayerSummaryResponse currentSummary = playerService.getPlayerSummary("76561198081248816"); // replace with
                                                                                                    // actual steamId

        if (lastSummary != null && !lastSummary.equals(currentSummary)) {
            dotaUserTrackService.updateDotaInfoMessage(currentSummary);
        }

        lastSummary = currentSummary;
    }
}
