package core;

import core.events.MessageListener;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * The core AwooBot class. Kinda ties everything else together, ya know?
 * @Author Ember (schott512)
 */
public class AwooBot extends ListenerAdapter {

    private static Configuration configObj = new Configuration();

    public static void main(String[] args) throws Exception {

       if (!configObj.isInitialized) { throw configObj.ex; }

        JDABuilder builder = new JDABuilder(AccountType.BOT);
        builder.setToken(configObj.botToken);
        builder.addEventListeners(new MessageListener());
        builder.build();

    }

}
