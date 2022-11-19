package handler;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Map;

public interface UpdateHandler {
    void handleUpdate(Update update) throws TelegramApiException;

    Map<String, Object> getData();

    void setData(String key, Object value);
}
