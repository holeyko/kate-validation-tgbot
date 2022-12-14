package bot;


import config.Config;
import handler.UpdateHandler;
import handler.exception.HandleException;
import handler.impl.*;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import service.InlineButtonBuilder;
import service.InlineKeyboardMarkupBuilder;
import service.ReplyKeyboardMarkupBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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

        public void executePhoto(SendPhoto sendPhoto, String parseMode) throws TelegramApiException {
            sendPhoto.setParseMode(parseMode);
            bot.execute(sendPhoto);
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
        UpdateHandler handler = null;

        if (update == null) {
            System.err.println("Update is null");
            return;
        }

        if (update.hasMessage()) {
            String messageText = update.getMessage().getText();

            if (messageText.equals("/start") || messageText.equals("/help")) {
                handler = new StartHandler(executionService);
            } else if (messageText.equals(Buttons.CHECK_SUBSCRIBED.getText())) {
                handler = new CheckSubscribeHandler(executionService);
                boolean passed = false;
                try {
                    passed = checkSubscribe(update.getMessage().getChatId());
                } catch (IOException ignored) {
                    //No operations.
                }

                handler.setData("passed", passed);
            } else {
                try {
                    if (checkSubscribe(update.getMessage().getChatId())) {
                        if (messageText.equals(Buttons.LESSONS.getText())) {
                            handler = new LessonsHandler(executionService);
                            handler.setData("imageName", "lessons.png");
                        } else if (messageText.equals(Buttons.FILE.getText())) {
                            handler = new SendDocumentHandler(executionService);
                            handler.setData("fileText", TEXT.WINTER_DOCUMENT.getText());
                            handler.setData("documentName", "50 ???????? ???????????? ????????.pdf");
                            handler.setData("imageName", "document.png");
                        }
                    }
                } catch (IOException ignored) {
                    // No operation.
                }
            }
        }

        if (handler == null) {
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

    public boolean checkSubscribe(Long chatId) throws IOException {
        String url = "https://api.telegram.org/bot" + token + "/getChatMember?chat_id=" + Config.KATE_CHANNEL + "&user_id=" + chatId;
        URL checkMemberUrl = new URL(url);
        HttpURLConnection con = (HttpURLConnection) checkMemberUrl.openConnection();

        con.setRequestMethod("GET");
        BufferedReader responseMessage = new BufferedReader(
                new InputStreamReader(con.getInputStream())
        );
        String line = responseMessage.readLine();

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);

        return line.contains("\"status\":\"member\"");
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
        CHECK_SUBSCRIBED("?? ????????????????(??)"),
        FILE("????????"),
        LESSONS("??????????");

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
                .setOneTimeKeyboard(true)
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
                .addRow(List.of(new InlineButtonBuilder().setText("????????????????????????????????").setUrl("https://youtu.be/98HQVFl_0cA").build()))
                .addRow(List.of(new InlineButtonBuilder().setText("??????????????").setUrl("https://youtu.be/YqPeYdkccA0").build()))
                .addRow(List.of(new InlineButtonBuilder().setText("??????????????????").setUrl("https://youtu.be/-mFMWhvFyuM").build()))
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
                ????????????????! ?????? ??????-???????????????? @k_visual

                ???? ?????????? ???????????????? ??????????????,
                ?????????????????? ???? ?????? ??????????????
                                 
                <a href="https://t.me/k_visual">??????????????????????</a>
                                 
                <b>???????? ???????????????? ????????, ?????? ?????????? ???????????? ???? ????????????????(??)??</b>
                """),
        PASSED_SUBSCRIBED("""
                ??????????????! ?????????????? ???? ?????????????????????? ?? ???????? ?? ?????????? ???? ?????????????? ?????????? ?????????????????????? ?????? ?????????????? ?? ????????????!

                <b>?????????????? ???? ???????????? ?? ?????????????? ???????? ??????????????! ??????????????, ?????????? ?????????????? :)</b>
                                
                ???? ?????????? ????????????????, ???????? ?????? ?? ???????????? ??????????????????: @k_vanova
                """),
        FAILED("""
                ????..??????-???? ?????????? ???? ??????????

                <b>?????????????? ???????????????? <a href="https://t.me/k_visual">???? ?????? ????????</a> ?????? ?????? ?? ?????????? ???? ????????????</b>
                """),
        WINTER_DOCUMENT("""
                <b>???????? ?????? ?????????? ???????????????? ???????????????????? ???????? ??????????????????????</b>
                                
                ?????????????????????? ????????????, ?????????? ???????? ?? ???????? ??????????????????: @k_vanova
                """),
        LESSONS("""
                <b>?????????? ?????????????? ?????? ?????????? ?????? ????????, ?????????? ?????? ???????????? ?????????????????? ????????????????</b>
                                
                ?? ????????????:
                ??? ?????? ?????????? ???????? ??????????????????
                ??? ?????? ?????????? ???????? ????????????????????????????????
                ??? ?????? ?????????????? ??????????????-????????
                ??? ???????? ???? ???????????????????? ????????????????
                ??? ??????????????????

                <b>???????????????? ?????????? ?? ?????????????? ?????????????????? ???????????????? ?????????????? ?????? ??????????????????</b>
                
                ???????? ???????????????? ??????????, ???????????? ??????????????????????????: @k_vanova
                """),
        INDIVIDUAL_CONSULTATION("""
                <b>?????????? ?????????????????? ?????????????????????? ???????????? ????????????????????????????????</b>
                
                ?????? ???????????????? ??????????: @k_vanova
                
                <u>?????? ???????????????????? ?? ???????????? ?????????? ?????????????????? ?? ?????????? ????????????????????????????</u> ?? ?????? ?????????????? ???????????????? ?????????????????????
                
                <b>???? ???????? ???????????????????? ???????????? ?? ??????????????????, ?????????? ???????????????????? ???? ???????????????????????????? ????????????????????????,</b> ?????? ???? ???????????????? ???????? ????????????????????????????????, ?????????????? ?? ???????????????? ?????????????????? ???????????? ?????? ??????????????????
                
                <b>???? ?????????????????? 7000???</b>
                <b>????????????????????: @k_vanova</b>
                """);

        private final String text;

        TEXT(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }
}
