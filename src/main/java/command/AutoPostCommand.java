package command;

import core.AwooBot;
import core.autopost.AutoPost;
import core.events.CommandReceivedEvent;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ReactRole configuration command. Creates/Removes autopost entries for a guild.
 * @author Ember (schott512)
 */
public class AutoPostCommand extends Command {

    public AutoPostCommand() {

        // Initialize stuff
        this.keyName = "autopost";
        this.aliases = new String[]{"ap"};
        this.argCount = 4;
        this.helpText = "Edits the AutoPost configuration. Add requires time and content," +
                " but view requires neither. Autoposts are posts which are to be posted daily at a certain time."
                + " Removing AutoPosts requires their ID (can be gotten from view command). Time arguments must be formatted 24hr 00:00 and include a timezone. Ex. 00:00EST";
        this.args = "<add|remove|view> <channel|autopostID> <time>\\* <content>\\*";
        this.commandType = "modify";

    }

    /**
     * @param cre The Event which triggered this command
     * @param selfReply Boolean value. If true, this command execution may respond to the calling message directly.
     * @return Boolean value. If true, the data was submitted to the DB (or retrieved in the case of view)
     */
    @Override
    public Object runCommand(CommandReceivedEvent cre, boolean selfReply) {

        List<String> args = cre.args;
        String act = args.get(0);

        // Copy and remove the add|remove|view args
        List<String> newArgs = new ArrayList<>(args);
        newArgs.remove(0);

        // Call the appropriate function
        switch (act) {
            case ("add") : {

                // Is the number of args correct?
                if (newArgs.size() != 3) { cre.reject("Insufficient arguments to create AP."); return false; }
                return addAutoPost(cre, newArgs.get(0), newArgs.get(1), newArgs.get(2));

            }
            case ("remove") : {

                // Is the number of args correct?
                if (newArgs.size() != 1) { cre.reject("Please provide the autopost ID with the remove call and nothing else."); return false; }
                return removeAutoPost(cre, newArgs.get(0));

            }

            case ("view") : {

                // Is the number of args correct?
                if (newArgs.size() < 1) { cre.reject("Provide channel ID and nothing else to view a channels autoposts."); return false; }
                return viewAutoPosts(cre, newArgs.get(0));

            }

            default: { cre.reject("Invalid option, please select add|remove|view."); return false; }

        }
    }

    /**
     * Function that handles adding an individual entry to the AutoPosts.
     * @param cre Calling CommandReceivedEvent
     * @param channel Id of the channel to post to
     * @param time String formatted 00:00 with a 3 digit timezone code at the end. 24 hour representation.
     * @param content String content of the message to be posted
     * @return whether or not the DB was updated
     */
    public boolean addAutoPost(CommandReceivedEvent cre, String channel, String time, String content) {

        // Create AP Object
        String timestamp = time.substring(0,5);
        String timezone = time.substring(5);
        AutoPost ap = new AutoPost(timestamp,timezone,content,cre.getGuild().getId(), channel);

        // Push it to DB, and also make sure the APManager is aware of it
        AwooBot.dbManager.addAutoPost(ap);
        AwooBot.apManager.newAP(ap);

        return true;

    }

    /**
     * Function that handles removing an individual entry from the reactroles
     * @param cre Calling CommandReceivedEvent
     * @param apID ID of an autopost, you can view IDs of Autoposts tied to a channel with 'view'
     * @return whether or not the DB was updated
     */
    public boolean removeAutoPost(CommandReceivedEvent cre, String apID) {

        // If no AutoPost exists with that ID, reject
        Map<String, Object> singleResult = AwooBot.dbManager.getAutoPost(apID);
        if (singleResult == null) { cre.reject("An autopost with that ID does not exist."); return false; }

        // If AutoPost is not part of this guild, reject
        if (!cre.getGuild().getId().contains(singleResult.get("guild_id").toString())) { cre.reject( "That autopost isn't in this guild..."); return false; }

        AwooBot.dbManager.removeAutoPost(apID);

        return true;

    }

    /**
     * Function that handles viewing entries from the autoposts for a specific channel
     * @param cre Calling CommandReceivedEvent
     * @param cID Id of the channel to check
     * @return whether or not view data was fetched
     */
    public boolean viewAutoPosts(CommandReceivedEvent cre, String cID) {

        // Grab DB entries for this message
        List<Map<String, Object>> apSet = AwooBot.dbManager.getAutoPostsByChannelID(cID);

        // Setup an EmbedBuilder
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("AutoPosts Configured for " + cID);

        // Build message which will become the description
        StringBuilder sb = new StringBuilder();

        for (Map<String, Object> ap : apSet) {

            // Display the ID, time, and Message Content
            sb.append("ID: " + ap.get("id").toString() + "  Content: " + ap.get("content").toString() + "  Timestamp (UTC): "
                    + ap.get("post_time").toString().substring(10) + "\n");

        }

        eb.setColor(this.color);
        eb.setDescription(sb.toString());
        cre.reply(eb.build(), false);

        return true;

    }

}
