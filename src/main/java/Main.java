import bot.Bot;
import config.Credentials;

public class Main {
    public static void main(String[] args) {
        new Bot(Credentials.BOT_TOKEN).start();
    }
}
