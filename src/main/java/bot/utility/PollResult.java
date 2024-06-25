package bot.utility;

import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.polls.Poll;
import org.telegram.telegrambots.meta.api.objects.polls.PollAnswer;

import java.util.ArrayList;

import static bot.tools.TextFormatter.getFormattedVoteUpdateText;

public class PollResult {
    private Poll poll;
    private ArrayList<User> comingUserList;
    private ArrayList<User> notComingUserList;
    private static final int OPTION_COMING = 0;
    private static final int OPTION_NOT_COMING = 1;

    public PollResult(Poll newPoll) {
        poll = newPoll;
        comingUserList = new ArrayList<>();
    }

    public void update(PollAnswer thisPollAnswer, Poll existingPoll) {
        if (!pollAnswerMatchesPollId(thisPollAnswer, existingPoll)) {
            return;
        }

        User sender = thisPollAnswer.getUser();
        String senderUsername = sender.getUserName();

        if (thisPollAnswer.getOptionIds().isEmpty()) {
            comingUserList.remove(sender); // removes only if user is in list
            System.out.println(getFormattedVoteUpdateText(senderUsername, Vote.Retract));
            System.out.println("Current coming: " + comingUserList.toString());
        } else if (thisPollAnswer.getOptionIds().get(0).equals(OPTION_COMING)) {
            comingUserList.add(sender);
            System.out.println(getFormattedVoteUpdateText(senderUsername, Vote.Coming));
            System.out.println("Current coming: " + comingUserList.toString());
        } else if (thisPollAnswer.getOptionIds().get(0).equals(OPTION_NOT_COMING)) {
            comingUserList.remove(sender);
            System.out.println(getFormattedVoteUpdateText(senderUsername, Vote.NotComing));
            System.out.println("Current coming: " + comingUserList.toString());
        }

    }

    public boolean pollAnswerMatchesPollId(PollAnswer givenPollAnswer, Poll givenPoll) {
        return givenPollAnswer.getPollId().equals(givenPoll.getId());
    }

}
