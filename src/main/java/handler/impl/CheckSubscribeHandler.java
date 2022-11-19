package handler.impl;

import bot.Bot;
import handler.AbstractUpdateHandler;
import handler.exception.HandleException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class CheckSubscribeHandler extends AbstractUpdateHandler {
    public CheckSubscribeHandler(Bot.ExecutionService executionService) {
        super(executionService);
    }

    @Override
    public void handleUpdateImpl(Update update) throws TelegramApiException {
        Boolean passed = (Boolean) data.getOrDefault("passed", null);

        if (passed == null) {
            throw new HandleException("There is no passed argument in the CheckSubscribeHandler");
        }

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId());

        if (passed) {
            sendMessage.setText(Bot.TEXT.PASSED_SUBSCRIBED.getText());
            sendMessage.setReplyMarkup(Bot.ReplyMarkups.TAKE.getKeyboardMarkup());
        } else {
            sendMessage.setText(Bot.TEXT.FAILED.getText());
            sendMessage.setReplyMarkup(Bot.ReplyMarkups.CHECK_SUBSCRIBE.getKeyboardMarkup());
        }

        executionService.executeMessage(sendMessage, "HTML");
    }
}
