package bot;
import bot.updates.VoteUpdate;
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
import static bot.utility.DateRetriever.getNextThursday;
import static bot.utility.GroupSplitter.getSplitGroupNumbers;
import static bot.utility.TextFormatter.getFormattedAllocateText;
import static bot.utility.TextFormatter.getFormattedVoteUpdateText;

public class Bot extends TelegramLongPollingBot {

    private Poll mostRecentPoll = null;

    private String mostRecentPollId = null;
    private ArrayList<User> mostRecentPollComing = new ArrayList<>();

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
        if (update.hasPollAnswer() && update.getPollAnswer().getPollId().equals(mostRecentPollId)) {
            PollAnswer thisPollAnswer = update.getPollAnswer();
            User sender = thisPollAnswer.getUser();
            String senderUsername = sender.getUserName();

            if (thisPollAnswer.getOptionIds().isEmpty()) {
                mostRecentPollComing.remove(sender); // removes only if user is in list
                System.out.println(getFormattedVoteUpdateText(senderUsername, Vote.Retract));
                System.out.println("Current coming: " + mostRecentPollComing.toString());
            } else if (thisPollAnswer.getOptionIds().get(0).equals(0)) {
                mostRecentPollComing.add(sender);
                System.out.println(getFormattedVoteUpdateText(senderUsername, Vote.Coming));
                System.out.println("Current coming: " + mostRecentPollComing.toString());
            } else if (thisPollAnswer.getOptionIds().get(0).equals(1)) {
                mostRecentPollComing.remove(sender);
                System.out.println(getFormattedVoteUpdateText(senderUsername, Vote.NotComing));
                System.out.println("Current coming: " + mostRecentPollComing.toString());
            }
        }

        Message message = update.getMessage();
        Long chatId = message.getChatId();
        String messageText = message.getText();
        User sender = message.getFrom();
        Long senderId = sender.getId();
        String senderUsername = sender.getFirstName();

        if (message.isCommand()) {
            if (messageText.equals(POLL_COMMAND)) {

                List<String> optionList = new ArrayList<>();
                optionList.add("Coming");
                optionList.add("Not coming");
                SendPoll sendPollObj = new SendPoll(chatId.toString(), getNextThursday(), optionList);
                sendPollObj.setIsAnonymous(false);

                try {
                    Message returnMessage = execute(sendPollObj);
                    if (returnMessage.hasPoll()) {
                        mostRecentPoll = returnMessage.getPoll();
                        mostRecentPollId = mostRecentPoll.getId();
                    }
                    mostRecentPollComing = new ArrayList<>();
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            } else if (messageText.equals(ALLOCATE_COMMAND)) {
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
}
