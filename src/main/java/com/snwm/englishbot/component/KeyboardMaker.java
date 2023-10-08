package com.snwm.englishbot.component;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

@Component
public class KeyboardMaker {

    public ReplyKeyboardMarkup getMainKeyboard() {
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(false);
        List<KeyboardRow> keys= new ArrayList<>();
        // Добавление кнопок
        KeyboardRow row1 = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();
        KeyboardButton button_quiz = new KeyboardButton();
        button_quiz.setText("Quiz 📚");
        row1.add(button_quiz);
        KeyboardButton button_info = new KeyboardButton();
        button_info.setText("О боте 📝");
        row1.add(button_info);
        KeyboardButton button_wordplay = new KeyboardButton();
        KeyboardButton button_secondchance = new KeyboardButton();
        button_wordplay.setText("Новое слово 💬");
        button_secondchance.setText("Статистика 🔄");
        row2.add(button_wordplay);
        row2.add(button_secondchance);
        keys.add(row1);
        keys.add(row2);
        keyboard.setKeyboard(keys);
        return keyboard;
    }

    public InlineKeyboardMarkup getDifficultLevelKeyboard() {
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText("A1 - Elementary");
        button1.setCallbackData("difficult:A1");
        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setText("A2 - Pre-Intermediate");
        button2.setCallbackData("difficult:A2");
        InlineKeyboardButton button3 = new InlineKeyboardButton();
        button3.setText("B1 - Intermediate");
        button3.setCallbackData("difficult:B1");
        InlineKeyboardButton button4 = new InlineKeyboardButton();
        button4.setText("B2 - Upper-Intermediate");
        button4.setCallbackData("difficult:B2");
        InlineKeyboardButton button5 = new InlineKeyboardButton();
        button5.setText("C1 - Advanced");
        button5.setCallbackData("difficult:C1");
        InlineKeyboardButton button6 = new InlineKeyboardButton();
        button6.setText("C2 - Proficiency");
        button6.setCallbackData("difficult:C2");
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(button1);
        row1.add(button2);
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(button3);
        row2.add(button4);
        List<InlineKeyboardButton> row3 = new ArrayList<>();
        row3.add(button5);
        row3.add(button6);
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup getNewWordKeyboard(String correctAnswer, List<String> options, Long wordid) {
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText(options.get(0));
        button1.setCallbackData("newword:" + correctAnswer + ":" + options.get(0) + ":" + wordid);
        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setText(options.get(1));
        button2.setCallbackData("newword:" + correctAnswer + ":" + options.get(1) + ":" + wordid);
        InlineKeyboardButton button3 = new InlineKeyboardButton();
        button3.setText(options.get(2));
        button3.setCallbackData("newword:" + correctAnswer + ":" + options.get(2) + ":" + wordid);
        InlineKeyboardButton button4 = new InlineKeyboardButton();
        button4.setText(options.get(3));
        button4.setCallbackData("newword:" + correctAnswer + ":" + options.get(3) + ":" + wordid);
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(button1);
        row1.add(button2);
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(button3);
        row2.add(button4);
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(row1);
        keyboard.add(row2);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
    }
}
