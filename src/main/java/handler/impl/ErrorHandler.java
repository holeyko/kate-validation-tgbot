package handler.impl;

import bot.Bot;
import handler.AbstractUpdateHandler;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class ErrorHandler extends AbstractUpdateHandler {
    public ErrorHandler(Bot.ExecutionService executionService) {
        super(executionService);
    }

    @Override
    protected void handleUpdateImpl(Update update) throws TelegramApiException {
        Long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        String text = (String) data.get("errorText");

        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        sendMessage.setReplyMarkup(Bot.ReplyMarkups.CHECK_SUBSCRIBE.getKeyboardMarkup());

        executionService.executeMessage(sendMessage, "HTML");
    }
}
