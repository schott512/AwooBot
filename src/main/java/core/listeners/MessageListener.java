package core.listeners;

import command.*;
import core.AwooBot;
import core.Configuration;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

/**
 * The class which houses listener methods for all types of message related event.
 * @author Ember (schott512)
 */
public class MessageListener extends ListenerAdapter {

    private String defaultPrefix = Configuration.getDefaultPrefix();
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
        Object prefixResult = null;
        if (mre.isFromGuild()) { prefixResult = AwooBot.dbManager.getSetting(mre.getGuild().getId(), "prefix"); }
        String prefix;
        if (prefixResult == null) { prefix = defaultPrefix; } else { prefix = prefixResult.toString(); }
        String cName = content.split(" ")[0].replace(prefix, "");

        // Check if content refers to our bot. If it does, invoke the command handler
        if(content.startsWith(prefix)) {
            comHandler.invoke(cName, mre);
        }

        // Check if the content contains *only* a single user tag
        else if(content.startsWith("<@!") && content.trim().endsWith(">") && mre.isFromGuild()) {

            // Check if it refers to this bot. If so, say this guilds current prefix
            String uID = content.substring(3, content.length() - 1);
            Member tag = mre.getGuild().getMemberById(uID);
            Member self = mre.getGuild().getSelfMember();
            if (self == tag) {

                // Check permissions, then send if we have them
                if (self.hasPermission(mre.getTextChannel(),Permission.MESSAGE_WRITE)) {
                    mre.getTextChannel().sendMessage("This guilds current prefix is \"" + prefix + "\".").queue();
                }
            }
        }
    }

    /**
     * Overrides the default onMessageReactionAdd from the JDA. Listens for received messages.
     * @param rae MessageReactionAddEvent that triggered this listener
     */
    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent rae) { reactRole("add", rae); }

    /**
     * Overrides the default onMessageReactionRemove from the JDA. Listens for received messages.
     * @param rre MessageReactionRemoveEvent that triggered this listener
     */
    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent rre) { reactRole("remove", rre); }

    /**
     * Performs the actual role altering for reactroles. Since the bulk of adding/removing a role is exactly the same.
     * @param action Whether we are removing or adding a role
     * @param gmre The GenericMessageReaction responsible for the trigger
     */
    private void reactRole(String action, GenericMessageReactionEvent gmre) {

        // Does the message that triggered this even have any reactrole entries?
        List<String> rrMessages = AwooBot.dbManager.getRRMessages(gmre.getGuild().getId());

        // Does this message have associated reactrole data?
        if (rrMessages.contains(gmre.getMessageId())) {

            // Grab the info for this emote
            ReactionEmote re = gmre.getReactionEmote();
            String eID;
            if (re.isEmoji()) { eID = re.getEmoji(); }
            else { eID = re.getId(); }

            // Search the db for a corresponding entry
            String roleID = AwooBot.dbManager.getReactRole(gmre.getMessageId(), eID).get("role").toString();

            // Find the corresponding role
            Role r = gmre.getGuild().getRoleById(roleID);

            // If a role was found, add/remove for member
            if (r == null) { }
            else {

                try {

                    if (action.equals("add")) { gmre.getGuild().addRoleToMember(gmre.getMember(), r).queue(); }
                    else { gmre.getGuild().removeRoleFromMember(gmre.getMember(), r).queue(); }

                }
                catch (HierarchyException he) { gmre.getTextChannel().sendMessage("Seems I'm below " + r.getName() + " in the hierarchy and may not assign or remove it.").queue(); }

            }
        }

    }

}
