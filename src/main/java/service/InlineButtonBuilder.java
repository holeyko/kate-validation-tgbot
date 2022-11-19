package service;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

public class InlineButtonBuilder {
    private InlineKeyboardButton button = new InlineKeyboardButton();

    public InlineButtonBuilder setText(String text) {
        button.setText(text);
        return this;
    }

    public InlineButtonBuilder setCallbackData(String callbackData) {
        button.setCallbackData(callbackData);
        return this;
    }

    public InlineButtonBuilder setUrl(String url) {
        button.setUrl(url);
        return this;
    }

    public InlineKeyboardButton build() {
        return button;
    }
}

