package io.neskdev.api.utils.math;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    public static String getFormatedNow() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public static String unixToTime(int unix) {
        return Instant.ofEpochSecond(unix)
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }

    public static String unixToTime(long unix) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(unix), ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }

    public static String unixToCalenderTime(int unix) {
        return Instant.ofEpochSecond(unix)
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public static int unixToHours(int unix) {
        return (unix / 60) / 60;
    }

    public static String unixToStringHours(int unix) {
        return String.valueOf(unixToHours(unix));
    }

    public static String getFormatTimeDifference(int timeDifference) {
        long seconds = (timeDifference) % 60;
        long minutes = (timeDifference / 60) % 60;
        long hours = (timeDifference / (60 * 60));

        StringBuilder formattedTime = new StringBuilder();

        if (hours > 0) {
            formattedTime.append(hours).append(" heure");
            if (hours > 1) {
                formattedTime.append("s");
            }
            formattedTime.append(", ");
        }

        formattedTime.append(minutes).append(" minute");
        if (minutes > 1) {
            formattedTime.append("s");
        }

        formattedTime.append(" et ").append(seconds).append(" seconde");
        if (seconds > 1) {
            formattedTime.append("s");
        }

        return formattedTime.toString();
    }

    public static String secToTime(int sec) {
        final int seconds = sec % 60;
        int minutes = sec / 60;
        if (minutes >= 60) {
            final int hours = minutes / 60;
            minutes %= 60;
            if (hours >= 24) {
                final int days = hours / 24;
                return String.format("%d jour(s) %02d:%02d:%02d", days, hours % 24, minutes, seconds);
            }
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }
        return String.format("00:%02d:%02d", minutes, seconds);
    }

    public static String secToTime(Long sec) {
        final long seconds = sec % 60;
        long minutes = sec / 60;
        if (minutes >= 60) {
            final long hours = minutes / 60;
            minutes %= 60;
            if (hours >= 24) {
                final long days = hours / 24;
                return String.format("%d jour(s) %02d:%02d:%02d", days, hours % 24, minutes, seconds);
            }
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }
        return String.format("00:%02d:%02d", minutes, seconds);
    }

    public static int secondToTickMinute(int m) {
        return (60 * 20) * m;
    }
}
