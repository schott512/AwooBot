package core.autopost;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A class for describing a single scheduled AutoPost
 */
public class AutoPost {

    public ZonedDateTime postTime;
    public String contents;
    public String guild;
    public String channel;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm z");

    /**
     * @param timestamp A string representing a time of day in 24hr format
     * @param timezone A string representing a time zone
     * @param content A string representing the message to be posted
     * @param gID A string representing a guild ID
     * @param cID A string representing a channel ID
     */
    public AutoPost(String timestamp, String timezone, String content, String gID, String cID) {

        // Terrible, terrible code for getting this time. I don't really care, as long as it works.
        ZonedDateTime temp = ZonedDateTime.now(ZoneId.of(timezone,ZoneId.SHORT_IDS));
        String month = String.valueOf(temp.getMonthValue());
        String day = String.valueOf(temp.getDayOfMonth());
        if (month.length()<2) {month = "0" + month;}
        if (day.length()<2) {day = "0" + day;}
        String dt = month + "-" + day +"-" + String.valueOf(temp.getYear()) + " " + timestamp + " " + timezone;
        postTime = ZonedDateTime.parse(dt,formatter);
        postTime = postTime.withZoneSameInstant(ZoneOffset.UTC);
        contents = content;
        guild = gID;
        channel = cID;

    }

    /**
     * @param timestamp A string representing a UTC time of day in 24hr format
     * @param content A string representing the message to be posted
     * @param gID A string representing a guild ID
     * @param cID A string representing a channel ID
     */
    public AutoPost(String timestamp, String content, String gID, String cID) {

        // Parse the timestamp we were given, assuming UTC
        String dt = timestamp + " UTC";
        postTime = ZonedDateTime.parse(dt,formatter);
        contents = content;
        guild = gID;
        channel = cID;

    }

}
