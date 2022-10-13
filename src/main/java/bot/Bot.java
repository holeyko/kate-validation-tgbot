package bot;


import config.Config;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Bot extends TelegramLongPollingBot {
    private final String token;
    private final String name;
    private final String linkForSubscribe;
    private final static String documentName = "50 идей осенних фото.pdf";

    public Bot(String token) {
        this.token = token;
        this.name = Config.BOT_NAME;
        this.linkForSubscribe = Config.KATE_CHANNEL;
    }


    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        Long chatId = message.getFrom().getId();
        String messageText = message.getText();

        SendMessage returnMessage = new SendMessage();
        SendDocument returnDocument = null;
        String returnText = BotTexts.START_TEXT.text;
        ReplyKeyboardMarkup keyboardMarkup = createReplyKeyboardMarkup(
                        List.of(List.of(Buttons.CHECK_SUBSCRIBED.innerText)),
                        true);

        File projectRoot = new File(System.getProperty("user.dir"));
	System.out.println(projectRoot.getPath());
        InputFile inputFile = new InputFile(
                new File(projectRoot, "/src/main/data/" + documentName)
        );

        if (messageText.equals("/start")) {
            //nothing
        } else if (messageText.equals(Buttons.CHECK_SUBSCRIBED.innerText)) {
            try {
                URL checkMemberUrl = new URL(
                        "https://api.telegram.org/bot" + this.getBotToken() + "/getChatMember?chat_id=" + this.linkForSubscribe + "&user_id=" + chatId
                );
                HttpURLConnection con = (HttpURLConnection) checkMemberUrl.openConnection();
                con.setRequestMethod("GET");

                BufferedReader responseMessage = new BufferedReader(
                        new InputStreamReader(con.getInputStream())
                );
                String line = responseMessage.readLine();

                if (line.contains("\"status\":\"member\"")) {
                    returnText = BotTexts.PASSED_TEXT.text;
                    keyboardMarkup = null;

                    returnDocument = new SendDocument();
                    returnDocument.setDocument(inputFile);
                } else {
                    returnText = BotTexts.FAILED_TEXT.text;
                }
            } catch (IOException e) {
                System.err.println(e);
                returnText = BotTexts.FAILED_TEXT.text;
            }
        } else {
            returnText = BotTexts.DO_NOT_UNDERSTAND.text;
        }

        returnMessage.setChatId(chatId);
        returnMessage.setText(returnText);
        returnMessage.setParseMode("HTML");
        returnMessage.setReplyMarkup(keyboardMarkup);

        try {
            execute(returnMessage);
            if (returnDocument != null) {
                returnDocument.setChatId(chatId);
                execute(returnDocument);
            }
        } catch (TelegramApiException e) {
            System.err.println("Something went wrong: " + e.getMessage());
        }
    }


    private ReplyKeyboardMarkup createReplyKeyboardMarkup(List<List<String>> rowsWithButtonNames, boolean isResize) {
        List<KeyboardRow> rows = new ArrayList<>();
        for (List<String> buttonNames : rowsWithButtonNames) {
            KeyboardRow row = new KeyboardRow();
            for (String buttonName : buttonNames) {
                row.add(buttonName);
            }
            rows.add(row);
        }

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup(rows);
        keyboardMarkup.setResizeKeyboard(isResize);
        return keyboardMarkup;
    }

    @Override
    public String getBotToken() {
        return this.token;
    }

    @Override
    public String getBotUsername() {
        return this.name;
    }

    private enum Buttons {
        CHECK_SUBSCRIBED("Я подписан(а)");

        private final String innerText;

        Buttons(String innerText) {
            this.innerText = innerText;
        }
    }

    private enum BotTexts {
        START_TEXT("""
                    Приветик! Это бот-помощник @k_visual

                    🍁 Чтобы получить файл
                    «50 идей осенних фото»,
                    подпишись на мой блог✔️
                   
                    <a href="https://t.me/k_visual">ПОДПИСАТЬСЯ</a>
                    
                    Если подписка есть, жми сразу кнопку «Я подписан(а)»
                    """),

        PASSED_TEXT("""
                    Отлично! Спасибо за подписку❤️ У меня в блоге ты найдёшь много интересного про контент и визуал!

                    Вот файл с идеями для фото! Надеюсь, что он будет полезен тебе :)
                    """),

        FAILED_TEXT("""
                    Ой..Что-то пошло не так🥲

                    Проверь подписку <a href="https://t.me/k_visual">на мой блог</a> еще раз и нажми на кнопку «Я подписан(а)»
                    """),

        DO_NOT_UNDERSTAND("Я вас не понимаю.\nНапишите /start.");

        private final String text;

        BotTexts(String text) {
            this.text = text;
        }
    }
}
