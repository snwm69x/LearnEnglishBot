package com.snwm.englishbot.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.snwm.englishbot.entity.Word;
import com.snwm.englishbot.entity.enums.WordLevel;
import com.snwm.englishbot.entity.enums.WordType;
import com.snwm.englishbot.service.WordService;

@Controller
@RequestMapping("/words")
public class WordsController {

    @Autowired
    private final WordService wordService;

    public WordsController(WordService wordService) {
        this.wordService = wordService;
    }

    @GetMapping
    public String getWordsPage() {
        return "words";
    }

    @PostMapping("/upload-file")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Failed to upload file. Please select a file and try again.");
            return "error";
        }

        try {
            // Получить содержимое файла в виде массива байтов
            byte[] bytes = file.getBytes();
            // Преобразовать содержимое файла в нужный формат
            String content = new String(bytes);
            // Разделить содержимое файла на строки
            String[] lines = content.split("\\r?\\n");
            // Пройтись по каждой строке и добавить слово в базу данных, если оно
            // соответствует маске
            for (String line : lines) {
                String[] parts = line.split("\\|");
                if (parts.length == 5) {
                    Word word = new Word();
                    String[] translations = parts[1].split(";");
                    word.setWord(parts[0]);
                    if (translations.length > 0) {
                        List<String> translationsList = new ArrayList<>();
                        for (String translation : translations) {
                            translationsList.add(translation);
                        }
                        word.setTranslation(translationsList);
                    } else {
                        word.setTranslation(Arrays.asList(translations));
                    }
                    word.setTranscription(parts[2]);
                    word.setWordLevel(WordLevel.valueOf(parts[3]));
                    word.setWordType(WordType.valueOf(parts[4]));
                    wordService.addWord(word);
                } else {
                    redirectAttributes.addFlashAttribute("error", "Failed to upload file. File format is invalid.");
                    return "error";
                }
            }

            redirectAttributes.addFlashAttribute("success", "File uploaded successfully.");

        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to upload file. Please try again.");
        }
        return "words";
    }

    @PostMapping("/add-word")
    public String addWord(
            @RequestParam("word") String word,
            @RequestParam("translation") String translation,
            @RequestParam("transcription") String transcription,
            @RequestParam("wordLevel") String wordLevel,
            @RequestParam("wordType") String wordType,
            RedirectAttributes redirectAttributes) {
        Word slovo = new Word();
        slovo.setWord(word);
        slovo.setTranslation(Arrays.asList(translation.split(";")));
        slovo.setTranscription(transcription);
        slovo.setWordLevel(WordLevel.valueOf(wordLevel));
        slovo.setWordType(WordType.valueOf(wordType));
        try {
            wordService.addWord(slovo);
            redirectAttributes.addFlashAttribute("message", "Word added successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to add word");
        }
        return "redirect:/words";
    }
}
