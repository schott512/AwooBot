package core.events;

import command.*;
import core.Configuration;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * The class which houses listener methods for all types of message related event.
 * @author Ember (schott512)
 */
public class MessageListener extends ListenerAdapter {

    private String defaultPrefix = Configuration.defaultPrefix;
    private static Command[] cList = {new AboutCommand(), new PingCommand(), new EchoCommand(), new PurgeCommand()};

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {

        // Immediately ignore if message is bot generated
        if (e.getAuthor().isBot()) { return; }

        String content = e.getMessage().getContentRaw();
        String cName = content.split(" ")[0].replace(defaultPrefix, "");

        if(content.startsWith(defaultPrefix)) {

            for (Command c : cList) {

                if (c.referencedBy(cName)) {c.execute(e);}

            }
        }
    }
}
