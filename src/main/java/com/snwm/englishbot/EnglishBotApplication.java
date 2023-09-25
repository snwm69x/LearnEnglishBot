package com.snwm.englishbot;

import com.snwm.englishbot.bot.EnglishWordBot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
public class EnglishBotApplication {

	public static void main(String[] args) throws TelegramApiException {
		SpringApplication.run(EnglishBotApplication.class, args);
	}
}
