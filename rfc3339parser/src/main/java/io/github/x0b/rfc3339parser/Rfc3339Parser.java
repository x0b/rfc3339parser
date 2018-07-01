package io.github.x0b.rfc3339parser;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public interface Rfc3339Parser {
    Date parse(String timeString) throws ParseException;

    /**
     * Parse a RFC 3339-compliant time string and get time zone information. Since the time string only
     * contains offset information no daylight saving time rules are applied.
     * @param timeString a time string
     * @return a custom {@link TimeZone} with the correct offset. Returns a UTC time zone with ID
     * {@code Etc/Unknown} as per <a href="https://tools.ietf.org/html/rfc3339#section-4.3">§4.3</a>
     * to indicate an unknown time zone.
     * @throws Rfc3339Exception if timeString does not contain a RFC 3339 valid time zone
     */
    TimeZone parseTimezone(String timeString) throws ParseException;
    Calendar parseCalendar(String timeString) throws ParseException;
    BigDecimal parsePrecise(String timeString) throws ParseException;
}
