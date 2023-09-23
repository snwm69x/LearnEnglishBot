package com.snwm.englishbot;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import com.snwm.englishbot.bot.EnglishWordBot;

@SpringBootApplication
public class EnglishBotApplication {

	public static void main(String[] args) throws TelegramApiException {
		ApplicationContext context = SpringApplication.run(EnglishBotApplication.class, args);
		EnglishWordBot bot = context.getBean(EnglishWordBot.class);
		TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
		botsApi.registerBot(bot);
	}

}
