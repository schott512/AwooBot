package command;

import java.util.List;
import core.events.CommandReceivedEvent;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.exceptions.HierarchyException;

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
        this.commandType = "modify";

    }

    /**
     * @param cre The Event which triggered this command
     * @param selfReply Boolean value. If true, this command execution may respond to the calling message directly.
     * @return Boolean value. True if the request to change nick was made. False otherwise.
     */
    @Override
    public Object runCommand(CommandReceivedEvent cre, boolean selfReply) {

        // Grab args
        List<String> args = cre.args;

        // String to hold new nickname
        String nick = "";

        // Member object to hold targeted user
        Member m = null;

        // If there is one argument, set the target user to the calling user
        if (args.size() == 1) { m = cre.getMember(); nick = args.get(0); }

        // Otherwise grab the first argument and attempt to parse into a Member
        else {

            nick = args.get(1);

            // Attempt to parse the user ID argument into a long, and then grab a user
            try {
                Long userID = Long.parseLong(args.get(0));
                m = cre.getGuild().getMemberById(userID);
            }
            catch (Exception e) { if (selfReply) { cre.reject("Invalid user."); } return false; }

        }

        try {
            m.modifyNickname(nick).queue();
        }
        catch (HierarchyException he) { cre.reject("That user is above me in the hierarchy."); return false; }
        return true;

    }
}
