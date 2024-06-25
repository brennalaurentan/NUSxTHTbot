package bot.updates;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.polls.Poll;
import org.telegram.telegrambots.meta.api.objects.polls.PollAnswer;

public class VoteUpdate {

    Update currentUpdate = null;

    public VoteUpdate(Update update, Poll mostRecentPoll) {
    }
}
