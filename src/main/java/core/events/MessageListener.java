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
    private static Command[] cList = {new AboutCommand(), new PingCommand(), new EchoCommand(), new PurgeCommand(),
                                      new TestArgsCommand(), new AddEmoteCommand()};

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {

        // Immediately ignore if message is bot generated
        if (e.getAuthor().isBot()) { return; }

        // Grab message content and parse out the command name used
        String content = e.getMessage().getContentRaw();
        String cName = content.split(" ")[0].replace(defaultPrefix, "");

        // Check if content refers to our bot
        if(content.startsWith(defaultPrefix)) {

            // Loop through known commands to see if any are referenced by the message, call that command if true
            for (Command c : cList) {

                if (c.referencedBy(cName)) {c.execute(e);}

            }
        }
    }
}
