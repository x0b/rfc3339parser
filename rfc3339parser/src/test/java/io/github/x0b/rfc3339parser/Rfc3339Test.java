package io.github.x0b.rfc3339parser;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

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

    @Test
    public void parseZuluPrecise() throws ParseException {
        Date date = parse("1985-04-12T23:20:50.520000Z");
        assertEquals(482196050520L, date.getTime());
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

    @Test
    public void reducePrecision(){
        assertEquals("1996-12-19T16:39:57.123-01:00", Rfc3339.reducePrecision("1996-12-19T16:39:57.123456-01:00", '-'));
        assertEquals("1996-12-19T16:39:57.123-01:00", Rfc3339.reducePrecision("1996-12-19T16:39:57.123-01:00", '-'));
    }

    @Test
    public void parseTimeZone(){
        assertEquals(TimeZone.getTimeZone("GMT-01:00").getID(), Rfc3339.parseTimezone("1996-12-19T16:39:57.123456-01:00").getID());
        assertEquals(TimeZone.getTimeZone("UTC").getID(), Rfc3339.parseTimezone("1985-04-12T23:20:50Z").getID());
        assertEquals(TimeZone.getTimeZone("GMT+13:00").getID(), Rfc3339.parseTimezone("1996-12-19T16:39:57.123456+13:00").getID());
    }

    @Test
    public void parseCalendar() throws ParseException {
        String timeString = "1996-12-19T16:39:57.123+01:30";
        Calendar parsed = Rfc3339.parseCalendar(timeString);
        assertEquals(Rfc3339.parseTimezone(timeString), parsed.getTimeZone());
        assertEquals(Rfc3339.parse(timeString), parsed.getTime());
    }
}