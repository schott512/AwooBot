package command;

import core.events.CommandReceivedEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.util.List;

public class EmbedCommand extends Command {

    public EmbedCommand() {

        // Initialize stuff
        this.keyName = "embed";
        this.aliases = new String[]{"simpleembed","newembed"};
        this.bPerms = new Permission[]{Permission.MESSAGE_WRITE};
        this.uPerms = new Permission[]{Permission.MESSAGE_WRITE};
        this.argCount = 4;
        this.helpText = "Builds a simple embed in <channelID>.";
        this.args = "<channelID> #<hexColor>\\* <title> <description>";
        this.commandType = "Embed";

    }

    /**
     * @param cre The Event which triggered this command
     * @param selfReply Boolean value. If true, this command execution may respond to the calling message directly.
     * @return EmbedBuilder object containing the simple embed
     */
    @Override
    public Object runCommand(CommandReceivedEvent cre, boolean selfReply) {

        // Reject if permissions within guild are not proper
        String pCheck = permCheck(cre.getMember(), cre.getGuild().getGuildChannelById(cre.getChannel().getId()));
        if (!pCheck.equals("")) {

            return cre.reject(pCheck);

        }

        // If perm check was successful, start doing stuff
        List<String> args = cre.args;
        EmbedBuilder eb = new EmbedBuilder();

        // If too few or too many args are present, reject
        if (args.size()<3 || args.size()>4) { return cre.reject("Incorrect number of args. Check help for usage."); }

        // Variable for channelID, textChannel, and color
        String chID = args.get(0);
        TextChannel ch;
        Color color = this.color;

        // Check if channel ID is a valid long. If not, reject
        try { Long l = Long.parseLong(chID); ch = cre.getGuild().getTextChannelById(chID); }
        catch (Exception ex) {

            return cre.reject("Invalid channel ID for this guild.");

        }

        // If color provided, parse it
        if (args.get(1).startsWith("#")) {

            try { color = Color.decode(args.get(1)); }
            catch (Exception e) { cre.reject("Cannot parse provided color, please ensure #000000 format for color arg."); }
            eb.setTitle(args.get(2));
            eb.setDescription(args.get(3).replace("\\n", "\n"));

        }
        // Otherwise, no color was included. Continue using default color and push other arguments 1 spot over
        else {

            // If 4 args exist, something was misinterpreted as color. Move all inputs 1 spot over.
            if (args.size() == 4) {

                args.set(3, args.get(2) + " " + args.get(3));
                args.set(2, args.get(1));
                eb.setTitle(args.get(2));
                eb.setDescription(args.get(3).replace("\\n", "\n"));

            }
            else {

                eb.setTitle(args.get(1));
                eb.setDescription(args.get(2).replace("\\n", "\n"));

            }

        }

        // Build Embed and send
        eb.setColor(color);

        if (selfReply) { ch.sendMessage(eb.build()).queue(); }
        return eb;
    }
}
