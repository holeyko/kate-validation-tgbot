package handler.impl;

import bot.Bot;
import handler.AbstractUpdateHandler;
import handler.UpdateHandler;
import handler.exception.HandleException;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;

public class SendDocumentHandler extends AbstractUpdateHandler {
    public SendDocumentHandler(Bot.ExecutionService executionService) {
        super(executionService);
    }

    public void handleUpdateImpl(Update update) throws TelegramApiException {
        String fileText = (String) data.getOrDefault("fileText", null);

        File document;
        try {
            document = getDocumentFromData();
        } catch (HandleException e) {
            UpdateHandler errorHandle = new ErrorHandler(executionService);
            errorHandle.setData("errorText", "Указанного курса больше не существует(");
            errorHandle.handleUpdate(update);
            return;
        }

        File image;
        try {
            image = getImageFromData();
        } catch (HandleException ignored) {
            image = null;
        }

        Long chatId = update.getMessage().getChatId();

        if (image == null) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText(fileText);

            executionService.executeMessage(sendMessage, "HTML");
        } else {
            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setChatId(chatId);
            sendPhoto.setPhoto(new InputFile(image));
            sendPhoto.setCaption(fileText);

            executionService.executePhoto(sendPhoto, "HTML");
        }

        SendDocument sendDocument = new SendDocument();
        sendDocument.setChatId(chatId);
        sendDocument.setDocument(new InputFile(document));

        executionService.executeDocument(sendDocument);
    }
}
