package com.snwm.englishbot.component;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import com.snwm.englishbot.entity.Word;

@Component
public class KeyboardMaker {

    public ReplyKeyboardMarkup getMainKeyboard() {
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(false);
        List<KeyboardRow> keys = new ArrayList<>();
        // Добавление кнопок
        KeyboardRow row1 = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();
        KeyboardButton button_newWord = new KeyboardButton();
        // KeyboardButton button_statistics = new KeyboardButton();
        KeyboardButton button_chooseDifficult = new KeyboardButton();
        KeyboardButton button_rating = new KeyboardButton();
        button_newWord.setText("Новое слово 💭");
        // button_statistics.setText("Статистика 🔄");
        button_chooseDifficult.setText("Выбрать сложность ⚙️");
        button_rating.setText("Таблица лидеров 🏆");
        row1.add(button_newWord);
        // row1.add(button_statistics);
        row2.add(button_chooseDifficult);
        row1.add(button_rating);
        keys.add(row1);
        keys.add(row2);
        keyboard.setKeyboard(keys);
        return keyboard;
    }

    public InlineKeyboardMarkup getAdminPageButton(String url) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setUrl(url);
        button.setText("Open");
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(button);
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(row);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
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
        InlineKeyboardButton otherDictionariesButton = new InlineKeyboardButton();
        otherDictionariesButton.setText("Другие словари");
        otherDictionariesButton.setCallbackData("difficult:other");

        List<InlineKeyboardButton> row4 = new ArrayList<>();
        row4.add(otherDictionariesButton);

        keyboard.add(row4);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup getNewWordKeyboard(Word word, List<Word> options, boolean isTranslated) {
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        for (int i = 0; i < options.size(); i++) {
            Word currentWord = options.get(i);
            InlineKeyboardButton button = new InlineKeyboardButton();
            if (isTranslated) {
                button.setText(currentWord.getWord());
            } else {
                button.setText(
                        currentWord.getTranslation().get((int) (Math.random() * currentWord.getTranslation().size())));
            }
            button.setCallbackData("nw:" + word.getId() + ":" + currentWord.getId());

            if (i < 2) {
                row1.add(button);
            } else {
                row2.add(button);
            }
        }

        keyboard.add(row1);
        if (!row2.isEmpty()) {
            keyboard.add(row2);
        }

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup getSuccessPickedDifficultLevel() {
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText("Сложность выбрана");
        button1.setCallbackData("success");
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(button1);
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(row1);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup checkIfUserSubscribedToChannel() {
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText("Проверить подписку!");
        button1.setCallbackData("checksubscription");
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(button1);
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(row1);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
    }
}
