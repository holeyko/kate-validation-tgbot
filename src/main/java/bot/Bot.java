package bot;


import config.Config;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Bot extends TelegramLongPollingBot {
    private final String token;
    private final String name;
    private final String kateChannelLink;

    public Bot(String token) {
        this.token = token;
        this.name = Config.BOT_NAME;
        this.kateChannelLink = Config.LINK_AFTER_VALIDATE;
    }


    @Override
    public void onUpdateReceived(Update update) {
        SendMessage returnMessage = new SendMessage();
        Message message = update.getMessage();
        Long userId = message.getFrom().getId();
        String text = "text";

        if (message.getText().equals("/start")) {
            text = "Something text...\n";
        } else if (message.getText().equals("check")) {
            try {
                String json = "{\n" +
                        "    \"ok\": true,\n" +
                        "    \"result\": {\n" +
                        "        \"status\": \"member\",\n" +
                        "        \"user\": {\n" +
                        "            \"id\": 123456,\n" +
                        "            \"first_name\": \"FirstName\",\n" +
                        "            \"last_name\": \"LastName\",\n" +
                        "            \"username\": \"UserName\",\n" +
                        "            \"language_code\": \"en\"\n" +
                        "        }\n" +
                        "    }\n" +
                        "}";
                ChatMember member = new GetChatMember(this.kateChannelLink, userId).deserializeResponse(json);
                if (member.getStatus().equals("member")) {
                    text = "Happy, you in the channel\n";
                } else {
                    text = "Unhappy\n Please, subscribe: @k_visual\n";
                }
            } catch (TelegramApiException e) {
                System.err.println("Something went wrong: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            text = "Oopss\n";
        }

        returnMessage.setChatId(userId);
        returnMessage.setText(text);

        try {
            execute(returnMessage);
        } catch (TelegramApiException e) {
            System.err.println("Something went wrong: " + e.getMessage());
            e.printStackTrace();
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
}
