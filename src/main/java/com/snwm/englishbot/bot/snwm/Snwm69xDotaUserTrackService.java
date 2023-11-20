package com.snwm.englishbot.bot.snwm;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import javax.print.DocFlavor.STRING;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.snwm.englishbot.bot.Snwm69xBot;
import com.snwm.englishbot.bot.snwm.dto.steamwebapiPlayerInfo.PlayerSummaryResponse;

public class Snwm69xDotaUserTrackService {

    @Autowired
    private Snwm69xBot snwm69xBot;

    private final Long CHAT_ID = -1002049685435L;

    public void updateDotaInfoMessage(PlayerSummaryResponse playerSummaryResponse) throws TelegramApiException {
        String avatarUrl = playerSummaryResponse.getResponse().getPlayers().get(0).getAvatarfull();
        long lastLogoffUnixTime = playerSummaryResponse.getResponse().getPlayers().get(0).getLastlogoff();
        String lastLogoffTime = unixTimeToHumanReadable(lastLogoffUnixTime);
        InputFile avatarFile;
        try {
            avatarFile = urlToInputFile(avatarUrl);
            SendPhoto sendPhoto = SendPhoto.builder()
                    .chatId(CHAT_ID.toString())
                    .parseMode("html")
                    .photo(avatarFile)
                    .caption("Егор Просвиркин aka " + "<b>"
                            + playerSummaryResponse.getResponse().getPlayers().get(0).getPersonaname() + "</b>" +
                            "\n" + "Последний раз был в сети: "
                            + lastLogoffTime)
                    .build();
            snwm69xBot.execute(sendPhoto);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public InputFile urlToInputFile(String url) throws IOException {
        URL imageUrl = new URL(url);
        InputStream in = new BufferedInputStream(imageUrl.openStream());
        byte[] imageBytes = IOUtils.toByteArray(in);
        in.close();

        return new InputFile(new ByteArrayInputStream(imageBytes), "filename");
    }

    public String unixTimeToHumanReadable(long unixTime) {
        Instant instant = Instant.ofEpochSecond(unixTime);
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        return zonedDateTime.format(formatter);
    }
}
