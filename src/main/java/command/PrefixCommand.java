package command;

import core.AwooBot;
import core.events.CommandReceivedEvent;
import net.dv8tion.jda.api.Permission;

import java.util.List;

/**
 * Prefix updating command. Updates the custom prefix for the guild.
 * @author Ember (schott512)
 */
public class PrefixCommand extends Command {

    public PrefixCommand() {

        // Initialize stuff
        this.keyName = "prefix";
        this.aliases = new String[]{"pref"};
        this.bPerms = new Permission[]{};
        this.uPerms = new Permission[]{Permission.MANAGE_SERVER};
        this.argCount = 1;
        this.helpText = "Edits the current set guild prefix.";
        this.args = "<prefix>";
        this.commandType = "modify";

    }

    /**
     * @param cre The Event which triggered this command
     * @param selfReply Boolean value. If true, this command execution may respond to the calling message directly.
     * @return Boolean, whether or not the entry was updated
     */
    @Override
    public Object runCommand(CommandReceivedEvent cre, boolean selfReply) {

        // Grab guild id and args
        String guildID = cre.getGuild().getId();
        List<String> args = cre.args;
        if (args.size() != 1) { cre.reject("Improper number of args."); return false; }

        AwooBot.dbManager.updateSetting(guildID,"prefix",args.get(0),true);

        return true;

    }

}
