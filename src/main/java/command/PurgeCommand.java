package command;

import java.util.ArrayList;
import java.util.List;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Purge command object. Contains the information for executing a message purge.
 * @Author Ember (schott512)
 */
public class PurgeCommand extends Command {

    public PurgeCommand() {

        // Initialize
        this.keyName = "purge";
        this.aliases = new String[]{"Pu","Purge","pu"};
        this.bPerms = new Permission[]{Permission.MESSAGE_MANAGE};
        this.uPerms = new Permission[]{Permission.MESSAGE_MANAGE};
        this.argCount = 2;
        this.args = "<userID>\\* <num>\\*";
        this.helpText = "This command purges all messages from <userID> in the last <num> messages, or all of the last <num> messages.";

    }

    @Override
    public void runCommand(MessageReceivedEvent e, List<String> args) {

        // Default number of messages is 101 (1 for the calling message, 100 to go back 100). Initialize member/message list
        int numMessages = 101;
        Member u = null;
        List<Message> m = new ArrayList<Message>();

        // If atleast one argument exists, attempt to fetch Member. If failed, no valid user was provided
        if (args.size() > 0) {
            try { u = e.getGuild().getMemberById(args.get(0)); } catch (Exception ex) { System.out.println(ex.getMessage()); }
        }

        // No user was retrieved and at least 1 arg exists, or if at least 2 args exist
        if ((u == null && args.size() > 0) || args.size() > 1) {

            // If at least 2 args exist, set num messages to the second arg (Member would have already been retrieved if exists)
            // Otherwise (only 1 arg exists), set num messages to the first arg (as no user would've been supplied
            if (args.size() > 1) { try { numMessages = Integer.parseInt(args.get(1))+1; } catch (Exception ex) { System.out.println(ex.getMessage()); } }
            else { try { numMessages = Integer.parseInt(args.get(0))+1; } catch (Exception ex) { System.out.println(ex.getMessage()); } }
        }

        // Grab Messages in groups of 100 and add to message list (cannot grab more than 100 at a time through JDA)
        for (int n=numMessages; n > 0; n=n-100) {

            // If more than 100 are needed, grab 100 this iteration and append all to message list
            int tempNum;
            if (n > 100) {tempNum = 100;} else { tempNum = n; }
            m.addAll(m.size(), e.getChannel().getHistory().retrievePast(tempNum).complete());
        }

        // if Member not null, search through message list and remove any that don't belong to that user
        if (u != null) {

            List<Message> tempList = new ArrayList<>(m);
            for (Message message : tempList) {

                if (!message.getAuthor().equals(u.getUser())) { m.remove(message); }

            }
        }

        // Purge messages in message list
        e.getChannel().purgeMessages(m);

    }
}
