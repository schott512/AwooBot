package core.listeners;

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
    private static final CommandHandler comHandler = new CommandHandler();

    /**
     * Overrides the default onMessageReceived from the JDA. Listens for received messages.
     * @param mre MessageReceivedEvent that triggered this listener
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent mre) {

        // Immediately ignore if message is bot generated
        if (mre.getAuthor().isBot()) { return; }

        // Grab message content and determine if it indicates a command
        String content = mre.getMessage().getContentRaw();
        String cName = content.split(" ")[0].replace(defaultPrefix, "");

        // Check if content refers to our bot. If it does, invoke the command handler
        if(content.startsWith(defaultPrefix)) {
            comHandler.invoke(cName, mre);
        }
    }
}
