package command;

import core.AwooBot;
import core.events.CommandReceivedEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * ReactRole configuration command. Creates/Removes reactrole entries for a guild.
 * @author Ember (schott512)
 */
public class ReactRoleCommand extends Command {

    public ReactRoleCommand() {

        // Initialize stuff
        this.keyName = "reactrole";
        this.aliases = new String[]{"rr"};
        this.bPerms = new Permission[]{Permission.MANAGE_ROLES};
        this.uPerms = new Permission[]{Permission.MANAGE_ROLES};
        this.argCount = 4;
        this.subCommands = new Command[]{new EditEmbedCommand()};
        this.helpText = "Edits the react role configuration. Add and remove require both emoteID and roleID," +
                " but view requires neither. Must be done in the same channel as the message. Messages cannot exceed 20 reactroles.";
        this.args = "<add|remove|view> <messageID> <emoteID>\\* <roleID>\\*";
        this.commandType = "modify";

    }

    /**
     * @param cre The Event which triggered this command
     * @param selfReply Boolean value. If true, this command execution may respond to the calling message directly.
     * @return Boolean value. If true, the data was submitted to the DB (or retrieved in the case of view)
     */
    @Override
    public Object runCommand(CommandReceivedEvent cre, boolean selfReply) {

        List<String> args = cre.args;
        String act = args.get(0);

        // Copy and remove the add|remove|view args
        List<String> newArgs = new ArrayList<>(args);
        newArgs.remove(0);

        // Grab the ID from the attached emote (If no emote attached, the emote is a default character)
        List<Emote> emotes = cre.getMessage().getEmotes();
        if (emotes.size() > 1) { cre.reject("Too many emotes provided"); return false; }
        else if (emotes.size() > 0) { Emote e = emotes.get(0); newArgs.set(2, e.getId()); }

        // Call the appropriate function
        switch (act) {
            case ("add") : {

                // Is the number of args correct?
                if (newArgs.size() != 3) { cre.reject("Emote ID and Role ID not provided with add request."); return false; }
                return addReactRole(cre, newArgs.get(0), newArgs.get(1), newArgs.get(2));

            }
            case ("remove") : {

                // Is the number of args correct?
                if (newArgs.size() < 2) { cre.reject("Emote ID not provided with remove request."); return false; }
                return removeReactRole(cre, newArgs.get(0), newArgs.get(1));

            }

            case ("view") : {

                // Is the number of args correct?
                if (newArgs.size() < 1) { cre.reject("No messageID provided."); return false; }
                return viewReactRole(cre, newArgs.get(0));

            }

            default: { cre.reject("Invalid option, please select add|remove|view."); return false; }

        }
    }

    /**
     * Function that handles adding an individual entry to the reactroles.
     * @param cre Calling CommandReceivedEvent
     * @param mID Id of the message to add items to
     * @param eID Id of the emote reference
     * @param rID ID of the corresponding role
     * @return whether or not the DB was updated
     */
    public boolean addReactRole(CommandReceivedEvent cre, String mID, String eID, String rID) {

        // If this message has an entry for this emote, reject
        Map<String, Object> singleResult = AwooBot.dbManager.getReactRole(mID,eID);
        if (singleResult != null) { cre.reject("A reactrole entry for this emote exists on that message."); return false; }

        AwooBot.dbManager.addReactRoleEntry(cre.getGuild().getId(), mID,eID,rID);

        // Call editembed subcommand to fetch the original message
        TextChannel ch = cre.getTextChannel();
        List<String> tempArgs = new ArrayList<String>();
        tempArgs.add(ch.getId());
        tempArgs.add(mID);
        tempArgs.add("Temp Title");
        tempArgs.add("Temp Desc");
        CommandReceivedEvent tempCRE = new CommandReceivedEvent(cre.getMRE(), tempArgs);
        Object result = subCommands[0].execute(tempCRE, false);
        EmbedBuilder eb = new EmbedBuilder();
        Message reactMessage = (Message) subCommands[0].execute(tempCRE, false);

        // Grab the added emote
        List<Emote> emotes = cre.getMessage().getEmotes();
        Emote e = null;
        if (emotes.size() > 1) { cre.reject("Too many emotes provided"); return false; }
        else if (emotes.size() > 0) { e = emotes.get(0); }

        // If it's an actual emote, react. Otherwise, react with the simple character
        if(e == null) { reactMessage.addReaction(eID).queue(); }
        else { reactMessage.addReaction(e).queue(); }

        return true;

    }

    /**
     * Function that handles removing an individual entry from the reactroles
     * @param cre Calling CommandReceivedEvent
     * @param mID Id of the message to remove items from
     * @param eID Id of the emote reference
     * @return whether or not the DB was updated
     */
    public boolean removeReactRole(CommandReceivedEvent cre, String mID, String eID) {

        // If this message has no entry for this emote, reject
        Map<String, Object> singleResult = AwooBot.dbManager.getReactRole(mID,eID);
        if (singleResult == null) { cre.reject("A reactrole entry for this emote does not exist on that message."); return false; }

        AwooBot.dbManager.removeReactRoleEntry(cre.getGuild().getId(), mID,eID);

        // Call editembed subcommand to fetch the original message
        TextChannel ch = cre.getTextChannel();
        List<String> tempArgs = new ArrayList<String>();
        tempArgs.add(ch.getId());
        tempArgs.add(mID);
        tempArgs.add("Temp Title");
        tempArgs.add("Temp Desc");
        CommandReceivedEvent tempCRE = new CommandReceivedEvent(cre.getMRE(), tempArgs);
        Object result = subCommands[0].execute(tempCRE, false);
        EmbedBuilder eb = new EmbedBuilder();
        Message reactMessage = (Message) subCommands[0].execute(tempCRE, false);

        // Grab the added emote
        List<Emote> emotes = cre.getMessage().getEmotes();
        Emote e = null;
        if (emotes.size() > 1) { cre.reject("Too many emotes provided"); return false; }
        else if (emotes.size() > 0) { e = emotes.get(0); }

        // If it's an actual emote, react. Otherwise, react with the simple character
        if(e == null) { reactMessage.clearReactions(eID).queue(); }
        else { reactMessage.clearReactions(e).queue(); }

        return true;

    }

    /**
     * Function that handles removing an individual entry from the reactroles
     * @param cre Calling CommandReceivedEvent
     * @param mID Id of the message to check
     * @return whether or not view data was fetched
     */
    public boolean viewReactRole(CommandReceivedEvent cre, String mID) {

        // Grab DB entries for this message
        List<Map<String, Object>> rrSet = AwooBot.dbManager.getReactRolesByMessageID(mID);

        // Setup an EmbedBuilder
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("React Roles Configured for " + mID);

        // Build message which will become the description
        StringBuilder sb = new StringBuilder();

        for (Map<String, Object> rr : rrSet) {

            // If it's a real emote, grab it and display it. Otherwise just use the character
            Emote e = null;
            String emote_text = "<name:id>";
            if (rr.get("emote").toString().length() < 9 ) { emote_text = rr.get("emote").toString(); }
            else { e = cre.getGuild().getEmoteById(rr.get("emote").toString()); }

            if (e != null) { emote_text.replace("name", e.getName()); emote_text.replace("id", e.getId()); }

            // Grab the role, fill in the data
            Role r = cre.getGuild().getRoleById(rr.get("role").toString());

            if (r == null) { sb.append(emote_text + " maps to a role that no longer exists!"); }
            else { sb.append(emote_text + " maps to the role named \"" + r.getName() + "\"\n"); }

        }

        eb.setColor(this.color);
        eb.setDescription(sb.toString());
        cre.reply(eb.build(), false);

        return true;

    }

}
