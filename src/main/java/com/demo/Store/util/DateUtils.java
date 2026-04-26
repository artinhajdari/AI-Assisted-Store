package com.demo.Store.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    public static String convertToLocalizedString(Instant instant) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.of(StoreConstants.AUDIENCE_TIMEZONE));
        return formatter.format(instant);
    }

}
