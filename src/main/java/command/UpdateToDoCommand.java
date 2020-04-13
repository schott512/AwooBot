package command;

import core.events.CommandReceivedEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * To-do list updating command. Updates and existing embed to-do card. To-do cards may have status new, planning,
 * in-progress, or complete.
 * @author Ember (schott512)
 */
public class UpdateToDoCommand extends Command{

    public UpdateToDoCommand() {

        // Initialize stuff
        this.keyName = "updatetodo";
        this.aliases = new String[]{"changetodo","altertodo", "utd", "ctd"};
        this.bPerms = new Permission[]{Permission.MESSAGE_WRITE};
        this.uPerms = new Permission[]{Permission.MESSAGE_WRITE};
        this.argCount = 3;
        this.dmCapable = true;
        this.subCommands = new Command[]{new EditEmbedCommand()};
        this.helpText = "Edits existing todo item <messageID> with new <description> and <status> in the calling channel. Supported statuses are new, planning, in-progress, and complete.";
        this.args = "<messageID> <status> <description>\\*";
        this.commandType = "embed";

    }

    /**
     * @param cre The Event which triggered this command
     * @param selfReply Boolean value. If true, this command execution may respond to the calling message directly.
     * @return EmbedBuilder containing the updated to-do list item
     */
    @Override
    public Object runCommand(CommandReceivedEvent cre, boolean selfReply) {

        List<String> args = cre.args;

        if (args.size() < 2 || args.size() > 3) { return cre.reject("Improper number of args. Requires 2 or 3"); }

        // Determine color based off of status
        String colorCode;
        String status = args.get(1);
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
        tempArgs.add(args.get(0));
        tempArgs.add("Temp Title");
        tempArgs.add("Temp Desc");
        CommandReceivedEvent tempCRE = new CommandReceivedEvent(cre.getMRE(), tempArgs);
        Object result = subCommands[0].execute(tempCRE, false);
        EmbedBuilder eb = new EmbedBuilder();
        Message m;

        // Check if result is valid. If failed, return failure. If success cast to EmbedBuilder
        if (result.toString().contains("Failed")) { return result.toString(); }
        else { m = (Message) result; }

        // If this message was not posted by AwooBot, reject
        if (!m.getAuthor().equals(cre.getJDA().getSelfUser())) { cre.reject("I can't edit an Embed I didn't write."); }

        // Set title and description
        eb.setTitle(m.getEmbeds().get(0).getTitle());
        if (args.size() == 3) { eb.setDescription(args.get(2)); }
        else { eb.setDescription(m.getEmbeds().get(0).getDescription()); }

        // Add in the extra details to the embed
        eb.addField("Author", cre.getAuthor().getName(), true);
        eb.addField("Status", status, true);
        eb.setFooter("Last updated " + new Date().toString());
        eb.setColor(Color.decode(colorCode));

        // Respond, delete original call, and return
        if (selfReply) { m.editMessage(eb.build()).queue(); }
        ch.deleteMessageById(cre.getMessageId()).queue();
        return eb;

    }
}
