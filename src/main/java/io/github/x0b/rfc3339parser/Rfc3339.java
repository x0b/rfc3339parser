package io.github.x0b.rfc3339parser;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.TimeZone;

/**
 * Partial implementation of RFC3339 date format
 *
 * @author (c) 2018 <a href="xob@users.noreply.github.com>xob</a>, licensed unter MIT
 * @version 0.1
 */
public class Rfc3339 {
    private static final String formatTemplateOffset = "yyyy-MM-dd'T'HH:mm:ssXXX";
    private static final String formatTemplateOffsetPrecise = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    private static final String formatTemplateZulu = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final String formatTemplateZuluPrecise = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public synchronized static Date parse(String timeString) throws ParseException {
        // allow lowercase per https://tools.ietf.org/html/rfc3339#section-5.6
        timeString = timeString.toUpperCase();
        char tzStyle = timeString.charAt(19);

        // allow time-secfrac for Zulu
        if('.' == tzStyle){
            tzStyle = getTimezoneStyle(timeString, 'Z', '+', '-');
            timeString = reducePrecision(timeString, tzStyle);
            tzStyle &=~ 3;
        }

        switch(tzStyle) {
            case '-':
            case '+':
                return parseZulu(timeString, formatTemplateOffset);
            case 'Z':
                return parseZulu(timeString, formatTemplateZulu);
            case ',':
            case '(':
                return parseZulu(timeString, formatTemplateOffsetPrecise);
            case 'X':
                return parseZulu(timeString, formatTemplateZuluPrecise);
        }
        throw new ParseException(timeString, 0);
    }

    /**
     * Parse a date string with zulu time zone and second precision
     * @param timeString time string to parse
     * @return a resulting date
     */
    private synchronized static Date parseZulu(String timeString, String parseTemplate) throws ParseException{
        SimpleDateFormat formatterZulu = new SimpleDateFormat(parseTemplate, Locale.getDefault());
        formatterZulu.setTimeZone(TimeZone.getTimeZone("UTC"));
        return formatterZulu.parse(timeString);
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

    static String reducePrecision(String timeString, char delim){
        int index = timeString.lastIndexOf(delim);
        if(index - 19 > 3){
            timeString = timeString.substring(0, 23) + timeString.substring(index, timeString.length());
        }
        return timeString;
    }
}
