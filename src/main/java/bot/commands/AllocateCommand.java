package bot.commands;

import bot.Bot;
import bot.utility.PollResult;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.ArrayList;

import static bot.tools.GroupSplitter.splitIntoTwoGroups;
import static bot.tools.StandardMessages.REACT_WHEN_BOOKED;
import static bot.tools.StandardMessages.VOTE_BY_12PM;
import static bot.tools.TextFormatter.getFormattedAllocateText;

public class AllocateCommand {

    private Message message;
    private Long chatId;
    private PollResult pollResult;
    private ArrayList<User> comingUserList;
    private ArrayList<Integer> splitGroupSizes;
    private ArrayList<String> firstGroup;
    private ArrayList<String> secondGroup;
    private String allocationsText;

    public AllocateCommand(Update update) {
        message = update.getMessage();
        chatId = message.getChatId();
    }

    public void allocateGroups(PollResult pollResult) {
        this.pollResult = pollResult;
        comingUserList = pollResult.getComingList();
    }

    public void buildAllocationsText() {
        String pollQuestion = pollResult.getPollQuestion();
        int numComing = pollResult.getNumComing();
        splitGroupSizes = splitIntoTwoGroups(numComing);
        firstGroup = new ArrayList<>();
        secondGroup = new ArrayList<>();
        int firstGroupSize = splitGroupSizes.get(0);
        int secondGroupSize = splitGroupSizes.get(1);
        for (int i = 0; i < firstGroupSize; i++) {
            String username = comingUserList.get(i).getUserName();
            firstGroup.add(username);
        }
        for (int j = 0; j < secondGroupSize; j++) {
            String username = comingUserList.get(firstGroupSize + j).getUserName();
            secondGroup.add(username);
        }

        allocationsText = getFormattedAllocateText(pollQuestion, firstGroup, secondGroup);
    }

    public void sendAllocationsText(Bot bot) {
        buildAllocationsText();
        bot.sendText(chatId, allocationsText);
        sendReactWhenBookedText(bot);
    }

    public void sendReactWhenBookedText(Bot bot) {
        bot.sendText(chatId, REACT_WHEN_BOOKED);
    }
}
