package bot;


import config.Config;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Bot extends TelegramLongPollingBot {
    private final String token;
    private final String name;
    private final String linkAfterValidation;
    private final String linkForSubscribe;
    private final ReplyKeyboardMarkup mainMarkup;

    public Bot(String token) {
        this.token = token;
        this.name = Config.BOT_NAME;
        this.linkAfterValidation = Config.LINK_AFTER_VALIDATE;
        this.linkForSubscribe = Config.KATE_CHANNEL;

        this.mainMarkup = new ReplyKeyboardMarkup();
        this.mainMarkup.setResizeKeyboard(true);
        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow mainRow = new KeyboardRow();
        mainRow.add("check");
        rows.add(mainRow);
        this.mainMarkup.setKeyboard(rows);
    }


    @Override
    public void onUpdateReceived(Update update) {
        String startText = "Please, subscribe: " + this.linkForSubscribe + "\n";
        String validationPassedText = "Happy, you in the channel\nurl: " + this.linkAfterValidation + "\n";
        String validationFailedText = "Unhappy\nPlease, subscribe: " + this.linkForSubscribe + "\n";
        String botFailedText = "Oops";

        SendMessage returnMessage = new SendMessage();
        returnMessage.setReplyMarkup(this.mainMarkup);
        Message message = update.getMessage();
        Long userId = message.getFrom().getId();
        String text = "Please, repeat command\n";

        if (message.getText().equals("/start")) {
            text = startText;
        } else if (message.getText().equals("check")) {
            try {
                URL checkMemberUrl = new URL(
                        "https://api.telegram.org/bot" + this.getBotToken() + "/getChatMember?chat_id=" + this.linkForSubscribe + "&user_id=" + userId
                );
                HttpURLConnection con = (HttpURLConnection) checkMemberUrl.openConnection();
                con.setRequestMethod("GET");

                BufferedReader responseMessage = new BufferedReader(
                        new InputStreamReader(con.getInputStream())
                );
                String line = responseMessage.readLine();

                if (line.contains("\"status\":\"member\"")) {
                    text = validationPassedText;
                } else {
                    text = validationFailedText;
                }
            } catch (IOException e) {
                System.err.println("Something went wrong: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            text = botFailedText;
        }

        returnMessage.setChatId(userId);
        returnMessage.setText(text);

        try {
            execute(returnMessage);
        } catch (TelegramApiException e) {
            System.err.println("Something went wrong: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public String getBotToken() {
        return this.token;
    }

    @Override
    public String getBotUsername() {
        return this.name;
    }
}
