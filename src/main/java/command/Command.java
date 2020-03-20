package command;

import core.Configuration;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import java.awt.Color;
import java.util.List;
import java.util.ArrayList;

/**  Object representing a basic command.
 *  @Author Ember (schott512)
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

    // Number of expected arguments
    protected int argCount = 0;

    // An abstract function to run command. This will be overridden by specific implementations of the class.
    protected abstract void runCommand(MessageReceivedEvent e, List<String> args);

    /**
     * Function to check whether the command may be run by the calling user in the calling guild/dm and then
     * executes the command by calling the runCommand() function.
     */
    public void execute(MessageReceivedEvent e) {

        if (e.isFromGuild()) {

            // Return if this command can't be used in a guild
            if (!guildCapable) { return; }

            // Return if permissions within guild are not proper
            if (!e.getGuild().getSelfMember().hasPermission(e.getTextChannel(),bPerms)){ return; }
            if (!e.getMember().hasPermission(e.getTextChannel(),uPerms)) { return; }

        }
        else {
            if (!dmCapable) { return; }
        }

        this.runCommand(e, parseArgs(e.getMessage().getContentRaw()));

    }

    public List<String> parseArgs(String s) {

        List<String> args = new ArrayList<String>();
        // Split message content
        String[] temp = s.split(" ");

        if (argCount==0 || temp.length==1) { return args; }


        StringBuilder sb = new StringBuilder();
        for (int i = 1; i<=argCount;i++) {



            if (i == argCount) {

                for (int x = i; x<temp.length; x++) {

                    sb.append(temp[x]);
                    if (x!=temp.length-1) { sb.append(" "); }

                }

            }
            else {

                try {
                    String argTemp = temp[i];
                    if (argTemp.startsWith("<#")) { argTemp = argTemp.substring(2,argTemp.length()-1); }
                    if (argTemp.startsWith("<@!")) { argTemp = argTemp.substring(3,argTemp.length()-1); }
                    args.add(argTemp);
                } catch (Exception e) {}

            }
        }

        if (sb.length()!=0){
            String argTemp = sb.toString();
            if (argTemp.startsWith("<#")) { argTemp = argTemp.substring(2,argTemp.length()-1); }
            args.add(argTemp);
        }
        return args;

    }

    /**
     * Add and alias to the existing list of aliases.
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
     */
    public boolean referencedBy(String s) {

        if (keyName.equals(s)) {
            return true;
        }

        for (String alias : aliases) {

            if (alias.equals(s)) {
                return true;
            }

        }

        return false;

    }

}