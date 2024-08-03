package bot.utility;

import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.polls.Poll;
import org.telegram.telegrambots.meta.api.objects.polls.PollAnswer;

import java.util.ArrayList;

import static bot.tools.TextFormatter.getFormattedVoteUpdateText;

public class PollResult {
    private Poll poll;
    private ArrayList<User> comingUserList;
    private static final int OPTION_COMING = 0;
    private static final int OPTION_NOT_COMING = 1;

    public PollResult(Poll newPoll) {
        poll = newPoll;
        comingUserList = new ArrayList<>();
    }

    public void update(PollAnswer thisPollAnswer) {
        if (!pollAnswerMatchesPollId(thisPollAnswer, poll)) {
            return;
        }

        User sender = thisPollAnswer.getUser();
        String senderUsername = sender.getUserName();

        if (thisPollAnswer.getOptionIds().isEmpty()) {
            comingUserList.remove(sender); // removes only if user is in list
            System.out.println(getFormattedVoteUpdateText(senderUsername, Vote.Retract));
            System.out.println("[PollResult] Current coming: " + comingUserList.toString());
        } else if (thisPollAnswer.getOptionIds().get(0).equals(OPTION_COMING)) {
            comingUserList.add(sender); // second user onwards not being added, not sure why
            System.out.println(getFormattedVoteUpdateText(senderUsername, Vote.Coming));
            System.out.println("[PollResult] Current coming: " + comingUserList.toString());
            System.out.println("[PollResult] User details: " + sender);
        } else if (thisPollAnswer.getOptionIds().get(0).equals(OPTION_NOT_COMING)) {
            comingUserList.remove(sender);
            System.out.println(getFormattedVoteUpdateText(senderUsername, Vote.NotComing));
            System.out.println("[PollResult] Current coming: " + comingUserList.toString());
        }

    }

    public boolean pollAnswerMatchesPollId(PollAnswer givenPollAnswer, Poll givenPoll) {
        return givenPollAnswer.getPollId().equals(givenPoll.getId());
    }

    public String getPollQuestion() {
        return poll.getQuestion();
    }

    public int getNumComing() {
        return comingUserList.size();
    }

    public ArrayList<User> getComingList() {
        return comingUserList;
    }
}
