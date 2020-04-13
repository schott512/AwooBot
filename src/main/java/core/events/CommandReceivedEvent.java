package core.events;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * CommandReceivedEvent class. This class is a wrapper for a MessageReceivedEvent, it is created when a command is received.
 * @Author Ember (schott512)
 */
public class CommandReceivedEvent {

    // The MessageReceivedEvent this object is built around
    private MessageReceivedEvent mre;

    // List of string arguments associated with this event
    public List<String> args = new ArrayList<>();

    public CommandReceivedEvent(MessageReceivedEvent e, List<String> alist) {

        mre = e;
        args = alist;

    }

    /**
     * Responds to message with a failure reason.
     * @param reason
     */
    public String reject(String reason) {

        // Build a generic error message and reply with it
        MessageBuilder msgBuild = new MessageBuilder();
        msgBuild.append(":x: ");
        msgBuild.append("Failed to execute command. ");
        msgBuild.append(reason);
        msgBuild.append(" :x:");
        reply(msgBuild.build(), false);

        return "Failed to execute command. " + reason;

    }

    /**
     * Function that replies to a message with a CharSequence.
     * @param content CharSequence containing content of the reply
     * @param asDM Boolean value, if true the response is sent in a DM to the calling user
     */
    public CharSequence reply(CharSequence content, boolean asDM) {

        if (asDM){
            // If sent from a guild, open a private channel. Otherwise send in same channel
            if (mre.isFromGuild()) {
                // Send message to users DMs
                mre.getMember().getUser().openPrivateChannel().queue((channel) -> {
                    channel.sendMessage(content).queue();
                });
            }
            else {
                mre.getPrivateChannel().sendMessage(content).queue();
            }
        }
        else { mre.getChannel().sendMessage(content).queue(); }

        return content;

    }

    /**
     * Function that replies to a message with an Embed
     * @param content Embed containing content of the reply
     * @param asDM Boolean value, if true the response is sent in a DM to the calling user
     */
    public MessageEmbed reply(MessageEmbed content, boolean asDM) {

        if (asDM){
            // If sent from a guild, open a private channel. Otherwise send in same channel
            if (mre.isFromGuild()) {
                // Send message to users DMs
                mre.getMember().getUser().openPrivateChannel().queue((channel) -> {
                    channel.sendMessage(content).queue();
                });
            }
            else {
                mre.getPrivateChannel().sendMessage(content).queue();
            }
        }
        else { mre.getChannel().sendMessage(content).queue(); }

        return content;
    }

    /**
     * Function that replies to a message with a Message.
     * @param content Message containing content of the reply
     * @param asDM Boolean value, if true the response is sent in a DM to the calling user
     */
    public Message reply(Message content, boolean asDM) {

        if (asDM) {
            // If sent from a guild, open a private channel. Otherwise send in same private channel
            if (mre.isFromGuild()) {
                // Send message to users DMs
                mre.getMember().getUser().openPrivateChannel().queue((channel) -> {
                    channel.sendMessage(content).queue();
                });
            } else {
                mre.getPrivateChannel().sendMessage(content).queue();
            }
        } else {
            mre.getChannel().sendMessage(content).queue();
        }

        return content;

    }


    // Just some functions so that using a CommandReceivedEvent appears identical to using a MessageReceivedEvent
    // Basically make sure the MessageReceivedEvent's stuff is still accessible

    public Message getMessage() { return mre.getMessage(); }
    public boolean isFromGuild() { return mre.isFromGuild(); }
    public Member getMember() { return mre.getMember(); }
    public MessageChannel getChannel() { return mre.getChannel(); }
    public Guild getGuild() { return mre.getGuild(); }
    public JDA getJDA() { return mre.getJDA(); }
    public TextChannel getTextChannel() { return mre.getTextChannel(); }
    public User getAuthor() { return mre.getAuthor(); }
    public boolean isWebHookMessage() { return mre.isWebhookMessage(); }
    public ChannelType getChannelType() { return mre.getChannelType(); }
    public String getMessageId() { return mre.getMessageId(); }
    public long getMessageIdLong() { return mre.getMessageIdLong(); }
    public PrivateChannel getPrivateChannel() { return mre.getPrivateChannel(); }
    public long getResponseNumber() { return mre.getResponseNumber(); }
    public boolean isFromType(ChannelType type) { return mre.isFromType(type); }

    // Method for retrieving entire MessageReceivedEvent
    public MessageReceivedEvent getMRE() { return mre; }
}
