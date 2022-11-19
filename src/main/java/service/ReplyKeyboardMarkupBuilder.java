package service;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class ReplyKeyboardMarkupBuilder {
    private List<KeyboardRow> rows = new ArrayList<>();
    private ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

    public ReplyKeyboardMarkupBuilder addRow(List<String> buttonNames) {
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.addAll(buttonNames);

        rows.add(keyboardRow);

        return this;
    }

    public ReplyKeyboardMarkupBuilder setOneTimeKeyboard(boolean oneTime) {
        keyboardMarkup.setOneTimeKeyboard(oneTime);
        return this;
    }

    public ReplyKeyboardMarkupBuilder setResizable(boolean resizable) {
        keyboardMarkup.setResizeKeyboard(resizable);
        return this;
    }

    public ReplyKeyboardMarkup build() {
        keyboardMarkup.setKeyboard(rows);
        return keyboardMarkup;
    }
}
