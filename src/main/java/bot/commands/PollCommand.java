package bot.commands;

import bot.Bot;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.polls.Poll;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Arrays;

import static bot.tools.DateRetriever.getNextThursday;
import static bot.tools.StandardMessages.VOTE_BY_12PM;

public class PollCommand {

    private static final ArrayList<String> pollOptionList = new ArrayList<>(Arrays.asList("Coming", "Not Coming"));

    private Message message;
    private Long chatId;
    private Poll newPoll;

    public PollCommand(Update update, Bot bot) {
        message = update.getMessage();
        chatId = message.getChatId();
        newPoll = new Poll();
        createSendPoll(bot);
    }

    public void createSendPoll(Bot bot) {
        String nextThursday = getNextThursday();
        SendPoll sendPollObj = new SendPoll(chatId.toString(), nextThursday, pollOptionList);
        sendPollObj.setIsAnonymous(false);
        Message executionMessage;
        try {
            executionMessage = bot.execute(sendPollObj);
            newPoll = executionMessage.getPoll();
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
        sendVoteBy12pmText(bot);
    }

    public Poll createNewPoll() {
        return newPoll;
    }

    public void sendVoteBy12pmText(Bot bot) {
        bot.sendText(chatId, VOTE_BY_12PM);
    }
}
