package bot;


import config.Config;
import handler.UpdateHandler;
import handler.exception.HandleException;
import handler.impl.*;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import service.InlineButtonBuilder;
import service.InlineKeyboardMarkupBuilder;
import service.ReplyKeyboardMarkupBuilder;

import java.util.List;

public class Bot extends TelegramLongPollingBot {
    public static class ExecutionService {
        private final TelegramLongPollingBot bot;

        private ExecutionService(TelegramLongPollingBot bot) {
            this.bot = bot;
        }

        public void executeMessage(SendMessage sendMessage) throws TelegramApiException {
            bot.execute(sendMessage);
        }

        public void executeMessage(SendMessage sendMessage, String parseMode) throws TelegramApiException {
            sendMessage.setParseMode(parseMode);
            bot.execute(sendMessage);
        }

        public void executeDocument(SendDocument sendDocument) throws TelegramApiException {
            bot.execute(sendDocument);
        }
    }

    private final String token;
    private final String name;
    private final ExecutionService executionService;

    public Bot(String token) {
        this.token = token;
        this.name = Config.BOT_NAME;
        this.executionService = new ExecutionService(this);
    }


    @Override
    public void onUpdateReceived(Update update) {
        if (update == null || !update.hasMessage()) {
            System.err.println("Handle hasn't message");
        } else {
            UpdateHandler handler;
            String messageText = update.getMessage().getText();

            if (messageText.equals("/start") || messageText.equals("/help")) {
                handler = new StartHandler(executionService);
            } else if (messageText.equals(Buttons.CHECK_SUBSCRIBED.getText())) {
                handler = new CheckSubscribeHandler(executionService);
                handler.setData("botToken", token);
            } else if (messageText.equals(Buttons.LESSONS.getText())) {
                handler = new LessonsHandler(executionService);
            } else if (messageText.equals(Buttons.FILE.getText())) {
                handler = new SendFileHandler(executionService);
                handler.setData("fileName", "50 идей зимних фото.pdf");
                handler.setData("fileText", TEXT.WINTER_DOCUMENT.getText());
            } else {
                handler = new ErrorHandler(executionService);
                handler.setData("errorText", TEXT.FAILED.getText());
            }

            try {
                handler.handleUpdate(update);
            } catch (HandleException e) {
                handler = new ErrorHandler(executionService);
                handler.setData("errorText", TEXT.FAILED.getText());

                try {
                    handler.handleUpdate(update);
                } catch (TelegramApiException ex) {
                    System.err.println(e.getMessage());
                    e.printStackTrace();
                }
            } catch (TelegramApiException e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getBotToken() {
        return this.token;
    }

    @Override
    public String getBotUsername() {
        return this.name;
    }

    public enum Buttons {
        CHECK_SUBSCRIBED("Я подписан(а)"),
        FILE("Файл"),
        LESSONS("Уроки");

        private final String text;

        Buttons(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }

    public enum ReplyMarkups {
        CHECK_SUBSCRIBE(new ReplyKeyboardMarkupBuilder()
                .addRow(List.of(Buttons.CHECK_SUBSCRIBED.text))
                .setResizable(true)
                .build()
        ),
        TAKE(new ReplyKeyboardMarkupBuilder()
                .addRow(List.of(Buttons.FILE.getText(), Buttons.LESSONS.getText()))
                .setResizable(true)
                .build()
        );

        private final ReplyKeyboardMarkup keyboardMarkup;

        ReplyMarkups(ReplyKeyboardMarkup keyboardMarkup) {
            this.keyboardMarkup = keyboardMarkup;
        }

        public ReplyKeyboardMarkup getKeyboardMarkup() {
            return keyboardMarkup;
        }
    }

    public enum InlineMarkups {
        LESSONS(new InlineKeyboardMarkupBuilder()
                .addRow(List.of(new InlineButtonBuilder().setText("Урок 1").setUrl("https://youtu.be/98HQVFl_0cA").build()))
                .addRow(List.of(new InlineButtonBuilder().setText("Урок 2").setUrl("https://youtu.be/YqPeYdkccA0").build()))
                .addRow(List.of(new InlineButtonBuilder().setText("Урок 2").setUrl("https://youtu.be/-mFMWhvFyuM").build()))
                .build());
        private final InlineKeyboardMarkup keyboardMarkup;

        InlineMarkups(InlineKeyboardMarkup keyboardMarkup) {
            this.keyboardMarkup = keyboardMarkup;
        }

        public InlineKeyboardMarkup getKeyboardMarkup() {
            return keyboardMarkup;
        }
    }

    public enum TEXT {
        START("""
                Приветик! Это бот-помощник @k_visual

                🎁 Чтобы получить подарок,
                подпишись на мой блог✔️
                                 
                <a href="https://t.me/k_visual">ПОДПИСАТЬСЯ</a>
                                 
                <b>Если подписка есть, жми сразу кнопку «Я подписан(а)»</b>
                """),
        PASSED_SUBSCRIBED("""
                Отлично! Спасибо за подписку❤️ У меня в блоге ты найдёшь много интересного про контент и визуал!

                <b>Нажимай на кнопку и забирай свой подарок! Надеюсь, будет полезно :)</b>
                
                По любым вопросам, пиши мне в личные сообщения: @k_vanova
                """),
        FAILED("""
                Ой..Что-то пошло не так🥲

                <b>Проверь подписку <a href="https://t.me/k_visual">на мой блог</a> еще раз и нажми на кнопку</b>
                """),
        WINTER_DOCUMENT("""
                <b>Файл для самых красивых фотографий этой зимой❤️❄️</b>
                
                Обязательно делись, какие фото у тебя получатся: @k_vanova
                """),
        LESSONS("""
                <b>Здесь собраны три урока для того, чтобы уже сейчас прокачать блог👌🏻</b>
                
                В уроках:
                ✨ как вести блог регулярно
                ✨ как найти свое позиционирование
                ✨ как создать контент-план
                ✨ файл по распаковке личности
                ✨ мотивация
                
                <b>Открывай уроки и начинай создавать классный контент уже сейчас❤️</b>
                """),

        DO_NOT_UNDERSTAND("Я вас не понимаю.\nНапишите /start.");

        private final String text;

        TEXT(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }
}
