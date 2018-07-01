package io.github.x0b.rfc3339parser;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Rfc3339Strict extends Rfc3339Lenient {

    /**
     * Note: Only checks format, not leap years, seconds or month lengths
     */
    private static final String RG_FORMAT_STRING = "([0-9]{4})-(0[1-9]|1[0-2])-([0-2][0-9]|3[0-1])[tT]([0-1][0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9]|60)([zZ]|[+\\-]([0-1][0-9]|2[0-3]):([0-5][0-9])|\\.(\\d+)([zZ]|[+\\-]([0-1][0-9]|2[0-3]):([0-5][0-9])))";
    private static final Pattern RG_PATTERN = Pattern.compile(RG_FORMAT_STRING);
    private static final int[] MONTH_LENGTH = {29, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    private static final int GTYPE = 6;
    private static final int TZ_HOUR = 8;
    private static final int TZ_MINUTE = 9;

    private static final int TSTYPE_Z = 0;
    private static final int TSTYPE_ZF = -2;
    private static final int FDEFAULT = -2;

    @Override
    public synchronized Date parse(String timeString) throws ParseException {
        return parseCalendar(timeString).getTime();
    }

    @Override
    public TimeZone parseTimezone(String timeString) throws ParseException {
        throwOnInvalid(timeString);
        return super.parseTimezone(timeString);
    }

    @Override
    public synchronized Calendar parseCalendar(String timeString) throws ParseException {
        int[] parseResult = parseInternal(timeString);

        if (null == parseResult) {
            throw new Rfc3339Exception("Invalid Time String " + timeString);
        }

        Calendar calendar = Calendar.getInstance();
        if (parseResult[GTYPE] == TSTYPE_Z || parseResult[GTYPE] == TSTYPE_ZF) {
            calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        } else {
            String timeZoneId = "GMT" + timeString.substring(timeString.length() - 6);
            TimeZone timeZone = TimeZone.getTimeZone(timeZoneId);
            calendar.setTimeZone(timeZone);
        }

        calendar.set(Calendar.YEAR, parseResult[0]);
        calendar.set(Calendar.MONTH, parseResult[1] - 1);
        calendar.set(Calendar.DAY_OF_MONTH, parseResult[2]);
        calendar.set(Calendar.HOUR_OF_DAY, parseResult[3]);
        calendar.set(Calendar.MINUTE, parseResult[4]);
        calendar.set(Calendar.SECOND, parseResult[5]);

        if (parseResult[GTYPE] <= TSTYPE_ZF) {
            calendar.set(Calendar.MILLISECOND, parseResult[7] / 1000000);
        } else {
            calendar.set(Calendar.MILLISECOND, 0);
        }
        return calendar;
    }

    @Override
    public BigDecimal parsePrecise(String timeString) throws ParseException {
        throwOnInvalid(timeString);
        return super.parsePrecise(timeString);
    }

    public boolean isValid(String timeString) {
        // invalid by length
        if (null == timeString || timeString.length() < 20) {
            return false;
        }

        return parseInternal(timeString) != null;
    }

    private int[] parseInternal(String timeString) {
        Matcher matcher = RG_PATTERN.matcher(timeString);
        if (matcher.find()) {
            // invalid by format
            if (!timeString.equals(matcher.group(0))) {
                return null;
            }

            int year = Integer.parseInt(matcher.group(1));
            int month = Integer.parseInt(matcher.group(2));
            int day = Integer.parseInt(matcher.group(3));

            // catches months that should be shorter than 31 but aren't. Leap year aware.
            // invalid month length
            if (month == 2 && year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) {
                if (day > MONTH_LENGTH[0]) {
                    return null;
                }
            } else if (day > MONTH_LENGTH[month]) {
                return null;
            }

            int hour = Integer.parseInt(matcher.group(4));
            int minute = Integer.parseInt(matcher.group(5));
            int second = Integer.parseInt(matcher.group(6));
            switch (matcher.group(7).toUpperCase().charAt(0)) {
                case 'Z':
                    return new int[]{year, month, day, hour, minute, second, TSTYPE_Z};
                case '+':
                    int tzpHour = Integer.parseInt(matcher.group(8));
                    int tzpMinute = Integer.parseInt(matcher.group(9));
                    return new int[]{year, month, day, hour, minute, second, 1, FDEFAULT, tzpHour, tzpMinute};
                case '-':
                    int tzmHour = Integer.parseInt(matcher.group(8));
                    int tzmMinute = Integer.parseInt(matcher.group(9));
                    return new int[]{year, month, day, hour, minute, second, -1, FDEFAULT, tzmHour, tzmMinute};
                case '.':
                    int[] nonFractional = new int[]{year, month, day, hour, minute, second, -2, FDEFAULT, -2, -2};
                    return parseInternalFractional(matcher, nonFractional);
            }

            return null;
        }
        return null;
    }

    private int[] parseInternalFractional(Matcher matcher, int[] nonFractional) {
        switch (matcher.group(11).toUpperCase().charAt(0)) {
            case 'Z':
                nonFractional[6] = TSTYPE_ZF;
                break;
            case '+':
                nonFractional[6] = -3;
                nonFractional[8] = Integer.parseInt(matcher.group(12));
                nonFractional[9] = Integer.parseInt(matcher.group(13));
                break;
            case '-':
                nonFractional[6] = -4;
                nonFractional[8] = Integer.parseInt(matcher.group(12));
                nonFractional[9] = Integer.parseInt(matcher.group(13));
                break;
            default:
                return null;
        }

        String fraction = matcher.group(10);
        char[] fracChars = fraction.toCharArray();
        char[] expanded = new char[9];
        Arrays.fill(expanded, '0');
        for (int i = 0; i <= 9 && i < fraction.length(); i++) {
            expanded[i] = fracChars[i];
        }
        nonFractional[7] = Integer.parseInt(new String(expanded));

        return nonFractional;
    }

    private void throwOnInvalid(String timeString) throws ParseException {
        if (!isValid(timeString))
            throw new Rfc3339Exception("Invalid time String: " + timeString);
    }

}
