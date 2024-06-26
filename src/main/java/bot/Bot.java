package bot;
import bot.commands.AllocateCommand;
import bot.commands.PollCommand;
import bot.utility.PollResult;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.polls.Poll;
import org.telegram.telegrambots.meta.api.objects.polls.PollAnswer;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static bot.BotConfig.botToken;
import static bot.commands.CommandValidator.isAllocateCommand;
import static bot.commands.CommandValidator.isPollAnswer;
import static bot.commands.CommandValidator.isPollCommand;

public class Bot extends TelegramLongPollingBot {

    private PollResult mostRecentPollResult = null; // stores most recent poll result data

    @Override
    public String getBotUsername() {
        return "NUSxTHTbot";
    }

    @Override
    public String getBotToken() {
        return botToken;
    } // replace with your own bot token


    @Override
    public void onUpdateReceived(Update update) {
        System.out.println("Update received");
        System.out.println(update.toString());

        if (isPollCommand(update)) {
            PollCommand newPollCommand = new PollCommand(update, this);
            Poll newPoll = newPollCommand.createNewPoll();
            mostRecentPollResult = new PollResult(newPoll);

        } else if (isPollAnswer(update)) {
            PollAnswer thisPollAnswer = update.getPollAnswer();
            mostRecentPollResult.update(thisPollAnswer);

        } else if (isAllocateCommand(update)) {
            AllocateCommand newAllocateCommand = new AllocateCommand(update);
            newAllocateCommand.allocateGroups(mostRecentPollResult);
            newAllocateCommand.sendAllocationsText(this);
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
