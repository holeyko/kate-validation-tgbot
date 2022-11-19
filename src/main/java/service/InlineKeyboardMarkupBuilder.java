package service;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class InlineKeyboardMarkupBuilder {
    private List<List<InlineKeyboardButton>> rows = new ArrayList<>();
    private InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();

    public InlineKeyboardMarkupBuilder addRow(List<InlineKeyboardButton> row) {
        rows.add(row);
        return this;
    }

    public InlineKeyboardMarkup build() {
        keyboardMarkup.setKeyboard(rows);
        return keyboardMarkup;
    }
}
