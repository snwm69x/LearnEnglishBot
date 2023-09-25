package com.snwm.englishbot;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
//import com.snwm.englishbot.bot.EnglishWordBot;

@SpringBootApplication
public class EnglishBotApplication {

	public static void main(String[] args) throws TelegramApiException {
		SpringApplication.run(EnglishBotApplication.class, args);
		// EnglishWordBot bot = context.getBean(EnglishWordBot.class);
		// TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
		// botsApi.registerBot(bot);
	}

}
