package command;

import java.util.List;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class AddEmoteCommand extends Command {

    public AddEmoteCommand() {

        this.keyName = "addemote";
        this.aliases = new String[]{"steal"};
        this.bPerms = new Permission[]{Permission.MANAGE_EMOTES};
        this.uPerms = new Permission[]{Permission.MANAGE_EMOTES};
        this.argCount = 2;
        this.args = "<userID> <number>";

    }

    @Override
    public void runCommand(MessageReceivedEvent e, List<String> args) {

    }


}
