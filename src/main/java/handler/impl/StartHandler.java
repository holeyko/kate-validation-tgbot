package handler.impl;

import bot.Bot;
import handler.AbstractUpdateHandler;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class StartHandler extends AbstractUpdateHandler {
    public StartHandler(Bot.ExecutionService executionService) {
        super(executionService);
    }

    @Override
    protected void handleUpdateImpl(Update update) throws TelegramApiException {
        Long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();

        sendMessage.setChatId(chatId);
        sendMessage.setText(Bot.TEXT.START.getText());
        sendMessage.setReplyMarkup(Bot.ReplyMarkups.CHECK_SUBSCRIBE.getKeyboardMarkup());

        executionService.executeMessage(sendMessage, "HTML");
    }
}
