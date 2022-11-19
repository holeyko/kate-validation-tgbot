package handler.impl;

import bot.Bot;
import config.Config;
import handler.AbstractUpdateHandler;
import handler.exception.HandleException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CheckSubscribeHandler extends AbstractUpdateHandler {
    public CheckSubscribeHandler(Bot.ExecutionService executionService) {
        super(executionService);
    }

    @Override
    public void handleUpdateImpl(Update update) throws TelegramApiException {
        try {
            Long chatId = update.getMessage().getChatId();
            String botToken = (String) data.get("botToken");

            String url = "https://api.telegram.org/bot" + botToken + "/getChatMember?chat_id=" + Config.KATE_CHANNEL + "&user_id=" + chatId;
            URL checkMemberUrl = new URL(url);
            HttpURLConnection con = (HttpURLConnection) checkMemberUrl.openConnection();

            con.setRequestMethod("GET");
            BufferedReader responseMessage = new BufferedReader(
                    new InputStreamReader(con.getInputStream())
            );
            String line = responseMessage.readLine();

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);

            if (line.contains("\"status\":\"member\"")) {
                sendMessage.setText(Bot.TEXT.PASSED_SUBSCRIBED.getText());
                sendMessage.setReplyMarkup(Bot.ReplyMarkups.TAKE.getKeyboardMarkup());
            } else {
                sendMessage.setText(Bot.TEXT.FAILED.getText());
                sendMessage.setReplyMarkup(Bot.ReplyMarkups.CHECK_SUBSCRIBE.getKeyboardMarkup());
            }

            executionService.executeMessage(sendMessage, "HTML");
        } catch (IOException e) {
            throw new HandleException("Verification failed", e);
        }
    }
}
