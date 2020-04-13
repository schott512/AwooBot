package command;

import core.events.CommandReceivedEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

/**
 * Embed editting command object. Contains the information for making alterations to a simple embed.
 * @Author Ember (schott512)
 */
public class EditEmbedCommand extends Command{

    public EditEmbedCommand() {

        // Initialize stuff
        this.keyName = "editembed";
        this.aliases = new String[]{"editsimpleembed"};
        this.bPerms = new Permission[]{Permission.MESSAGE_WRITE};
        this.uPerms = new Permission[]{Permission.MESSAGE_MANAGE};
        this.argCount = 4;
        this.helpText = "Edits a simple embed of <messageID> within <channelID>.";
        this.args = "<channelID> <messageID> <title> <description>";
        this.commandType = "message";

    }

    /**
     * @param cre The Event which triggered this command
     * @param selfReply Boolean value. If true, this command execution may respond to the calling message directly.
     * @return The original message object containing the original embed
     */
    @Override
    public Object runCommand(CommandReceivedEvent cre, boolean selfReply) {

        List<String> args = cre.args;

        // reject if wrong number of args
        if (args.size() != 4) { return cre.reject("Invalid number of parameters, 4 required."); }

        // Empty message and channel objects to hold message content and channel
        Message m;
        TextChannel ch;

        // Check if channel ID is a valid long. If not, reject
        try {

            String chID = args.get(0);
            Long l = Long.parseLong(chID);
            ch = cre.getGuild().getTextChannelById(l);
            if (ch == null) { return cre.reject("Invalid channel ID for this guild."); }

        }
        catch (Exception ex) {

            return cre.reject("Invalid channel ID for this guild.");

        }

        // Try to parse out a message ID and retrieve message.
        try {

            String mID = args.get(1);
            Long mIDLong = Long.parseLong(mID);
            m = ch.retrieveMessageById(mIDLong).submit().get();
            if (m == null) { return cre.reject("Invalid message ID for this channel."); }

        }
        catch (Exception e) {

            return cre.reject("Invalid message ID for this channel.");

        }

        // If this message was not posted by awooBot, reject
        if (!m.getAuthor().equals(cre.getJDA().getSelfUser())) { cre.reject("I can't edit an Embed I didn't write."); }

        // After successful grabbing of channel and message, rebuild embed.
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(args.get(2));
        eb.setDescription(args.get(3).replace("\\n", "\n"));
        eb.setColor(m.getEmbeds().get(0).getColor());

        if (selfReply) { m.editMessage(eb.build()).queue(); }
        return m;

    }

}
