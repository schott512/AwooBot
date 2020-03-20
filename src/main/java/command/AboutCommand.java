package command;

import core.Configuration;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import java.util.List;


/**
 * Simple command to print info about AwooBot.
 * @Author schott512 (Ember)
 */
public class AboutCommand extends Command {

    public AboutCommand() {

        this.keyName = "about";
        this.helpText = "Prints information about the bot.";
        this.aliases = new String[]{"A","About","a"};
        this.bPerms = new Permission[]{Permission.MESSAGE_WRITE};
        this.dmCapable = true;

    }

    @Override
    public void runCommand(MessageReceivedEvent e, List<String> args) {

        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(this.color);
        eb.setTitle("About AwooBot");
        eb.setThumbnail(Configuration.imageLink);
        eb.addField("Build Version", Configuration.build, false);
        eb.addField("Author Github Username", "schott512", true);
        eb.setFooter("Thanks for using AwooBot!!!");

        e.getChannel().sendMessage(eb.build()).queue();

    }





}
