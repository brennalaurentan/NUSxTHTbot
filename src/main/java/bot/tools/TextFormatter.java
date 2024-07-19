package bot.tools;

import bot.utility.Vote;

import java.util.ArrayList;

public class TextFormatter {
    public static String getFormattedAllocateText(
            String header, ArrayList<String> firstGroup, ArrayList<String> secondGroup) {
        String headerText = header;
        StringBuilder firstGroupText = new StringBuilder("Jovan");
        for (String username : firstGroup) {
            firstGroupText.append("\n- @").append(username);
        }
        StringBuilder secondGroupText = new StringBuilder("Dilon");
        for (String username : secondGroup) {
            secondGroupText.append("\n- @").append(username);
        }
        String finalFormattedMessage =
                headerText + "\n"
                        + firstGroupText + "\n\n"
                        + secondGroupText;
        System.out.println(finalFormattedMessage);
        return finalFormattedMessage;
    }

    public static String getFormattedVoteUpdateText(String username, Vote voteType) {
        String voteStatus = "undetermined";
        switch (voteType) {
        case Coming:
            voteStatus = "voted COMING";
            break;
        case NotComing:
            voteStatus = "voted NOT COMING";
            break;
        case Retract:
            voteStatus = "RETRACTED VOTE";
            break;
        }
        String finalFormattedMessage =
                "POLL ANSWER DETECTED: " + username + " " + voteStatus;
        return finalFormattedMessage;
    }
}
