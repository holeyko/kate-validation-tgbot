package handler.impl;

import bot.Bot;
import config.Config;
import handler.AbstractUpdateHandler;
import handler.exception.HandleException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;

public class LessonsHandler extends AbstractUpdateHandler {
    public LessonsHandler(Bot.ExecutionService executionService) {
        super(executionService);
    }

    @Override
    public void handleUpdateImpl(Update update) throws TelegramApiException {
        String fileImageName = (String) data.getOrDefault("fileImageName", null);

        Long chatId = update.getMessage().getChatId();
        File image;
        try {
            image = getImageFromData();
        } catch (HandleException ignored) {
            image = null;
        }

        if (image == null) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText(Bot.TEXT.LESSONS.getText());
            sendMessage.setReplyMarkup(Bot.InlineMarkups.LESSONS.getKeyboardMarkup());

            executionService.executeMessage(sendMessage, "HTML");
        } else {
            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setChatId(chatId);
            sendPhoto.setPhoto(new InputFile(image));
            sendPhoto.setCaption(Bot.TEXT.LESSONS.getText());
            sendPhoto.setReplyMarkup(Bot.InlineMarkups.LESSONS.getKeyboardMarkup());

            executionService.executePhoto(sendPhoto, "HTML");
        }

        SendMessage afterMessage = new SendMessage();
        afterMessage.setChatId(chatId);
        afterMessage.setText(Bot.TEXT.INDIVIDUAL_CONSULTATION.getText());

        executionService.executeMessage(afterMessage, "HTML");
    }
}
