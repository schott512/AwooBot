package command;

import java.util.Date;
import java.util.List;
import core.Configuration;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;


/**
 * Command Object for testing API stuff. Basically just echos whatever arguments or attachments it's given in an embed as well
 * as some additional info about the event that triggered the command.
 * @author Ember (schott512)
 */
public class TestArgsCommand extends Command {

    public TestArgsCommand() {

        // Initialize stuff
        this.keyName = "tapi";
        this.aliases = new String[]{"tappy","Tapi","Tappy","tap"};
        this.bPerms = new Permission[]{Permission.MESSAGE_WRITE};
        this.uPerms = new Permission[]{Permission.MESSAGE_WRITE};
        this.argCount = 100;
        this.dmCapable = true;
        this.guildCapable = false;
        this.helpText = "Takes anything and returns the arguments it parsed, attachments, emotes from the message + extra API info.";
        this.args = "Literally whatever you send it lol. Up to 100 things.";

    }

    @Override
    public void runCommand(MessageReceivedEvent e, List<String> args) {

        // Calculate ping, grab all attachments + emotes, and initialize an empty EmbedBuilder
        Long dif = (new Date().getTime()) - Date.from(e.getMessage().getTimeCreated().toInstant()).getTime();
        List<Attachment> attachments = e.getMessage().getAttachments();
        List<Emote> emojis = e.getMessage().getEmotes();
        EmbedBuilder eb = new EmbedBuilder();

        // Build embed using some basic information about this specific call
        eb.setThumbnail(Configuration.imageLink);
        eb.setColor(this.color);
        eb.setTitle("Test Args/API results for message id " + e.getMessage().getId());
        eb.setFooter("Latency: " + dif.toString() + "ms");

        // Loop through each string arg, keeping count and adding them as fields to the embed
        int count = 0;
        for (String arg : args) {

            eb.addField("Arg " + Integer.toString(count), arg, true);
            count+=1;

        }

        // Do the same as above, but for attachments and their URLs
        count = 0;
        for (Attachment attachment : attachments) {

            eb.addField("Attachment " + Integer.toString(count), attachment.getUrl(), true);
            count+=1;

        }

        // More of the same, but this time for emotes and their URLs
        count = 0;
        for (Emote emote: emojis) {

            eb.addField("Emote " + Integer.toString(count), emote.getImageUrl(), true);
            count+=1;

        }

        // Reply with embed after building
        reply(e,eb.build());

    }

}
