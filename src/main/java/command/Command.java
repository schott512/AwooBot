package command;

import core.Configuration;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.Color;
import java.util.List;
import java.util.ArrayList;

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
     * @param e The MessageEvent which triggered this command
     */
    public void execute(MessageReceivedEvent e) {

        // Check if the source of the command is a guild
        if (e.isFromGuild()) {

            // Reject if this command can't be used in a guild
            if (!guildCapable) { reject(e,"Command may not be used in guilds."); return; }

            // Reject if permissions within guild are not proper
            if (!e.getGuild().getSelfMember().hasPermission(e.getTextChannel(),bPerms)){ reject(e,"I lack required permissions for this action."); return; }
            if (!e.getMember().hasPermission(e.getTextChannel(),uPerms)) { reject(e,"User lacks required permissions."); return; }

        }
        else {

            // Reject if not usable in DMs
            if (!dmCapable) { reject(e, "This command is not usable from DMs."); return; }

        }

        // Run this command if it hasn't been rejected
        this.runCommand(e, parseArgs(e.getMessage().getContentRaw()));

    }

    /**
     * Breaks up the raw content string of a message into a list of string arguments
     * @param s A string, specifically the raw content of a message event
     * @return A list of string parameters
     */
    public List<String> parseArgs(String s) {

        // Initialize list of args to eventually pass back
        List<String> args = new ArrayList<String>();

        // Split message content on spaces
        String[] temp = s.split(" ");

        // Return empty list if the string input only contains 1 item (as that would be "prefix;command", and isn't an arg)
        if (temp.length==1) { return args; }

        // Initialize a StringBuilder object for keeping tracking of multi-word args
        StringBuilder sb = new StringBuilder();

        // Loop through temp array starting a index 1 (to skip over the "prefix;command")
        for (int i = 1; i<=argCount;i++) {

            // Break/stop if i is greater than the number of possible arguments in the temp array
            if (i > temp.length-1) { break; }

            // If on the last expected argument, loop through any remaining members of temp and build as one multi-word arg
            if (i == argCount) {

                // If one argument remains, add it. Else attempt building multi-word arg
                if (i == temp.length) {

                    // Scrub tags from single args
                    String argTemp = temp[i];
                    if (argTemp.startsWith("<#")) { argTemp = argTemp.substring(2,argTemp.length()-1); }
                    if (argTemp.startsWith("<@!")) { argTemp = argTemp.substring(3,argTemp.length()-1); }

                }

                // Loop through remaining values in temp Array to build multi-word arg
                else {
                    for (int x = i; x<temp.length; x++) {
                        sb.append(temp[x]);
                        if (x != temp.length - 1) { sb.append(" "); }
                    }

                    // Build/add StringerBuilder to args list
                    args.add(sb.toString());
                    sb = new StringBuilder();
                }
            }

            // If item [i] starts with ", treat it as the start of a multi-word argument
            else if (temp[i].startsWith("\"")) {

                int counter = 0;

                // Loop until either and end quote is found or we reach the end of the temp array
                while (!temp[i+counter].endsWith("\"" ) && i+1+counter < temp.length) {

                    // Add this item to the StringBuilder, with a space after. Then increment counter
                    sb.append(temp[i+counter].replace("\"",""));
                    sb.append(" ");
                    counter += 1;

                }

                // The last item will not have been appended, so append it
                sb.append(temp[i+counter].replace("\"",""));

                // Add counter to i to skip ahead counter number of elements in temp Array. Build/add arg to list, reset StringBuilder
                i+=(counter);
                args.add(sb.toString());
                sb = new StringBuilder();

            }

            // Default case, try to add single argument. Scrub user/channel tags from single args
            else {

                try {
                    String argTemp = temp[i];
                    if (argTemp.startsWith("<#")) { argTemp = argTemp.substring(2,argTemp.length()-1); }
                    if (argTemp.startsWith("<@!")) { argTemp = argTemp.substring(3,argTemp.length()-1); }
                    args.add(argTemp);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        return args;

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