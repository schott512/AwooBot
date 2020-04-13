package command;

import core.Configuration;
import core.events.CommandReceivedEvent;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;

import java.awt.Color;

/**
 * Object representing a basic command.
 * @Author Ember (schott512)
 */
public abstract class Command {

    // The name of the command
    protected String keyName="null";

    // The help string of a command
    protected String helpText ="No help for this command is available.";

    // The arguments of a command, used to generate usage details for "help". Format <arg1> <arg2>
    protected String args = "";

    // Aliases for this command
    protected String[] aliases = new String[0];

    // Whether or not a command is able to be used inside of a dm. Default false.
    protected boolean dmCapable = false;

    // Whether or not a command is able to be used inside of a guild. Default true.
    protected boolean guildCapable = true;

    // Permissions a user requires to run this command (if in a server)
    protected Permission[] uPerms = new Permission[0];

    // Permissions required by the bot to run this command
    protected Permission[] bPerms = new Permission[0];

    // Can this command only be used by those with a moderator role?
    protected boolean modOnly = false;

    // Array of sub commands to be run before, during, or after execution
    protected Command[] subCommands = new Command[0];

    // Color for any Embeds this command builds (default to AwooBot's color)
    protected Color color = Configuration.color;

    // Maximum number of expected arguments
    protected int argCount = 0;

    /**
     * Command type. Available types are [message, charseq, embed, modify]. Message commands respond with Message Object.
     * CharSequence commands respond with CharSequences(Strings being a subtype of CharSequence), Embed commands respond
     * with an EmbedBuilder Object, Modify commands return true/false values on whether or not they were able to submit a
     * request to modify the state of a guild and/or its users
     */
    protected String commandType = "";

    /** An abstract function to run command. This will be overridden by specific implementations of the class.
     * @param cre The Event which triggered this command
     * @param selfReply Boolean value. If true, this command execution may respond to the calling message directly.
     * @return An object. Whatever output (if any) the command created.
     */
    protected abstract Object runCommand(CommandReceivedEvent cre, boolean selfReply);

    /**
     * Function to check whether the command may be run by the calling user in the calling guild/dm and then
     * executes the command by calling the runCommand() function.
     * @param cre The Event which triggered this execution
     * @param selfReply Boolean value. If true, this command execution may respond to the calling message directly.
     *                  If not, it may only return. Even if true, and rejection reasons will be replied.
     * @return An object. Whatever output (if any) the command created.
     */
    public Object execute(CommandReceivedEvent cre, boolean selfReply) {

        // Check if the source of the command is a guild
        if (cre.isFromGuild()) {

            // Reject if this command can't be used in a guild
            if (!guildCapable) { cre.reject("Command may not be used in guilds."); return null; }


            // Reject if permissions within guild are not proper
            String pCheck = permCheck(cre.getMember(), cre.getGuild().getGuildChannelById(cre.getChannel().getId()));
            if (!pCheck.equals("")) {

                cre.reject(pCheck);
                return null;

            }

        }
        else {

            // Reject if not usable in DMs
            if (!dmCapable) { cre.reject("This command is not usable from DMs."); return null; }

        }

        // Run this command if it hasn't been rejected
        return this.runCommand(cre, selfReply);

    }

    /**
     * Function to specifically check permissions of both user and bot in a target channel, as well as the white and
     * blacklist to determine if a user may run a command
     * @param m A member for whom to perform a permissions check (in addition to basic bot permissions)
     * @param gc A guild channel in which to perform a permissions check
     * @return A string. Empty if no issues, otherwise contains a rejection reason
     */
    public String permCheck(Member m, GuildChannel gc) {

        // First - Does the bot have permission within this channel? Failure is an instant rejection.
        if (!gc.getGuild().getSelfMember().hasPermission(gc,bPerms)){ return "I lack required permissions for this action."; }

        // Next, does the user have permissions for this channel?
        if (!m.hasPermission(gc,uPerms)) { return "User lacks permissions required for this actions."; }

        return "";

    }

    /**
     * Add an alias to the existing list of aliases.
     * @param a A list of strings representing new aliases to add to the existing list.
     */
    public void addAliases(String[] a) {

        String[] aliasesUpdate = new String[aliases.length + a.length];

        // Copy existing aliases
        for (int i = 0; i<aliases.length; i++) {

            aliasesUpdate[i] = aliases[i];

        }

        // Add new aliases
        for (int i = aliases.length; i < aliasesUpdate.length; i++) {

            aliasesUpdate[i] = a[i-aliases.length];

        }
    }

    /**
     * Checks whether the input is a reference to this command based on if the string matches the name of the command
     * or any of its known aliases.
     * @param s A string input to be checked
     * @return A boolean. True if this command object is referenced by input string
     */
    public boolean referencedBy(String s) {

        // If input matches this commands name, return true
        if (keyName.equals(s)) {
            return true;
        }

        // Loop through aliases, return true if one of them matches
        for (String alias : aliases) {

            if (alias.equals(s)) {
                return true;
            }
        }

        // When all else fails, return false!
        return false;

    }

    /**
     * Builds a short string demonstrating usage of the command
     * @return String which contains expected arguments and command format
     */
    public String getUsage() {

        StringBuilder sb = new StringBuilder();
        sb.append("{prefix}");
        sb.append(keyName);
        sb.append(" ");
        sb.append(args);

        return sb.toString();

    }

}