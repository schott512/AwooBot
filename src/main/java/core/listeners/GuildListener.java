package core.listeners;

import core.Configuration;
import core.WebhookLogManager;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

/**
 * The class which houses listener methods for all types of guild related events.
 * @author Ember (schott512)
 */
public class GuildListener extends ListenerAdapter {

    @Override
    public void onGuildJoin(@Nonnull GuildJoinEvent gje) {

        WebhookLogManager.sendMessage("I joined a guild.");

    }

}
