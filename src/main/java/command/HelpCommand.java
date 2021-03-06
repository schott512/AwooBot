package command;

import java.util.List;

import core.events.CommandReceivedEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;


public class HelpCommand extends Command{

    public HelpCommand() {

        // Initialize stuff
        this.keyName = "help";
        this.aliases = new String[]{"h"};
        this.bPerms = new Permission[]{Permission.MESSAGE_WRITE};
        this.uPerms = new Permission[]{Permission.MESSAGE_WRITE};
        this.argCount = 1;
        this.dmCapable = true;
        this.helpText = "Builds and returns the help info for a command.";
        this.args = "<commandName| -l>\\*";
        this.commandType = "embed";

    }

    /**
     * @param cre The Event which triggered this command
     * @param selfReply Boolean value. If true, this command execution may respond to the calling message directly.
     * @return EmbedBuilder object containing the help info
     */
    @Override
    public Object runCommand(CommandReceivedEvent cre, boolean selfReply) {

        Command referencedCommand = null;

        // Grab args
        List<String> args = cre.args;

        // Set up an EmbedBuilder
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(this.color);
        eb.setFooter("Arguments marked * are optional.");

        // If an argument exists, check if it is the list flag or if it is a specific command
        if (args.size() == 1) {

            // If -l is not the argument, proceed as if a command name was given
            if (!args.get(0).equals("-l")) {
                String commandName = args.get(0);

                // Loop through until a command matches
                for (Command c : CommandHandler.cList) {
                    if (c.referencedBy(commandName)) {
                        referencedCommand = c;
                    }
                }

                // Reject/return if no match
                if (referencedCommand == null) {
                    return cre.reject("No command with that name can be found.");
                }

                // Add stuff to the embedBuilder
                eb.setTitle("Help for " + commandName);
                eb.addField("Description", referencedCommand.helpText, true);
                if (referencedCommand.argCount != 0) { eb.addField("Expected args", referencedCommand.args, false); }
                eb.addField("Usage", referencedCommand.getUsage(), false);
            }

            // -l was given, provide a list of all available commands in a DM to the user
            else {

                // Add stuff to the embed
                eb.setTitle("Command List");

                for (Command c: CommandHandler.cList) {
                    eb.addField(c.keyName + " " + c.args, c.helpText, false);
                }

                // Build and send EB, return
                if (selfReply) { cre.reply(eb.build(),true); return eb;}
            }

        }

        // No arguments given, provide basic overview of use
        else {

            eb.setTitle("Basic Use");
            eb.addField("Help Command", "Use {prefix}help -l to see a full list of available commands.", true);

        }

        if (selfReply) { cre.reply(eb.build(),false); }
        return eb;

    }


}
