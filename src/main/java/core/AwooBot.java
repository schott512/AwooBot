package core;

import core.autopost.AutoPostManager;
import core.listeners.GuildListener;
import core.listeners.MessageListener;
import data.DatabaseManager;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.JDA;

/**
 * The core AwooBot class. Kinda ties everything else together, ya know?
 * @author Ember (schott512)
 */

public class AwooBot extends ListenerAdapter {

    private static Configuration configObj = new Configuration();
    public static DatabaseManager dbManager;
    public static AutoPostManager apManager;
    private static JDA jda;

    public static void main(String[] args) throws Exception {

        // If config failed to initialize, throw exception
        if (!configObj.IsInitialized()) { throw configObj.getEx(); }

        // Initialize the database manager
        dbManager = new DatabaseManager("AwooBot");
        dbManager.toggleLogging();

        // Create JDA
        JDABuilder builder = JDABuilder.createDefault(configObj.getBotToken());
        builder.addEventListeners(new MessageListener());
        builder.addEventListeners(new GuildListener());
        jda = builder.build();
        jda = jda.awaitReady();

        // Start the AutoPost Manager
        apManager = new AutoPostManager(jda);
        apManager.start();

    }

}
