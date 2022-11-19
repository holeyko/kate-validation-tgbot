package handler.impl;

import bot.Bot;
import config.Config;
import handler.AbstractUpdateHandler;
import handler.UpdateHandler;
import handler.exception.HandleException;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;

public class SendFileHandler extends AbstractUpdateHandler {
    public SendFileHandler(Bot.ExecutionService executionService) {
        super(executionService);
    }

    public void handleUpdateImpl(Update update) throws TelegramApiException {
        String fileName = (String) data.getOrDefault("fileName", null);
        String fileText = (String) data.getOrDefault("fileText", null);

        if (fileName == null || fileName.isEmpty()) {
            throw new HandleException("File name is null or empty");
        }

        File projectRoot = new File(System.getProperty("user.dir"));
        File sendFile = new File(projectRoot, new File(Config.PATH_TO_DATA, fileName).getPath());

        if (!sendFile.isFile() || !sendFile.exists()) {
            UpdateHandler errorHandle = new ErrorHandler(executionService);
            errorHandle.setData("errorText", "Указанного курса больше не существует(");
            errorHandle.handleUpdate(update);
            return;
        }

        InputFile inputFile = new InputFile(sendFile);
        Long chatId = update.getMessage().getChatId();

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(fileText);

        SendDocument sendDocument = new SendDocument();
        sendDocument.setChatId(chatId);
        sendDocument.setDocument(inputFile);

        executionService.executeMessage(sendMessage, "HTML");
        executionService.executeDocument(sendDocument);
    }
}
