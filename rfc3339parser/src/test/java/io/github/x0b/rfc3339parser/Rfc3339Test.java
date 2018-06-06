package io.github.x0b.rfc3339parser;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static io.github.x0b.rfc3339parser.Rfc3339.parse;
import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class Rfc3339Test {

    @Test
    public void parseZulu() throws ParseException {
        Date date = parse("1985-04-12T23:20:50Z");
        assertEquals(482196050000L, date.getTime());
    }

    // https://tools.ietf.org/html/rfc3339#section-5.6
    // 'T' and 'Z' may be lower case
    @Test
    public void parseZuluL() throws ParseException {
        Date date = parse("1985-04-12t23:20:50z");
        assertEquals(482196050000L, date.getTime());
    }

    @Test
    public void parseZuluPrecise() throws ParseException {
        Date date = parse("1985-04-12T23:20:50.520000Z");
        assertEquals(482196050520L, date.getTime());
    }

    @Test
    public void parseZuluFractional1() throws ParseException {
        Date date = parse("1985-04-12T23:20:50.1Z");
        assertEquals(482196050100L, date.getTime());
    }

    @Test
    public void parseZuluFractional2() throws ParseException {
        Date date = parse("1985-04-12T23:20:50.12Z");
        assertEquals(482196050120L, date.getTime());
    }

    @Test
    public void parseZuluFractional3() throws ParseException {
        Date date = parse("1985-04-12T23:20:50.123Z");
        assertEquals(482196050123L, date.getTime());
    }

    @Test
    public void parseZuluFractional4() throws ParseException {
        Date date = parse("1985-04-12T23:20:50.1234Z");
        assertEquals(482196050123L, date.getTime());
    }

    @Test
    public void parseOffset() throws ParseException {
        Date date = parse("1996-12-19T16:39:57-08:00");
        assertEquals(851042397000L, date.getTime());
    }

    @Test
    public void parseOffsetPlus() throws ParseException {
        Date date = parse("1996-12-19T16:39:57+01:30");
        assertEquals(851008197000L, date.getTime());
    }

    @Test
    public void parseOffsetPrecise() throws ParseException {
        assertEquals(851017197123L, parse("1996-12-19T16:39:57.123-01:00").getTime());
        assertEquals(851017197123L, parse("1996-12-19T16:39:57.123456-01:00").getTime());
    }

    @Test
    public void parseOffsetPrecisePlus() throws ParseException {
        assertEquals(851008197123L,  parse("1996-12-19T16:39:57.123+01:30").getTime());
        assertEquals(851008197123L,  parse("1996-12-19T16:39:57.123456+01:30").getTime());
    }

    // https://tools.ietf.org/html/rfc3339#section-4.2
    // reject unqualified time
    @Test(expected = ParseException.class)
    public void parseFailUnqualified() throws ParseException {
        parse("1996-12-19T16:39:57.123");
    }

    // reject unsigned time zone
    @Test(expected = ParseException.class)
    public void parseFailUnsignedTimeZone() throws ParseException {
        parse("1996-12-19T16:39:57.123 01:00");
    }

    // reject other notations
    @Test(expected = ParseException.class)
    public void parseFailUnsupportedTimeZone() throws ParseException {
        parse("1996-12-19T16:39:57UTC+01:00");
    }

    // https://tools.ietf.org/html/rfc3339#appendix-A
    // reject time missing 'T'
    @Test(expected = ParseException.class)
    public void parseFailMissingT() throws ParseException {
        parse("1996-12-19 16:39:57.123Z");
    }

    @Test
    public void reducePrecision() throws ParseException{
        assertEquals("1996-12-19T16:39:57.123-01:00", Rfc3339.reducePrecision("1996-12-19T16:39:57.123456-01:00", '-'));
        assertEquals("1996-12-19T16:39:57.123-01:00", Rfc3339.reducePrecision("1996-12-19T16:39:57.123-01:00", '-'));
    }

    @Test(expected = ParseException.class)
    public void reducePrecisionFail() throws ParseException{
        Rfc3339.reducePrecision("1996-12-19T16:39:57.123456-01:00", ' ');
    }

    @Test
    public void parseTimeZone() throws ParseException{
        assertEquals(TimeZone.getTimeZone("GMT-01:00").getID(), Rfc3339.parseTimezone("1996-12-19T16:39:57.123456-01:00").getID());
        assertEquals(TimeZone.getTimeZone("UTC").getID(), Rfc3339.parseTimezone("1985-04-12T23:20:50Z").getID());
        assertEquals(TimeZone.getTimeZone("GMT+13:00").getID(), Rfc3339.parseTimezone("1996-12-19T16:39:57.123456+13:00").getID());
    }

    @Test(expected = ParseException.class)
    public void parseTimeZoneCustomIdFail() throws ParseException{
        Rfc3339.parseTimezone("1996-12-19T16:39:57.123456-01:60");
    }

    // reject other notations
    @Test(expected = ParseException.class)
    public void parseTimeZoneUnsupportedFail() throws ParseException {
        Rfc3339.parseTimezone("1996-12-19T16:39:57.203GMT0800");
    }

    @Test
    public void parseCalendar() throws ParseException {
        String timeString = "1996-12-19T16:39:57.123+01:30";
        Calendar parsed = Rfc3339.parseCalendar(timeString);
        assertEquals(Rfc3339.parseTimezone(timeString), parsed.getTimeZone());
        assertEquals(Rfc3339.parse(timeString), parsed.getTime());
    }

    @Test
    public void parsePrecise() throws ParseException{
        String timeString = "1996-12-19T16:39:57.123+01:30";
        BigDecimal parsed = Rfc3339.parsePrecise(timeString);
        assertEquals(new BigDecimal("851008197.123"), parsed);
    }

    // reject unknown time zone prefixes
    // this fails due to a sneaky mathematical minus U+2212
    // instead of an ascii minus
    @Test(expected = ParseException.class)
    public void parsePreciseFail() throws ParseException{
        String timeString = "1996-12-19T16:39:57.123−01:30";
        Rfc3339.parsePrecise(timeString);
    }
}