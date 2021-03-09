package core;

import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;

import com.google.gson.JsonObject;
import com.google.gson.Gson;

/**
 * Class responsible for reading/storing bot configuration data.
 * @Author Ember (schott512)
 */
public class Configuration {

    // Read from file
    private static String botToken = null;
    private static boolean writeLog = false;
    private static String build = "";
    private static Color color = new Color(255,255,255);
    private static String imageLink = null;
    private static String defaultPrefix = "awb;";
    private static boolean isInitialized = false;
    private static Exception ex = null;
    private static Date startTime;

    // Constructor. Look for and open the settings.JSON file.
    public Configuration() {

        try {
            loadConfig();
        } catch (Exception e) { ex = e; }

    }

    /**
     * Public function to allow bot to re-read configuration details, should something change.
     */
    public static void refreshConfig() throws Exception {

        // Check if isInitiated. If true, a configuration object exists and has been initiated. Otherwise, error.
        if (isInitialized) { loadConfig(); }
        else { throw new Exception("You can't refresh a Configuration Object that doesn't exist!"); }

    }

    /**
     * This class actually does the reading in of the json and stuff.
     */
    private static void loadConfig() throws Exception {

        startTime = new Date();

        // Open settings file and read it
        try {
            String s = File.separator;
            String path = System.getProperty("user.dir")+ s + "settings.json";

            // File reading shenanigans to get settings file into one string ~
            FileReader fr = new FileReader(path);
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            StringBuilder sb = new StringBuilder();

            while(line != null){
                sb.append(line).append("\n");
                line = br.readLine();
            }

            br.close();

            // Convert to JsonOjbect and grab necessary data. Replace quotes because GSON is silly and doesn't account for them..
            JsonObject configJSON = new Gson().fromJson(sb.toString(), JsonObject.class);
            botToken = configJSON.get("botToken").toString().replace("\"", "");
            writeLog = configJSON.get("writeLog").getAsBoolean();
            build = configJSON.get("buildVers").toString().replace("\"", "");
            String tempColor = configJSON.get("botColor").toString().replace("\"", "");
            imageLink = configJSON.get("imageLink").toString().replace("\"", "");
            String tempPrefix = configJSON.get("defaultPrefix").toString().replace("\"", "");
            isInitialized = true;


            //------------------------------------Exceptions Checks-----------------------------------------\\

            if (botToken.isEmpty()) { throw new Exception("Please insert bot token."); }
            if (build.isEmpty()) { throw new Exception("Please specify build version."); }
            if (imageLink.isEmpty()) { throw new Exception("Please specify image location."); }

            // Decode default color.
            if (tempColor.isEmpty()) { throw new Exception("Must include default bot color Hex value."); }
            else { color = Color.decode(tempColor); }

            //----------------------------------End Exception Checks-----------------------------------------\\

            if (!tempPrefix.isEmpty()) { defaultPrefix = tempPrefix; }

        } catch (IOException e) {
            ex = e;
            e.printStackTrace();
            isInitialized = false;
        }

    }

    //Getters for stuff
    public static String getBotToken() { return botToken; }
    public static String getBuild() { return build; }
    public static String getImageLink() { return imageLink; }
    public static String getDefaultPrefix() { return defaultPrefix; }
    public static boolean isWriteLog() { return writeLog; }
    public static boolean IsInitialized() { return isInitialized; }
    public static Color getColor() { return color; }
    public static Exception getEx() { return ex; }

}
