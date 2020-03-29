package command;

import core.Configuration;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.EmbedBuilder;
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

    }

    @Override
    public void runCommand(MessageReceivedEvent e, List<String> args) {

        // Build Embed with some basic details
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(this.color);
        eb.setTitle("About AwooBot");
        eb.setThumbnail(Configuration.imageLink);
        eb.addField("Build Version", Configuration.build, false);
        eb.addField("Author Github Username", "schott512", true);
        eb.setFooter("Thanks for using AwooBot!!!");

        // Reply with embed after building
        reply(e,eb.build(),false);

    }





}
