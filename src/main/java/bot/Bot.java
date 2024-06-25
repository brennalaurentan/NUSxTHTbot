package bot;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.methods.polls.StopPoll;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.polls.Poll;
import org.telegram.telegrambots.meta.api.objects.polls.PollAnswer;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

import static bot.utility.TextFormatter.getFormattedAllocateText;

public class Bot extends TelegramLongPollingBot {

    static long masterUserId = 883878234;
    private Poll mostRecentPoll = null;

    static String masterChatId = "-4276298909";

    private String mostRecentPollId = null;
    private ArrayList<User> mostRecentPollComing = new ArrayList<>();
    private Integer mostRecentPollComingCount = 0;

    @Override
    public String getBotUsername() {
        return "NUSxTHTbot";
    }

    @Override
    public String getBotToken() {
        return "7423596148:AAEOTxceqjnbgudx8tCu3o2TRpGRmrDiG5o";
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
                mostRecentPollComing.remove(sender); // removes if user is in list
                mostRecentPollComingCount++;
                System.out.println("POLL ANSWER DETECTED: " + senderUsername + " RETRACTED VOTE");
                System.out.println("Current coming: " + mostRecentPollComing.toString());
            } else if (thisPollAnswer.getOptionIds().get(0).equals(0)) {
                mostRecentPollComing.add(sender);
                System.out.println("POLL ANSWER DETECTED: " + senderUsername + " voted COMING");
                System.out.println("Current coming: " + mostRecentPollComing.toString());
            } else if (thisPollAnswer.getOptionIds().get(0).equals(1)) {
                mostRecentPollComing.remove(sender);
                System.out.println("POLL ANSWER DETECTED: " + senderUsername + " voted NOT COMING");
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
                        mostRecentPollId = mostRecentPoll.getId();
                        mostRecentPollComingCount = 0;
                    }
                    mostRecentPollComing = new ArrayList<>();
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            } else if (messageText.equals("/allocate")) { // Respondents not working smh
                System.out.println("Entered ALLOCATE");
                String pollHeader = mostRecentPoll.getQuestion();
                sendText(chatId, "Most recent poll is: " + pollHeader);
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

            } else if (messageText.equals("/stop")) {
                System.out.println("entered STOP");
                StopPoll stopPollObj = StopPoll.builder() // SOMETHING WRONG W THIS PART I THINK
                        .chatId(chatId.toString())
                        .build();

                try {
                    Poll stoppedPoll = execute(stopPollObj);
                    sendText(chatId, "Poll " + mostRecentPollId + " stopped");
                    sendText(chatId, stoppedPoll.toString());
                } catch (TelegramApiException e) {
                    sendText(chatId, "Stop bot poll failed");
                    throw new RuntimeException(e);
                }
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

    public String getNextThursday() {
        LocalDate today = LocalDate.now();
        LocalDate closestThursday = today.with(TemporalAdjusters.next(DayOfWeek.THURSDAY));
        return closestThursday.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"));
    }

    public ArrayList<Integer> getSplitGroupNumbers(Integer totalGroupSize) {
        ArrayList<Integer> groupSizes = new ArrayList<>();
        if (totalGroupSize % 2 == 0) {
            int oneGroupSize = totalGroupSize / 2;
            groupSizes.add(oneGroupSize);
            groupSizes.add(oneGroupSize);
        } else {
          int oneGroupSize = (totalGroupSize / 2) + 1;
          int otherGroupSize = totalGroupSize / 2;
          groupSizes.add(oneGroupSize);
          groupSizes.add(otherGroupSize);
        }
        return groupSizes;
    }
}
