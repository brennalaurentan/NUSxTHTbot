package bot.tools;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

public class DateRetriever {
    public static String getNextThursday() {
        LocalDate today = LocalDate.now();
        LocalDate closestThursday = today.with(TemporalAdjusters.next(DayOfWeek.THURSDAY));
        return closestThursday.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"));
    }
}
