package command;

import core.Configuration;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.Color;
import java.util.List;

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

    // An abstract function to run command. This will be overridden by specific implementations of the class.
    protected abstract void runCommand(MessageReceivedEvent e, List<String> args);

    /**
     * Function to check whether the command may be run by the calling user in the calling guild/dm and then
     * executes the command by calling the runCommand() function.
     * @param mre The MessageEvent which triggered this command
     */
    public void execute(MessageReceivedEvent mre, List<String> args) {

        // Check if the source of the command is a guild
        if (mre.isFromGuild()) {

            // Reject if this command can't be used in a guild
            if (!guildCapable) { reject(mre,"Command may not be used in guilds."); return; }

            // Reject if permissions within guild are not proper
            if (!mre.getGuild().getSelfMember().hasPermission(mre.getTextChannel(),bPerms)){ reject(mre,"I lack required permissions for this action."); return; }
            if (!mre.getMember().hasPermission(mre.getTextChannel(),uPerms)) { reject(mre,"User lacks required permissions."); return; }

        }
        else {

            // Reject if not usable in DMs
            if (!dmCapable) { reject(mre, "This command is not usable from DMs."); return; }

        }

        // Run this command if it hasn't been rejected
        this.runCommand(mre, args);

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

    public void reject(MessageReceivedEvent mre, String reason) {

        // Build a generic error message and reply with it
        MessageBuilder msgBuild = new MessageBuilder();
        msgBuild.append(":x: ");
        msgBuild.append("Failed to execute command. ");
        msgBuild.append(reason);
        msgBuild.append(" :x:");
        reply(mre,msgBuild.build());

    }

    /**
     * Function that replies to a message with a CharSequence.
     * @param mre MessageReceivedEvent to respond to
     * @param content CharSequence containing content of the reply
     */
    protected void reply(MessageReceivedEvent mre, CharSequence content) {
        mre.getChannel().sendMessage(content).queue();
    }

    /**
     * Function that replies to a message with an Embed
     * @param mre MessageReceivedEvent to respond to
     * @param content Embed containing content of the reply
     */
    protected void reply(MessageReceivedEvent mre, MessageEmbed content) {
        mre.getChannel().sendMessage(content).queue();
    }

    /**
     * Function that replies to a message with a Message.
     * @param mre MessageReceivedEvent to respond to
     * @param content Message containing content of the reply
     */
    protected void reply(MessageReceivedEvent mre, Message content) {
        content.getChannel().sendMessage(content).queue();
    }
}