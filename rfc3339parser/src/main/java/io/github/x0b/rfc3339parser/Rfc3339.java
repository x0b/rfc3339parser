package io.github.x0b.rfc3339parser;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.TimeZone;

/**
 * Partial implementation of RFC3339 date format
 *
 * @author (c) 2018 <a href="xob@users.noreply.github.com>xob</a>, licensed unter MIT
 * @version 1.0
 */
public class Rfc3339 {
    private static final String formatTemplateOffset = "yyyy-MM-dd'T'HH:mm:ssXXX";
    private static final String formatTemplateOffsetPrecise = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    private static final String formatTemplateZulu = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final String formatTemplateZuluPrecise = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    /**
     * Parse a RFC 3339-compliant date time string into a {@link Date} instance with millisecond
     * precision. Time zone information is respected.
     * @param timeString a string to parse
     * @return a Date with the resulting date time
     * @throws ParseException if the date format does not conform to RFC 3339
     */
    public synchronized static Date parse(String timeString) throws ParseException {
        // allow lowercase per https://tools.ietf.org/html/rfc3339#section-5.6
        timeString = timeString.toUpperCase();
        char tzStyle = timeString.charAt(19);

        // allow time-secfrac
        if('.' == tzStyle){
            tzStyle = getTimezoneStyle(timeString, 'Z', '+', '-');
            timeString = reducePrecision(timeString, tzStyle);
            tzStyle &=~ 3;
        }

        switch(tzStyle) {
            case '-':
            case '+':
                return parseInternal(timeString, formatTemplateOffset);
            case 'Z':
                return parseInternalZulu(timeString, formatTemplateZulu);
            case ',':
            case '(':
                return parseInternal(timeString, formatTemplateOffsetPrecise);
            case 'X':
                return parseInternalZulu(timeString, formatTemplateZuluPrecise);
        }
        throw new ParseException(timeString, 0);
    }

    /**
     * Parse a RFC 3339-compliant time string and get time zone information
     * @param timeString a time string
     * @return a custom {@link TimeZone} with the correct offset
     */
    public static TimeZone parseTimezone(String timeString){
        // allow lowercase per https://tools.ietf.org/html/rfc3339#section-5.6
        timeString = timeString.toUpperCase();
        char tzStyle = timeString.charAt(19);

        // allow time-secfrac
        if('.' == tzStyle){
            tzStyle = getTimezoneStyle(timeString, 'Z', '+', '-');
        }

        if(tzStyle == 'Z'){
            return TimeZone.getTimeZone("UTC");
        } else {
            String timeZoneId = "GMT" + timeString.substring(timeString.length()-6);
            return TimeZone.getTimeZone(timeZoneId);
        }
    }

    /**
     * Parse a RFC 3339-compliant date time string into a {@link Date} instance with millisecond
     * precision. Time zone information is respected and applied.
     * @param timeString a string to parse
     * @return a resulting calendar
     * @throws ParseException if the date format does not conform to RFC 3339
     */
    public synchronized static Calendar parseCalendar(String timeString) throws ParseException{
        Date date = parse(timeString);
        TimeZone timeZone = parseTimezone(timeString);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.setTimeZone(timeZone);
        return calendar;
    }

    /**
     * Parse a date string with appropriate time zone template.
     * @param timeString time string to parse
     * @return a resulting date
     */
    private synchronized static Date parseInternal(String timeString, String parseTemplate) throws ParseException{
        SimpleDateFormat timeFormat = new SimpleDateFormat(parseTemplate, Locale.getDefault());
        timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = timeFormat.parse(timeString);
        return parseOffset(date, timeString);
    }

    /**
     * Parse a date string as UTC/Zulu with appropriate formatting template.
     * @param timeString time string to parse
     * @return a resulting date
     */
    private synchronized static Date parseInternalZulu(String timeString, String parseTemplate) throws ParseException{
        SimpleDateFormat formatterZulu = new SimpleDateFormat(parseTemplate, Locale.getDefault());
        formatterZulu.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = formatterZulu.parse(timeString);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        return calendar.getTime();
    }

    /**
     * Parse a date string with appropriate time zone template.
     * @param timeString time string to parse
     * @return a resulting date
     */
    private synchronized static Date parseOffset(Date date, String timeString) throws ParseException{
        String timeZoneId = "GMT" + timeString.substring(timeString.length()-6);
        TimeZone timeZone = TimeZone.getTimeZone(timeZoneId);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.setTimeZone(timeZone);
        return calendar.getTime();
    }

    private static char getTimezoneStyle(String timeString, char... styleIds){
        for (char c: styleIds) {
            int lastIndex = timeString.lastIndexOf(c);
            if(lastIndex != -1){
                return c;
            }
        }
        throw new NoSuchElementException();
    }

    /**
     * Reduce a time string's fractional second precision to ensure correct parsing.
     * Required as per <a href="https://tools.ietf.org/html/rfc3339#section-5.6>
     *     RFC 3339 §5.6</a> as consequence of <a
     * href="https://tools.ietf.org/html/rfc2234#section-3.6">RFC 2234 §3.6</a>
     * which allows 1-n digits of decimal fractions while java's date only allows
     * @param timeString a non-null input string
     * @param delim a time zone delimiter to signal the next part (e.g. Z, +, -)
     * @return a time string with at most 3 fractional second digits
     */
    static String reducePrecision(String timeString, char delim){
        int index = timeString.lastIndexOf(delim);
        if(index - 19 > 3){
            timeString = timeString.substring(0, 23) + timeString.substring(index, timeString.length());
        }
        return timeString;
    }
}
