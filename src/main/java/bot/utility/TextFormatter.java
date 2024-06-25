package bot.utility;

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
        return finalFormattedMessage;
    }
}
