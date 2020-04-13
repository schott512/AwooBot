package core;

import core.listeners.GuildListener;
import core.listeners.MessageListener;
import data.DatabaseManager;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Date;

/**
 * The core AwooBot class. Kinda ties everything else together, ya know?
 * @Author Ember (schott512)
 */
public class AwooBot extends ListenerAdapter {

    private static Configuration configObj = new Configuration();
    private static WebhookLogManager logManager;
    public static DatabaseManager dbManager;

    public static void main(String[] args) throws Exception {

        // If config failed to initialize, throw exception
        if (!configObj.isInitialized) { throw configObj.ex; }

        // Initialize the database manager
        dbManager = new DatabaseManager("AwooBot");

        // If logging desired, enable logManager
        logManager = new WebhookLogManager(configObj.webhookLogURL);

        // Build JDA
        JDABuilder builder = new JDABuilder(AccountType.BOT);
        builder.setToken(configObj.botToken);
        builder.addEventListeners(new MessageListener());
        builder.addEventListeners(new GuildListener());
        builder.build();

        // Log proper startup if logging enabled
        if (configObj.writeLog) {
            logManager.sendMessage("Successful startup.");
        }
    }
}
