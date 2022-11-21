package handler;

import bot.Bot;
import config.Config;
import handler.exception.HandleException;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractUpdateHandler implements UpdateHandler {
    protected final Bot.ExecutionService executionService;
    protected Map<String, Object> data = new HashMap<>();

    public AbstractUpdateHandler(Bot.ExecutionService executionService) {
        this.executionService = executionService;
    }

    @Override
    public void handleUpdate(Update update) throws TelegramApiException {
        if (update == null) {
            throw new HandleException("Handle is null");
        } else if (!update.hasMessage()) {
            throw new HandleException("Handle hasn't an any message");
        }

        handleUpdateImpl(update);
    }

    protected abstract void handleUpdateImpl(Update update) throws TelegramApiException;

    protected File getDocumentFromData() {
        String documentName = (String) data.getOrDefault("documentName", null);
        return getFileFromData(documentName);
    }

    protected File getImageFromData() {
        String imageName = (String) data.getOrDefault("imageName", null);
        return getFileFromData(imageName);
    }

    private File getFileFromData(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            throw new HandleException("File name is empty or null");
        }

        File projectRoot = new File(System.getProperty("user.dir"));
        File file = new File(projectRoot, new File(Config.PATH_TO_DATA, fileName).getPath());

        if (!file.exists()) {
            System.out.println("File doesn't exist [fileName=" + fileName + ",filePath=" + file.getPath() + "]"); //TODO
            throw new HandleException("File doesn't exist [fileName=" + fileName + "]");
        }

        if (!file.isFile()) {
            System.out.println("File isn't file [fileName=" + fileName + ",filePath=" + file.getPath() + "]"); //TODO
            throw new HandleException("File isn't file [fileName=" + fileName + "]");
        }

        return file;
    }

    @Override
    public Map<String, Object> getData() {
        return data;
    }

    @Override
    public void setData(String key, Object value) {
        data.put(key, value);
    }
}
