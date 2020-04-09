package command;

import core.events.CommandReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for handling commands. Parsing/passing args, Matching messages to commands, etc.
 * @author (Ember) schott512
 */
public class CommandHandler {

    // Array containing objects of every command
    public static Command[] cList = {new AboutCommand(), new PingCommand(), new EchoCommand(), new PurgeCommand(),
                                      new TestArgsCommand(), new AddEmoteCommand(), new HelpCommand(), new ChangeNickName()};

    public CommandHandler() {

    }

    /**
     * Sorts out which command to send an input to, as well as parses out any arguments
     */
    public void invoke(String cName, MessageReceivedEvent mre) {

        // Variable to house command object that is called
        Command com = null;

        // List to hold args
        List<String> args = new ArrayList<>();

        // Loop through known commands to see if any are referenced by the message
        for (Command c : cList) {

            // If this is the correct command, parse arguments from the message (using the commands argCount) and call it and return
            if (c.referencedBy(cName)) {

                com = c;
                args = parseArgs(mre.getMessage().getContentRaw(), com.argCount);
                break;

            }
        }

        // CommandReceivedEvent for the calling MRE
        CommandReceivedEvent cre = new CommandReceivedEvent(mre, args);

        if (com != null) { com.execute(cre, args); }
        else { cre.reject("No command matches that name or alias."); }

    }

    /**
     * Breaks up the raw content string of a message into a list of string arguments
     * @param s The raw content of a message
     * @param argCount The maximum number of arguments s can be expected to contain
     * @return List of string arguments parsed from the input
     */
    private List<String> parseArgs(String s, int argCount) {

        // Split message content on spaces
        String[] temp = s.split(" ");
        List<String> args = new ArrayList<>();

        // Return empty if the string input only contains 1 item (as that would be "prefix;command", and isn't an arg)
        if (temp.length == 1) {
            return args;
        }

        // Initialize a StringBuilder object for keeping tracking of multi-word args
        StringBuilder sb = new StringBuilder();

        // Loop through temp array starting a index 1 (to skip over the "prefix;command")
        for (int i = 1; i <= argCount; i++) {

            // Break/stop if i is greater than the number of possible arguments in the temp array
            if (i > temp.length - 1) {
                break;
            }

            // If on the last expected argument, loop through any remaining members of temp and build as one multi-word arg
            if (i == argCount) {

                // If one argument remains, add it.
                if (i == temp.length) {

                    // Scrub tags from single args
                    String argTemp = temp[i];
                    if (argTemp.startsWith("<#")) {
                        argTemp = argTemp.substring(2, argTemp.length() - 1);
                    }
                    if (argTemp.startsWith("<@!")) {
                        argTemp = argTemp.substring(3, argTemp.length() - 1);
                    }

                }

                // Loop through remaining values in temp Array to build multi-word arg
                else {
                    for (int x = i; x < temp.length; x++) {
                        sb.append(temp[x]);
                        if (x != temp.length - 1) {
                            sb.append(" ");
                        }
                    }

                    // Build/add StringerBuilder to args list
                    args.add(sb.toString());
                    sb = new StringBuilder();
                }
            }

            // If item [i] starts with ", treat it as the start of a multi-word argument
            else if (temp[i].startsWith("\"")) {

                int counter = 0;

                // Loop until either and end quote is found or we reach the end of the temp array
                while (!temp[i + counter].endsWith("\"") && i + 1 + counter < temp.length) {

                    // Add this item to the StringBuilder, with a space after. Then increment counter
                    sb.append(temp[i + counter].replace("\"", ""));
                    sb.append(" ");
                    counter += 1;

                }

                // The last item will not have been appended, so append it
                sb.append(temp[i + counter].replace("\"", ""));

                // Add counter to i to skip ahead counter number of elements in temp Array. Build/add arg to list, reset StringBuilder
                i += (counter);
                args.add(sb.toString());
                sb = new StringBuilder();

            }

            // Default case, try to add single argument. Scrub user/channel tags from single args
            else {

                try {
                    String argTemp = temp[i];
                    if (argTemp.startsWith("<#")) {
                        argTemp = argTemp.substring(2, argTemp.length() - 1);
                    }
                    if (argTemp.startsWith("<@!")) {
                        argTemp = argTemp.substring(3, argTemp.length() - 1);
                    }
                    args.add(argTemp);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        return args;

    }



}
