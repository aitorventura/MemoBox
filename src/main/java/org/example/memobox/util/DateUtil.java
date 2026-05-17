package org.example.memobox.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter ISO_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public static String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_FMT) : "—";
    }

    public static String formatDateTime(LocalDateTime dt) {
        return dt != null ? dt.format(DATETIME_FMT) : "—";
    }

    public static String toIso(LocalDateTime dt) {
        return dt != null ? dt.format(ISO_FMT) : null;
    }

    /** Devuelve texto relativo: "Hoy", "Ayer", "Hace N días", "En N días". */
    public static String relativeDate(LocalDateTime dt) {
        if (dt == null) return "—";
        LocalDate today = LocalDate.now();
        LocalDate date = dt.toLocalDate();
        long diff = today.toEpochDay() - date.toEpochDay();
        if (diff == 0)  return "Hoy";
        if (diff == 1)  return "Ayer";
        if (diff > 1)   return "Hace " + diff + " días";
        if (diff == -1) return "Mañana";
        return "En " + (-diff) + " días";
    }

    private DateUtil() {}
}
