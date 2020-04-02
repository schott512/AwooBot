package command;

import core.events.CommandReceivedEvent;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.EmbedBuilder;
import java.util.Date;
import java.util.List;

/**
 * Ping command object. Contains the information for calculating/returning API latency.
 * @Author Ember (schott512)
 */
public class PingCommand extends Command {

    public PingCommand() {

        // Initialize stuff
        this.keyName = "ping";
        this.helpText = "Checks API ping.";
        this.aliases = new String[]{"P","Ping","p"};
        this.bPerms = new Permission[]{Permission.MESSAGE_WRITE};
        this.dmCapable = true;

    }

    @Override
    public void runCommand(CommandReceivedEvent cre) {

        // Grab args
        List<String> args = cre.args;

        // Build an embed to respond with
        EmbedBuilder eb = new EmbedBuilder();
        Long dif = (new Date().getTime()) - Date.from(cre.getMessage().getTimeCreated().toInstant()).getTime();
        eb.setColor(this.color);
        eb.setTitle("Pong!");
        eb.addField("Latency", dif.toString() + " ms", true);

        // Reply with the embed (after building)
        cre.reply(eb.build(),false);

    }



}
