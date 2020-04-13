package command;

import core.events.CommandReceivedEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * To-do list command. Creates an embed to-do card. To-do cards may have status new, planning, in-progress, or complete.
 * @author Ember (schott512)
 */
public class ToDoCommand extends Command {

    public ToDoCommand() {

        // Initialize stuff
        this.keyName = "todo";
        this.aliases = new String[]{"maketodo","addtodo", "td"};
        this.bPerms = new Permission[]{Permission.MESSAGE_WRITE};
        this.uPerms = new Permission[]{Permission.MESSAGE_WRITE};
        this.argCount = 3;
        this.dmCapable = true;
        this.subCommands = new Command[]{new EmbedCommand()};
        this.helpText = "Creates a todo item of <description> with <status> in the calling channel. Supported statuses are new, planning, in-progress, and complete.";
        this.args = "<status> <title> <description>";
        this.commandType = "embed";

    }

    /**
     * @param cre The Event which triggered this command
     * @param selfReply Boolean value. If true, this command execution may respond to the calling message directly.
     * @return EmbedBuilder object containing the to-do list item
     */
    @Override
    public Object runCommand(CommandReceivedEvent cre, boolean selfReply) {

        List<String> args = cre.args;

        if (args.size() != 3) { return cre.reject("Improper number of args. Requires 3"); }

        // Determine color based off of status
        String colorCode;
        String status = args.get(0);
        switch (status) {

            case ("new") : { colorCode = "#ff45f6"; break; }
            case ("planning") : { colorCode = "#4f42ff"; break; }
            case ("in-progress") : { colorCode = "#42f2ff"; break; }
            case ("complete") : { colorCode = "#38cf52"; break; }
            default : { return cre.reject("Invalid status."); }

        }

        // Grab the current channel info
        TextChannel ch = cre.getTextChannel();

        // First, make a new CRE object and call the embed subcommand to build the basic embed
        List<String> tempArgs = new ArrayList<String>();
        tempArgs.add(ch.getId());
        tempArgs.add(colorCode);
        tempArgs.add(args.get(1));
        tempArgs.add(args.get(2));
        CommandReceivedEvent tempCRE = new CommandReceivedEvent(cre.getMRE(), tempArgs);
        Object result = subCommands[0].execute(tempCRE, false);
        EmbedBuilder eb;

        // Check if result is valid. If failed, return failure. If success cast to EmbedBuilder
        if (result.toString().contains("Failed")) { return result.toString(); }
        else { eb = (EmbedBuilder) result; }

        // Add in the extra details to the embed
        eb.addField("Author", cre.getAuthor().getName(), true);
        eb.addField("Status", status, true);
        eb.setFooter("Created " + new Date().toString());

        // Respond, delete original call, and return
        if (selfReply) { ch.sendMessage(eb.build()).queue(); }
        ch.deleteMessageById(cre.getMessageId()).queue();
        return eb;

    }
}
