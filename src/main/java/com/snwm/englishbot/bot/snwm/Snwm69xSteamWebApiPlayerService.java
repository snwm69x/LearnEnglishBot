package com.snwm.englishbot.bot.snwm;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.snwm.englishbot.bot.snwm.dto.steamwebapiPlayerInfo.PlayerSummaryResponse;

@Service
public class Snwm69xSteamWebApiPlayerService {

    private static final String API_URL = "https://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key=87327A6DBC9E3FE03B1A543172572C5B&steamids=";

    public PlayerSummaryResponse getPlayerSummary(String steamId) {
        RestTemplate restTemplate = new RestTemplate();
        PlayerSummaryResponse result = restTemplate.getForObject(API_URL + steamId, PlayerSummaryResponse.class);
        return result;
    }
}
