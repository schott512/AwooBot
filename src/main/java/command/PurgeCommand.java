package command;

import java.util.ArrayList;
import java.util.List;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PurgeCommand extends Command {

    public PurgeCommand() {

        this.keyName = "purge";
        this.aliases = new String[]{"Pu","Purge","pu"};
        this.bPerms = new Permission[]{Permission.MESSAGE_MANAGE};
        this.uPerms = new Permission[]{Permission.MESSAGE_MANAGE};
        this.argCount = 2;
        this.args = "<userID> <number>";

    }

    @Override
    public void runCommand(MessageReceivedEvent e, List<String> args) {

        int numMessages = 101;
        Member u = null;
        List<Message> m = new ArrayList<Message>();

        if (args.size() > 0) {
            try { u = e.getGuild().getMemberById(args.get(0)); } catch (Exception ex) { System.out.println(ex.getMessage()); }
        }

        if ((u == null && args.size() > 0) || args.size() > 1) {

            if (args.size() > 1) { try { numMessages = Integer.parseInt(args.get(1))+1; } catch (Exception ex) { System.out.println(ex.getMessage()); } }
            else { try { numMessages = Integer.parseInt(args.get(0))+1; } catch (Exception ex) { System.out.println(ex.getMessage()); } }
        }

        for (int n=numMessages; n > 0; n=n-100) {

            int tempNum;
            if (n > 100) {tempNum = 100;} else { tempNum = n; }
            m.addAll(m.size(), e.getChannel().getHistory().retrievePast(tempNum).complete());
        }

        if (u != null) {

            List<Message> tempList = new ArrayList<>(m);
            for (Message message : tempList) {

                if (!message.getAuthor().equals(u.getUser())) { m.remove(message); }

            }

        }

        e.getChannel().purgeMessages(m);

    }

}
