package core.autopost;

import core.AwooBot;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

/**
 * A thread class for managing the autopost functions of awoobot. Everything about this is inefficient and terrible,
 * but too bad!
 * @author Ember
 */
public class AutoPostManager extends Thread {

    // List of autopost objects to act as a cache for storing upcoming autoposts
    public List<AutoPost> posts = new ArrayList<>();

    // Act as a lock, if the AutopostManager needs to alter its data, this lets the bot know it can't currently alter anything
    public static boolean lock = false;

    // JDA
    private JDA jda;

    // A time representing when autoposts were last refreshed
    public static ZonedDateTime refreshTime;

    public AutoPostManager(JDA in) {

        jda = in;

    }

    /**
     * The run method, the brains of the operation. Manages the checking of posts and refreshing of the list when needed
     */
    @Override
    public void run() {

        // Fetch posts initially
        refreshAP();

        // Keep this thread running repeatedly.
        while (true) {

            checkPosts();

            // if need be, refresh posts
            ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
            if (now.isAfter(refreshTime.plusMinutes(15))) {
                refreshAP();
            }

            // Wait 30 seconds
            try { Thread.sleep(30000); }
            catch (Exception e) { e.printStackTrace(); }

        }


    }

    /**
     * Fetches upcoming autoposts from the DB, resets the posts list and populates.
      */
    private void refreshAP() {

        lock = true;

        // Reinitialize posts
        posts = new ArrayList<>();
        posts = AwooBot.dbManager.fetchUpcomingAP();

        // Update last refresh time
        refreshTime = ZonedDateTime.now(ZoneId.of("UTC"));

        lock = false;

    }

    /**
     * Check to see if any posts in the list need sent at this moment
     */
    private void checkPosts() {

        // Fetch time now (in UTC)
        ZonedDateTime zdt = ZonedDateTime.now(ZoneId.of("UTC"));

        // List of posts needing posted
        List<AutoPost> tempList = new ArrayList<>();

        // Cycle through posts and compare hours/minutes
        for (AutoPost post : posts) {

            if ((post.postTime.getHour() == zdt.getHour()) && post.postTime.getMinute() <= zdt.getMinute()) {
                tempList.add(post);
            }

        }

        // Send any messages identified (set the lock)
        for (AutoPost a : tempList) {
            lock = true;
            sendAP(a);
        }

        // Unlock
        lock = false;
    }

    /**
     * Sends a single AutoPost message and removes it from list to avoid double-send
     * @param ap
     */
    private void sendAP(AutoPost ap){

        // Grab the guild in question
        Guild g = jda.getGuildById(ap.guild);
        TextChannel c = g.getTextChannelById(ap.channel);
        c.sendMessage(ap.contents).queue();

        // Remove the ap from the list
        posts.remove(ap);

    }

    // Called by AutoPost command to add AP's to the list upon creation
    public void newAP(AutoPost ap) {

        posts.add(ap);

    }
}
