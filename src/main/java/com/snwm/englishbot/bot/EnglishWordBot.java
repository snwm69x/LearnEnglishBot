package com.snwm.englishbot.bot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.snwm.englishbot.entity.User;
import com.snwm.englishbot.entity.Word;
import com.snwm.englishbot.component.KeyboardMaker;
import com.snwm.englishbot.entity.enums.UserType;
import com.snwm.englishbot.entity.enums.WordLevel;
import com.snwm.englishbot.service.UserService;
import com.snwm.englishbot.service.UserWordStatsService;
import com.snwm.englishbot.service.WordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class EnglishWordBot extends TelegramLongPollingBot {

    private static final Logger logger = LoggerFactory.getLogger(EnglishWordBot.class);
    private final String token;
    private final String username;
    private Random random = new Random();

    @Autowired
    private WordService wordService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserWordStatsService userWordStatsService;
    @Autowired
    private KeyboardMaker keyboardMaker;

    EnglishWordBot(@Value("6566742010:AAHYTvo8_s_CZ95VYzLiz2a6t51PaSiTycY") String token, @Value("@SykaTrydnoBot") String username) {
        this.token = token;
        this.username = username;
    }

    @PostConstruct
    public void start() {
        logger.info("username: {}, token: {}", username, token);
    }

    @PreDestroy
    public void destroy() {
        logger.info("username: {}, token: {} stopped working", username, token);
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    // @Scheduled(cron = "0 0 0 */3 * *")
    // public void sendMessageToAllUsers() {
    //     List<User> users = userService.getAllUsers();
    //     for (User user : users) {
    //         SendMessage sendMessage = SendMessage.builder()
    //                 .chatId(user.getChatId().toString())
    //                 .text("Давно не виделись, пора подтянуть английский!")
    //                 .build();
    //         try {
    //             execute(sendMessage);
    //         } catch (TelegramApiException e) {
    //             e.printStackTrace();
    //         }
    //     }
    // }

    @Override
    public void onUpdateReceived(Update update) {

        // Обработка неизвестных команд
        if (update.hasMessage() && update.getMessage().hasText()
                && !update.getMessage().getText().equals("/start")
                && !update.getMessage().getText().equals("Новое слово 💬")
                && !update.getMessage().getText().equals("Статистика 🔄")
                && !update.getMessage().getText().equals("Выбрать сложность 📊")) {
            handleUnknownCommand(update.getMessage());
        }

        // Обработка команд
        if (update.hasMessage() && update.getMessage().hasText()) {

            // Обработка команды "/start"
            if (update.getMessage().getText().equals("/start")) {
                handleStartCommand(update.getMessage());
            }

            // Обработка команды "Новое слово"
            if (update.getMessage().getText().equals("Новое слово 💬")) {
                handleNewWordCommand(update.getMessage());
            }

            // Обработка команды "Статистика 🔄"
            if (update.getMessage().getText().equals("Статистика 🔄")) {
                handleStatsCommand(update.getMessage());
            }

            // Обработка команды "Выбрать сложность 📊"
            if (update.getMessage().getText().equals("Выбрать сложность 📊")) {
                handleChooseDifficult(update.getMessage());
            }

            // if (update.getMessage().getText().equals("Рейтинг 🏆")) {
            //     handleRatingCommand(update.getMessage());
            // }
        }

        if (update.hasCallbackQuery()) {
            // Обработка ответа на команду "Новое слово"
            if (update.getCallbackQuery().getData().startsWith("newword")) {
                handleNewWordCommandResponse(update.getCallbackQuery());
            }
            // Обработка ответа на команду "Выбрать сложность"
            if (update.getCallbackQuery().getData().startsWith("difficult")) {
                try {
                    handleDifficultLevelCommand(update.getCallbackQuery());
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }

            if (update.getCallbackQuery().getData().equals("checksubscription")) {
                handleUserSubscriptionRespone(update.getCallbackQuery());
            }
        }
    }

    private void handleStartCommand(Message message) {
        User user = userService.getUserByChatId(message.getChatId());
        if (user == null) {
            userService.createNewUser(message);
        }
        SendMessage startMessage = new SendMessage();
        startMessage.setChatId(message.getChatId().toString());
        startMessage.setText("Привет, я бот для изучения английского языка. Выбери сложность:");
        InlineKeyboardMarkup keyboard = keyboardMaker.getDifficultLevelKeyboard();
        startMessage.setReplyMarkup(keyboard);
        try {
            execute(startMessage);
        } catch (TelegramApiException e) {
            logger.error("Error while sending start message: {}", e.getMessage());
        }
    }

    private void handleUnknownCommand(Message message) {
        SendMessage unknownMessage = new SendMessage();
        unknownMessage.setChatId(message.getChatId().toString());
        unknownMessage.setText("Неизвестная команда");
        try {
            execute(unknownMessage);
        } catch (TelegramApiException e) {
            logger.error("Error while sending unknown command message: {}", e.getMessage());
        }
    }

    private void handleNewWordCommand(Message message) {
        // Если у пользователя не выбрана сложность, предлагает ее выбрать
        if(userService.getUserByChatId(message.getChatId()).getWordLevel().equals(null)) {
            SendMessage sendMessage = SendMessage.builder()
                        .chatId(message.getChatId().toString())
                        .text("У вас не выбрана сложность.")
                        .build();
            sendMessage.setReplyMarkup(keyboardMaker.getDifficultLevelKeyboard());
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
                                
        }
        if(random.nextBoolean()){
            findTranslation(message);
        } else {
            findWordByTranslation(message);
        }
    }

    private void findTranslation(Message message) {
        Word word = wordService.getRandomWordByUserChatIdAndDeleteIt(message.getChatId());
        String word_translation = word.getTranslation().get((int) (Math.random() * word.getTranslation().size()));
        List<Word> words = wordService.getAllWordsByTypeAndLevel(word.getWordType(), word.getWordLevel());
        List<String> options = new ArrayList<>();
        while (options.size() != 3) {
            int randomIndex = (int) (Math.random() * words.size());
            if (!words.get(randomIndex).getWord().equals(word.getWord())) {
                options.add(words.get(randomIndex).getTranslation()
                        .get((int) (Math.random() * words.get(randomIndex).getTranslation().size())));
                words.remove(randomIndex);
            }
        }
        options.add(word_translation);
        Collections.shuffle(options);
        String correctAnswer = options.get(options.indexOf(word_translation));
        SendMessage newWordMessage = new SendMessage();
        newWordMessage.disableNotification();
        newWordMessage.enableHtml(true);
        newWordMessage.setChatId(message.getChatId().toString());
        newWordMessage.setText("<b>" + word.getWord() + "</b> " + word.getTranscription());
        InlineKeyboardMarkup inlineKeyboardMarkup = keyboardMaker.getNewWordKeyboard(correctAnswer, options,
                word.getId());
        newWordMessage.setReplyMarkup(inlineKeyboardMarkup);
        try {
            execute(newWordMessage);
        } catch (TelegramApiException e) {
            System.out.println("error while sending msg in method findTranslation");
            e.printStackTrace();
        }
    }

    private void findWordByTranslation(Message message) {
        Word word = wordService.getRandomWordByUserChatIdAndDeleteIt(message.getChatId());
        String word_name = word.getWord();
        List<Word> words = wordService.getAllWordsByTypeAndLevel(word.getWordType(), word.getWordLevel());
        List<String> options = new ArrayList<>();
        while (options.size() != 3) {
            int randomIndex = (int) (Math.random() * words.size());
            if (!words.get(randomIndex).getWord().equals(word_name)) {
                options.add(words.get(randomIndex).getWord());
                words.remove(randomIndex);
            }
        }
        options.add(word_name);
        Collections.shuffle(options);
        String correctAnswer = options.get(options.indexOf(word_name));
        SendMessage newWordMessage = new SendMessage();
        newWordMessage.disableNotification();
        newWordMessage.enableHtml(true);
        newWordMessage.setChatId(message.getChatId().toString());
        newWordMessage.setText("<b>" + word.getTranslation() + "</b>");
        InlineKeyboardMarkup inlineKeyboardMarkup = keyboardMaker.getNewWordKeyboard(correctAnswer, options,
                word.getId());
        newWordMessage.setReplyMarkup(inlineKeyboardMarkup);
        try {
            execute(newWordMessage);
        } catch (TelegramApiException e) {
            System.out.println("error while sending msg in method findWordByTranslation");
            e.printStackTrace();
        }
    }

    private void handleNewWordCommandResponse(CallbackQuery callbackQuery) {
        String[] data = callbackQuery.getData().split(":");
        User user = userService.getUserByChatId(callbackQuery.getMessage().getChatId());
        Word word = wordService.getWordById(Long.parseLong(data[3]));
        String correctAnswer = data[1];
        String userAnswer = data[2];
        if (correctAnswer.equals(userAnswer)) {
            userWordStatsService.updateWordStats(user, word, true);
            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            List<InlineKeyboardButton> row = new ArrayList<>();
            List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setCallbackData(callbackQuery.getData());
            button.setText("Correct!");
            EditMessageText editMessageText = new EditMessageText();
            editMessageText.setChatId(callbackQuery.getMessage().getChatId().toString());
            editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
            editMessageText.setText("<b>" + word.getWord() + "</b> " + word.getTranscription() + " - "
                    + word.getTranslation().toString());
            editMessageText.enableHtml(true);
            editMessageText.setReplyMarkup(markup);
            row.add(button);
            keyboard.add(row);
            markup.setKeyboard(keyboard);
            try {
                execute(editMessageText);
            } catch (TelegramApiException e) {
                logger.error("Error while editing message reply markup: {}", e.getMessage());
            }
        } else {
            userWordStatsService.updateWordStats(user, word, false);
            EditMessageText editMessageText = new EditMessageText();
            editMessageText.setChatId(callbackQuery.getMessage().getChatId().toString());
            editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
            editMessageText.enableHtml(true);
            editMessageText.setText("<b>" + word.getWord() + "</b> " + word.getTranscription() + " - "
                    + word.getTranslation().toString());
            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
            List<InlineKeyboardButton> row = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText("Wrong!");
            button.setCallbackData(callbackQuery.getData());
            row.add(button);
            keyboard.add(row);
            markup.setKeyboard(keyboard);
            editMessageText.setReplyMarkup(markup);
            try {
                execute(editMessageText);
            } catch (TelegramApiException e) {
                logger.error("Error while editing message reply markup: {}", e.getMessage());
            }
        }
    }

    private void handleStatsCommand(Message message) {
        SendMessage msg = SendMessage.builder()
                .chatId(message.getChatId().toString())
                .text("Статистика @" + message.getFrom().getUserName() + ":\n"
                        + "Всего попыток: " + userWordStatsService.getAllAttempt(message.getChatId()) + "\n"
                        + "Всего правильных ответов: " + userWordStatsService.getCorrectAttempt(message.getChatId())
                        + "\n"
                        + "Процент правильных ответов: " + userWordStatsService.getSuccessRate(message.getChatId())
                        + "%\n\n")
                .build();

        User user = userService.getUserByChatId(message.getChatId());
        GetChatMember getChatMember = new GetChatMember();
        getChatMember.setChatId("-1001672871308");
        getChatMember.setUserId(message.getFrom().getId());

        try {
            ChatMember chatMember = execute(getChatMember);
            if (chatMember.getStatus().equals("left") && user.getUserType().equals(UserType.PREMIUM)) {
                user.setUserType(UserType.USER);
                userService.saveUser(user);
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        if (user.getUserType().equals(UserType.USER)) {
            msg.setReplyMarkup(keyboardMaker.checkIfUserSubscribedToChannel());
        }
        try {
            execute(msg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void handleUserSubscriptionRespone(CallbackQuery callbackQuery) {
        User user = userService.getUserByChatId(callbackQuery.getMessage().getChatId());
        GetChatMember getChatMember = new GetChatMember();
        getChatMember.setChatId("-1001672871308");
        getChatMember.setUserId(callbackQuery.getFrom().getId());
        try {
            ChatMember chatMember = execute(getChatMember);
            if (chatMember.getStatus().equals("left") && user.getUserType().equals(UserType.USER)) {
                EditMessageText editMessageText = new EditMessageText();
                editMessageText.setChatId(callbackQuery.getMessage().getChatId().toString());
                editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
                editMessageText.setText("Вы не подписаны на канал @english_in_use_channel");
                editMessageText.setReplyMarkup(keyboardMaker.checkIfUserSubscribedToChannel());
                execute(editMessageText);
            } else {
                user.setUserType(UserType.PREMIUM);
                userService.saveUser(user);
                EditMessageText editMessageText = new EditMessageText();
                editMessageText.setChatId(callbackQuery.getMessage().getChatId().toString());
                editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
                editMessageText.setText("Вы получили права PREMIUM");
                execute(editMessageText);
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void handleDifficultLevelCommand(CallbackQuery callbackQuery) throws TelegramApiException {
        User user = userService.getUserByChatId(callbackQuery.getMessage().getChatId());
        if (!user.getWords().isEmpty()) {
            userService.deleteUserWordsByChatId(callbackQuery.getMessage().getChatId());
        }
        String[] data = callbackQuery.getData().split(":");
        SendMessage msg = SendMessage.builder()
                .chatId(callbackQuery.getMessage().getChatId().toString())
                .text("Выбран уровень сложности: " + data[1])
                .build();
        msg.setReplyMarkup(keyboardMaker.getMainKeyboard());
        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
        editMessageReplyMarkup.setChatId(callbackQuery.getMessage().getChatId().toString());
        editMessageReplyMarkup.setMessageId(callbackQuery.getMessage().getMessageId());
        editMessageReplyMarkup.setReplyMarkup(keyboardMaker.getSuccessPickedDifficultLevel());
        WordLevel wordLevel = WordLevel.valueOf(data[1]);
        switch (data[1]) {
            case "A1":
                if (user.getUserType().equals(UserType.USER) || user.getUserType().equals(UserType.PREMIUM)
                        || user.getUserType().equals(UserType.ADMIN)) {
                    user.setWordLevel(wordLevel);
                    userService.saveUser(user);
                    wordService.setAllWordToUser(callbackQuery.getMessage().getChatId(), wordLevel);
                    execute(msg);
                    execute(editMessageReplyMarkup);
                }
                break;
            case "A2":
                if (user.getUserType().equals(UserType.USER) || user.getUserType().equals(UserType.PREMIUM)
                        || user.getUserType().equals(UserType.ADMIN)) {
                    user.setWordLevel(wordLevel);
                    userService.saveUser(user);
                    wordService.setAllWordToUser(callbackQuery.getMessage().getChatId(), wordLevel);
                    execute(msg);
                    execute(editMessageReplyMarkup);
                }
                break;
            case "B1":
                if (user.getUserType().equals(UserType.USER) || user.getUserType().equals(UserType.PREMIUM)
                        || user.getUserType().equals(UserType.ADMIN)) {
                    user.setWordLevel(wordLevel);
                    userService.saveUser(user);
                    wordService.setAllWordToUser(callbackQuery.getMessage().getChatId(), wordLevel);
                    execute(msg);
                    execute(editMessageReplyMarkup);
                }
                break;
            case "B2":
                if (user.getUserType().equals(UserType.PREMIUM) || user.getUserType().equals(UserType.ADMIN)) {
                    user.setWordLevel(wordLevel);
                    userService.saveUser(user);
                    wordService.setAllWordToUser(callbackQuery.getMessage().getChatId(), wordLevel);
                    execute(msg);
                    execute(editMessageReplyMarkup);
                } else {
                    SendMessage msg2 = SendMessage.builder()
                            .chatId(callbackQuery.getMessage().getChatId().toString())
                            .text("У вас нет доступа к этому уровню. \n Чтобы разблокировать доступ к сложным уровням, подпишитесь на канал @english_in_use_channel")
                            .build();
                    msg2.setReplyMarkup(keyboardMaker.checkIfUserSubscribedToChannel());
                    execute(msg2);
                }
                break;
            case "C1":
                if (user.getUserType().equals(UserType.PREMIUM) || user.getUserType().equals(UserType.ADMIN)) {
                    user.setWordLevel(wordLevel);
                    userService.saveUser(user);
                    wordService.setAllWordToUser(callbackQuery.getMessage().getChatId(), wordLevel);
                    execute(msg);
                    execute(editMessageReplyMarkup);
                } else {
                    SendMessage msg3 = SendMessage.builder()
                            .chatId(callbackQuery.getMessage().getChatId().toString())
                            .text("У вас нет доступа к этому уровню. \n Чтобы разблокировать доступ к сложным уровням, подпишитесь на канал @english_in_use_channel")
                            .build();
                    msg3.setReplyMarkup(keyboardMaker.checkIfUserSubscribedToChannel());
                    execute(msg3);
                }
                break;
            case "C2":
                if (user.getUserType().equals(UserType.PREMIUM) || user.getUserType().equals(UserType.ADMIN)) {
                    user.setWordLevel(wordLevel);
                    userService.saveUser(user);
                    wordService.setAllWordToUser(callbackQuery.getMessage().getChatId(), wordLevel);
                    execute(msg);
                    execute(editMessageReplyMarkup);
                } else {
                    SendMessage msg4 = SendMessage.builder()
                            .chatId(callbackQuery.getMessage().getChatId().toString())
                            .text("У вас нет доступа к этому уровню. \n Чтобы разблокировать доступ к сложным уровням, подпишитесь на канал @english_in_use_channel")
                            .build();
                    msg4.setReplyMarkup(keyboardMaker.checkIfUserSubscribedToChannel());
                    execute(msg4);
                }
                break;
            default:
                break;
        }
    }

    private void handleChooseDifficult(Message message) {
        SendMessage startMessage = new SendMessage();
        startMessage.setChatId(message.getChatId().toString());
        startMessage.setText("Выбери сложность:");
        InlineKeyboardMarkup keyboard = keyboardMaker.getDifficultLevelKeyboard();
        startMessage.setReplyMarkup(keyboard);
        try {
            execute(startMessage);
        } catch (TelegramApiException e) {
            logger.error("Error while sending start message: {}", e.getMessage());
        }
    }
}