package command;

import java.util.List;
import core.events.CommandReceivedEvent;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;

public class ChangeNickName extends Command {

    public ChangeNickName() {

        // Initialize
        this.keyName = "nickname";
        this.aliases = new String[]{"nick", "name"};
        this.bPerms = new Permission[]{Permission.NICKNAME_MANAGE};
        this.uPerms = new Permission[]{Permission.NICKNAME_MANAGE};
        this.argCount = 2;
        this.args = "<userID>* <nickname>";
        this.helpText = "This command changes sets the nickname for <userID>. If no ID provided, sets senders nickname.";

    }

    @Override
    public void runCommand(CommandReceivedEvent cre) {

        // Grab args
        List<String> args = cre.args;

        // Member object to hold targeted user
        Member m = null;

        // If there is one argument, set the target user to the calling user
        if (args.size() == 1) { m = cre.getMember(); }

        // Otherwise grab the first argument and attempt to parse into a Member
        else {

            // Attempt to parse the user ID argument into a long, and then grab a user
            try {
                Long userID = Long.parseLong(args.get(0));
                m = cre.getGuild().getMemberById(userID);
            }
            catch (Exception e) { cre.reject("Invalid user."); return; }

        }

        m.modifyNickname(args.get(1)).queue();

    }
}
