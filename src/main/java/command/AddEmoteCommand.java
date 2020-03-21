package command;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Icon;

/**
 * AddEmote command object. Contains the information for adding emotes. Either from an attachment or a pre-existing emote.
 * @Author Ember (schott512)
 */
public class AddEmoteCommand extends Command {

    public AddEmoteCommand() {

        // Initialize stuff
        this.keyName = "addemote";
        this.aliases = new String[]{"steal"};
        this.bPerms = new Permission[]{Permission.MANAGE_EMOTES};
        this.uPerms = new Permission[]{Permission.MANAGE_EMOTES};
        this.argCount = 2;
        this.helpText = "Adds an emote to the current guild.";
        this.args = "<emote|attachment> <newEmoteName>";

    }

    @Override
    public void runCommand(MessageReceivedEvent e, List<String> args) {

        // Grab any emotes/attachments that may be present, initialize strings to hold the emotes image URL and name
        List<Emote> emotes = e.getMessage().getEmotes();
        List<Attachment> attachments = e.getMessage().getAttachments();
        String newEmoteName = "";
        String newEmote = "";

        // InputStream to hold emote
        InputStream tempStream = null;

        // If more than one image/emote has been provided, reject
        if (emotes.size() + attachments.size() != 1) { reject(e,"Too many attachments and/or emotes!"); return; }

        // If exactly 1 attachment exists, add it as an emote
        if (attachments.size() == 1) {

            // Get the attachment from the list. If no args exist, use the attachment filename for the emote name
            Attachment temp = attachments.get(0);
            if (args.size() == 0) {
                newEmoteName = temp.getFileName().replace("."+ temp.getFileExtension(), "");

                // If the attachment is an image, assign newEmote to its string URL
                if (temp.isImage()) { newEmote = temp.getUrl(); } else { reject(e, "No image given."); return; }
            }

            // If 1 arg exists, use it as the name
            else if (args.size() == 1) {
                newEmoteName = args.get(0);

                // If the attachment is an image, assign newEmote to its string URL
                if (temp.isImage()) { newEmote = temp.getUrl(); } else { reject(e, "No image given."); return; }
            }

            // Reject if some other number of args is given
            else { reject(e, "Too many arguments!"); return; }

        }

        // If exactly 1 emote exists, add it as an emote to this guild
        if (emotes.size() == 1) {

            // Get the attachment from the list. If 1 arg exists (the string name of the emote), use the old emote name for the new emote name
            // And grab emote URL
            Emote temp = emotes.get(0);
            if (args.size() == 1) {
                newEmoteName = temp.getName();
                newEmote = temp.getImageUrl();
            }

            // If 2 args exist, use the second arg as the name and grab emote URL
            else if (args.size() == 2) {
                newEmoteName = args.get(1);
                newEmote = temp.getImageUrl();
            }

            // Reject if some other number of args is given
            else { reject(e, "Too many arguments!"); return; }

        }

        // Attempt to upload. Open an InputStream to the image URL and createEmote in guild
        try {
            tempStream = new URL(newEmote).openStream();
            e.getGuild().createEmote(newEmoteName, Icon.from(tempStream)).queue();
        } catch (Exception ex) { System.out.println(ex.getMessage()); }

    }


}
