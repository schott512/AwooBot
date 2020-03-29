package command;

import java.util.List;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

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

    }

    @Override
    public void runCommand(MessageReceivedEvent e, List<String> args){

        // Some strings to hold Channel ID and the message to echo, empty channel var
        String chID = "";
        String echo = "";
        MessageChannel ch;

        // Figure out how many args were passed, assign chID and echo message
        if (args.size()==0) { reject(e,"Lacking sufficient arguments."); }
        else if (args.size()==1) { chID = e.getChannel().getId(); echo = args.get(0);}
        else if (args.size()==2) { chID = args.get(0); echo = args.get(1);}
        else { reject(e,"Too many arguments."); }

        // Check if channel ID is a valid long. If not, default to channel echo was called from
        try { Long l = Long.parseLong(chID); ch=e.getGuild().getTextChannelById(chID);}
        catch (Exception ex) {
            chID = e.getChannel().getId();
            ch=e.getGuild().getTextChannelById(chID);
            echo = args.get(0) + " " + echo;
        }

        // Build and send message
        MessageBuilder msg = new MessageBuilder();
        msg.append(echo);
        msg.sendTo(ch).queue();
    }
}
