package handler;

import bot.Bot;
import handler.exception.HandleException;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

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
            throw new HandleException("Handle hasn't a message");
        }

        handleUpdateImpl(update);
    }

    protected abstract void handleUpdateImpl(Update update) throws TelegramApiException;

    @Override
    public Map<String, Object> getData() {
        return data;
    }

    @Override
    public void setData(String key, Object value) {
        data.put(key, value);
    }
}
