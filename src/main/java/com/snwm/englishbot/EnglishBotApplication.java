package com.snwm.englishbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@SpringBootApplication
public class EnglishBotApplication {

	public static void main(String[] args) throws TelegramApiException {
		SpringApplication.run(EnglishBotApplication.class, args);
	}
}
