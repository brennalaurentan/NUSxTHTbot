package tutorial;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.polls.Poll;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

public class Bot extends TelegramLongPollingBot {

    static long masterUserId = 883878234;
    static Poll mostRecentPoll = null;

    @Override
    public String getBotUsername() {
        return "NUSxTHTbot";
    }

    @Override
    public String getBotToken() {
        return "7218428311:AAFcUaJ3R9_kvhJbHsPpuc-Sj7ztMnI_ZtA";
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        String messageText = message.getText();
        User sender = message.getFrom();
        Long senderId = sender.getId();
        String senderUsername = sender.getFirstName();
        Long chatId = message.getChatId();

        if (message.isCommand()) {
            if (messageText.equals("/poll")) {

                List<String> optionList = new ArrayList<>();
                optionList.add("Coming");
                optionList.add("Not coming");
                SendPoll sendPollObj = new SendPoll(chatId.toString(), getNextThursday(), optionList);
                sendPollObj.setIsAnonymous(false);

                try {
                    Message returnMessage = execute(sendPollObj);
                    if (returnMessage.hasPoll()) {
                        mostRecentPoll = returnMessage.getPoll();
                    }
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            } else if (messageText.equals("/allocate")) {
                sendText(chatId, "Most recent poll is: " + mostRecentPoll.getQuestion()); // echo back
            }
        }

        sendText(senderId, message.getText()); // echo back

        String messageToAdmin = "[ADMIN] " + senderUsername + " said " + "\"" + messageText + "\"";
        sendText(masterUserId, messageToAdmin);

        System.out.println(senderUsername + " wrote " + messageText);
    }

    public void sendText(Long recipientId, String text) {
        SendMessage sendMessageObj =
                SendMessage.builder()
                        .chatId(recipientId.toString()) // to recipient
                        .text(text) // with text
                        .build();

        try {
            execute(sendMessageObj);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        Bot bot = new Bot();
        botsApi.registerBot(new Bot());
        //bot.sendText(user.getId(), "Hello World!");
    }

    public String getNextThursday() {
        LocalDate today = LocalDate.now();
        LocalDate closestThursday = today.with(TemporalAdjusters.next(DayOfWeek.THURSDAY));
        return closestThursday.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"));
    }
}
