package command;

import java.util.List;
import core.events.CommandReceivedEvent;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;

/**
 * Echo command object. Contains the information for echoing an input.
 * @Author Ember (schott512)
 */
public class EchoCommand extends Command {

    public EchoCommand(){

        // Initialize stuff
        this.keyName = "echo";
        this.helpText = "Says a message.";
        this.aliases = new String[]{"E","Echo","e"};
        this.bPerms = new Permission[]{Permission.MESSAGE_WRITE};
        this.uPerms = new Permission[]{Permission.MESSAGE_WRITE};
        this.argCount = 2;
        this.helpText = "This command repeats <content> in <channelID>.";
        this.args = "<channelID>\\* <content>";
        this.commandType = "message";

    }

    /**
     * @param cre The Event which triggered this command
     * @param selfReply Boolean value. If true, this command execution may respond to the calling message directly.
     * @return Message object containing the contents of the echo
     */
    @Override
    public Object runCommand(CommandReceivedEvent cre, boolean selfReply){

        // Grab args
        List<String> args = cre.args;

        // Some strings to hold Channel ID and the message to echo, empty channel var
        String chID = "";
        String echo = "";
        TextChannel ch;

        // Figure out how many args were passed, assign chID and echo message
        if (args.size()==0) { return cre.reject("Lacking sufficient arguments."); }
        else if (args.size()==1) { chID = cre.getChannel().getId(); echo = args.get(0);}
        else if (args.size()==2) { chID = args.get(0); echo = args.get(1);}
        else { if (selfReply) { return cre.reject("Too many arguments."); }}

        // Check if channel ID is a valid long. If not, default to channel echo was called from
        try { Long l = Long.parseLong(chID); ch = cre.getGuild().getTextChannelById(chID);}
        catch (Exception ex) {
            chID = cre.getChannel().getId();
            ch = cre.getGuild().getTextChannelById(chID);
            echo = args.get(0) + " " + echo;
        }

        // Build and send message, run a permissions check for the target channel
        MessageBuilder msg = new MessageBuilder();
        msg.append(echo);
        if (ch != null) {

            // Reject if permissions within guild are not proper
            String pCheck = permCheck(cre.getMember(), ch);
            if (!pCheck.equals("")) {

                return cre.reject(pCheck);

            }

            // If the perm check didn't fail, send
            ch.sendMessage(msg.build()).queue();
            return msg;

        }
        else {

            return cre.reject("Not a valid channel within this guild.");

        }
    }
}
