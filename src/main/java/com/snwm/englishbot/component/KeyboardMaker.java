package com.snwm.englishbot.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

        KeyboardButton button_newWord = new KeyboardButton("Новое слово 💭");
        KeyboardButton button_chooseDifficult = new KeyboardButton("Выбрать сложность ⚙️");
        KeyboardButton button_rating = new KeyboardButton("Таблица лидеров 🏆");

        KeyboardRow row1 = new KeyboardRow();
        row1.add(button_newWord);
        row1.add(button_rating);

        KeyboardRow row2 = new KeyboardRow();
        row2.add(button_chooseDifficult);

        List<KeyboardRow> keys = Arrays.asList(row1, row2);

        keyboard.setKeyboard(keys);
        return keyboard;
    }

    public InlineKeyboardMarkup getAdminPageButton(String url) {
        InlineKeyboardButton button = InlineKeyboardButton.builder()
                .url(url)
                .text("Open")
                .build();
        List<List<InlineKeyboardButton>> keyboard = Arrays.asList(
                Collections.singletonList(button));
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup getDifficultLevelKeyboard() {
        InlineKeyboardButton a1btn = InlineKeyboardButton.builder()
                .text("A1 - Elementary")
                .callbackData("difficult:A1")
                .build();
        InlineKeyboardButton a2btn = InlineKeyboardButton.builder()
                .text("A2 - Pre-Intermediate")
                .callbackData("difficult:A2")
                .build();
        InlineKeyboardButton b1btn = InlineKeyboardButton.builder()
                .text("B1 - Intermediate")
                .callbackData("difficult:B1")
                .build();
        InlineKeyboardButton b2btn = InlineKeyboardButton.builder()
                .text("B2 - Upper-Intermediate")
                .callbackData("difficult:B2")
                .build();
        InlineKeyboardButton c1btn = InlineKeyboardButton.builder()
                .text("C1 - Advanced")
                .callbackData("difficult:C1")
                .build();
        InlineKeyboardButton c2btn = InlineKeyboardButton.builder()
                .text("C2 - Proficiency")
                .callbackData("difficult:C2")
                .build();
        InlineKeyboardButton otherDict = InlineKeyboardButton.builder()
                .text("Другие словари")
                .callbackData("difficult:other")
                .build();
        List<List<InlineKeyboardButton>> keyboard = Arrays.asList(
                Arrays.asList(a1btn, a2btn),
                Arrays.asList(b1btn, b2btn),
                Arrays.asList(c1btn, c2btn),
                Arrays.asList(otherDict));

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
        InlineKeyboardButton button = InlineKeyboardButton.builder()
                .text("Сложность выбрана")
                .callbackData("success")
                .build();
        List<List<InlineKeyboardButton>> keyboard = Arrays.asList(
                Collections.singletonList(button));
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup checkIfUserSubscribedToChannel() {
        InlineKeyboardButton button = InlineKeyboardButton.builder()
                .text("Проверить подписку!")
                .callbackData("checksubscription")
                .build();
        List<List<InlineKeyboardButton>> keyboard = Arrays.asList(
                Collections.singletonList(button));
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
    }
}
