package bot.commands;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static bot.commands.CommandKeywords.ALLOCATE_COMMAND;
import static bot.commands.CommandKeywords.POLL_COMMAND;

public class CommandValidator {

    public static boolean isPollAnswer(Update update) {
        return update.hasPollAnswer();
    }

    public static boolean isPollCommand(Update update) {
        if (!update.hasMessage()) {
            return false;
        }
        Message message = update.getMessage();
        String messageText = message.getText();
        return messageText.equals(POLL_COMMAND);
    }

    public static boolean isAllocateCommand(Update update) {
        if (!update.hasMessage()) {
            return false;
        }
        Message message = update.getMessage();
        String messageText = message.getText();
        return messageText.equals(ALLOCATE_COMMAND);
    }
}
