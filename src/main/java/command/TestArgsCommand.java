package command;

import java.util.List;
import core.Configuration;
import core.events.CommandReceivedEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Message.Attachment;



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
        this.guildCapable = true;
        this.helpText = "Takes anything and returns the arguments it parsed, attachments, emotes from the message + extra API info.";
        this.args = "<up to 100 args>\\*";
        this.commandType = "embed";

    }

    /**
     * @param cre The Event which triggered this command
     * @param selfReply Boolean value. If true, this command execution may respond to the calling message directly.
     * @return EmbedBuilder containing the data of the arg test
     */
    @Override
    public Object runCommand(CommandReceivedEvent cre, boolean selfReply) {

        // Grab args
        List<String> args = cre.args;

        // Grab all attachments + emotes, and initialize an empty EmbedBuilder
        List<Attachment> attachments = cre.getMessage().getAttachments();
        List<Emote> emojis = cre.getMessage().getEmotes();
        EmbedBuilder eb = new EmbedBuilder();

        // Build embed using some basic information about this specific call
        eb.setThumbnail(Configuration.getImageLink());
        eb.setColor(this.color);
        eb.setTitle("Test Args/API results for message id " + cre.getMessage().getId());
        eb.setFooter("Latency: " + cre.getJDA().getGatewayPing() + "ms");
        eb.addField("Message ID", cre.getMessageId(),true);

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
        if (selfReply) { cre.reply(eb.build(),false); }
        return eb;

    }

}
