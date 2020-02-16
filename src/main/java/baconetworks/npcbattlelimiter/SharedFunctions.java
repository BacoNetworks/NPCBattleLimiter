package baconetworks.npcbattlelimiter;

import org.spongepowered.api.text.Text;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SharedFunctions {
    public static long Seconds() {
        LocalDateTime localNow = LocalDateTime.now();
        ZonedDateTime zonedNow = ZonedDateTime.of(localNow, ZoneId.systemDefault());
        ZonedDateTime zonedNext = zonedNow.withHour(12).withMinute(0).withSecond(0);

        if (zonedNow.compareTo(zonedNext) > 0) {
            zonedNext = zonedNext.plusDays(1);
        }

        return Duration.between(zonedNow, zonedNext).getSeconds();
    }

    public static String GetTime() {
        long InitialDelay = Seconds();

        int day = (int) TimeUnit.SECONDS.toDays(InitialDelay);
        int hours = (int) (TimeUnit.SECONDS.toHours(InitialDelay) - (day * 24));
        int minutes = (int) (TimeUnit.SECONDS.toMinutes(InitialDelay) - (TimeUnit.SECONDS.toHours(InitialDelay) * 60));
        int seconds = (int) (TimeUnit.SECONDS.toSeconds(InitialDelay) - (TimeUnit.SECONDS.toMinutes(InitialDelay) * 60));

        List<Text> texts = new ArrayList<>();

        //Blank line
        texts.add(Text.builder()
                .append(Text.builder()
                        .append(Text.of(""))
                        .build())
                .build());

        //We append the actual time.
        String time = " {h}{m}{s}.";
        String displayedtime = time.replaceAll("\\{h}", hours + " hour" + (hours != 1 ? "s " : " "))
                .replaceAll("\\{m}", minutes + " minute" + (minutes != 1 ? "s " : " "))
                .replaceAll("\\{s}", seconds + " second" + (seconds != 1 ? "s" : ""));
        if (hours <= 0) {
            displayedtime = time.replaceAll("\\{h}", "")
                    .replaceAll("\\{m}", minutes + " minute" + (minutes != 1 ? "s " : " "))
                    .replaceAll("\\{s}", seconds + " second" + (seconds != 1 ? "s" : ""));
        }
        if (minutes <= 0) {
            displayedtime = time.replaceAll("\\{h}", "")
                    .replaceAll("\\{m}", "")
                    .replaceAll("\\{s}", seconds + " second" + (seconds != 1 ? "s" : ""));
        }
        return displayedtime;
    }
}
