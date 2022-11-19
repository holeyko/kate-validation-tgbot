package handler.impl;

import bot.Bot;
import handler.AbstractUpdateHandler;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class LessonsHandler extends AbstractUpdateHandler {
    public LessonsHandler(Bot.ExecutionService executionService) {
        super(executionService);
    }

    @Override
    public void handleUpdateImpl(Update update) throws TelegramApiException {
        Long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();

        sendMessage.setChatId(chatId);
        sendMessage.setText(Bot.TEXT.LESSONS.getText());
        sendMessage.setReplyMarkup(Bot.InlineMarkups.LESSONS.getKeyboardMarkup());

        executionService.executeMessage(sendMessage, "HTML");
    }
}
