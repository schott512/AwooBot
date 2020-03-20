package command;

import java.util.List;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class EchoCommand extends Command {

    public EchoCommand(){

        this.keyName = "echo";
        this.helpText = "Says a message.";
        this.aliases = new String[]{"E","Echo","e"};
        this.bPerms = new Permission[]{Permission.MESSAGE_WRITE};
        this.uPerms = new Permission[]{Permission.MESSAGE_WRITE};
        this.argCount = 2;
        this.args = "<channelID> <content>";

    }

    @Override
    public void runCommand(MessageReceivedEvent e, List<String> args){

        String chID = "";
        String echo = "";

        if (args.size()==0) { return; }
        if (args.size()==1) { chID = e.getChannel().getId(); echo = args.get(0);}
        if (args.size()==2) { chID = args.get(0); echo = args.get(1);}

        try { Long l = Long.parseLong(chID); }
        catch (Exception ex) {
            chID = e.getChannel().getId();
            echo = args.get(0) + " " + echo;
        }

        e.getGuild().getTextChannelById(chID).sendMessage(echo).queue();

    }

}
