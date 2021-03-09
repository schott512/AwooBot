package command;

import core.Configuration;
import core.events.CommandReceivedEvent;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.List;


/**
 * About command object. Contains the information for returning basic bot info.
 * @Author Ember (schott512)
 */
public class AboutCommand extends Command {

    public AboutCommand() {

        // Initialize stuff
        this.keyName = "about";
        this.helpText = "Prints information about the bot.";
        this.aliases = new String[]{"A","About","a"};
        this.bPerms = new Permission[]{Permission.MESSAGE_WRITE};
        this.dmCapable = true;
        this.commandType = "embed";

    }

    /**
     * @param cre The Event which triggered this command
     * @param selfReply Boolean value. If true, this command execution may respond to the calling message directly.
     * @return The constructed embed containing the about information.
     */
    @Override
    public Object runCommand(CommandReceivedEvent cre, boolean selfReply) {

        // Grab args
        List<String> args = cre.args;

        // Build Embed with some basic details
        MessageEmbed me;
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(this.color);
        eb.setTitle("About AwooBot");
        eb.setThumbnail(Configuration.getImageLink());
        eb.addField("Build Version", Configuration.getBuild(), false);
        eb.addField("Author Github Username", "schott512", true);
        eb.setFooter("Thanks for using AwooBot!!!");
        eb.setDescription("!~ Awoo ~! They/Them Pronouns!\n" +
                "I'm a test tool with a handful of miscellaneous tasks.");
        me = eb.build();

        // Reply with embed after building if needed, then return embed
        if (selfReply) {
            cre.reply(me,false);
        }

        return eb;

    }





}
