package ua.ali_x.telegrambot.utils;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

@Component
public class DateUtils {

    public Date getNow() {
        Instant nowUtc = Instant.now();
        ZoneId zone = ZoneId.of("Europe/Kiev");

        ZonedDateTime date = ZonedDateTime.ofInstant(nowUtc, zone);

        return Date.from(date.toInstant());
    }

}
