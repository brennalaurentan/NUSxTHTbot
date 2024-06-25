package bot;
import bot.commands.PollCommand;
import bot.utility.PollResult;
import bot.utility.Vote;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.polls.Poll;
import org.telegram.telegrambots.meta.api.objects.polls.PollAnswer;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

import static bot.BotConfig.botToken;
import static bot.commands.CommandKeywords.ALLOCATE_COMMAND;
import static bot.commands.CommandKeywords.POLL_COMMAND;
import static bot.commands.CommandValidator.isAllocateCommand;
import static bot.commands.CommandValidator.isPollAnswer;
import static bot.commands.CommandValidator.isPollCommand;
import static bot.tools.DateRetriever.getNextThursday;
import static bot.tools.GroupSplitter.getSplitGroupNumbers;
import static bot.tools.TextFormatter.getFormattedAllocateText;
import static bot.tools.TextFormatter.getFormattedVoteUpdateText;

public class Bot extends TelegramLongPollingBot {

    private Poll mostRecentPoll = null;

    private String mostRecentPollId = null;
    private ArrayList<User> mostRecentPollComing = new ArrayList<>();
    private PollResult mostRecentPollResult = null; // stores most recent poll result data

    @Override
    public String getBotUsername() {
        return "NUSxTHTbot";
    }

    @Override
    public String getBotToken() {
        return botToken;
    }


    @Override
    public void onUpdateReceived(Update update) {
        System.out.println("Update received");
        System.out.println(update.toString());
        if (isPollAnswer(update)) {
            PollAnswer thisPollAnswer = update.getPollAnswer();
            mostRecentPollResult.update(thisPollAnswer, mostRecentPoll);
        } else if (isPollCommand(update)) {
            PollCommand newPollCommand = new PollCommand(update, this);
            mostRecentPoll = newPollCommand.getNewPoll();
            mostRecentPollResult = new PollResult(mostRecentPoll);
        } else if (isAllocateCommand(update)) {
                Message message = update.getMessage();
                Long chatId = message.getChatId();
                String messageText = message.getText();
                User sender = message.getFrom();
                Long senderId = sender.getId();
                String senderUsername = sender.getFirstName();
                System.out.println("Entered ALLOCATE");
                String pollHeader = mostRecentPoll.getQuestion();
                //sendText(chatId, "Most recent poll is: " + pollHeader);
                int numComing = mostRecentPollComing.size();
                //sendText(chatId, "Respondents: " + mostRecentPoll.getTotalVoterCount());
                ArrayList<Integer> splitGroupSizes = getSplitGroupNumbers(numComing);
                ArrayList<String> firstGroup = new ArrayList<>();
                for (int i = 0; i < splitGroupSizes.get(0); i++) {
                    firstGroup.add(mostRecentPollComing.get(i).getUserName());
                }
                ArrayList<String> secondGroup = new ArrayList<>();
                for (int j = splitGroupSizes.get(0); j < mostRecentPollComing.size() - 1; j++) {
                    secondGroup.add(mostRecentPollComing.get(j).getUserName());
                }
                String allocationMessage = getFormattedAllocateText(pollHeader, firstGroup, secondGroup);
                sendText(chatId, allocationMessage);
        }
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
}
