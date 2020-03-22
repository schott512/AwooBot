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
    public static String botToken = null;
    public static boolean writeLog = false;
    public static String webhookLogURL = "";
    public static String build = "";
    public static Color color = new Color(255,255,255);
    public static String imageLink = null;
    public static String defaultPrefix = "awb;";
    public static boolean isInitialized = false;
    public static Exception ex = null;

    // Calculated on startup
    public static Date startTime = null;

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

            // Convert to JsonOjbect and grab necessary data. Replace quotes because GSON is silly and doesn't account for them..
            JsonObject configJSON = new Gson().fromJson(sb.toString(), JsonObject.class);
            botToken = configJSON.get("botToken").toString().replace("\"", "");
            writeLog = configJSON.get("writeLog").getAsBoolean();
            webhookLogURL = configJSON.get("webhookLogURL").toString().replace("\"", "");
            build = configJSON.get("buildVers").toString().replace("\"", "");
            String tempColor = configJSON.get("botColor").toString().replace("\"", "");
            imageLink = configJSON.get("imageLink").toString().replace("\"", "");
            String tempPrefix = configJSON.get("defaultPrefix").toString().replace("\"", "");
            isInitialized = true;


            //------------------------------------Exceptions Checks-----------------------------------------\\

            if (botToken.isEmpty()) { throw new Exception("Please insert bot token."); }
            if (writeLog && webhookLogURL.isEmpty()) { throw new Exception("Please insert log URL."); }
            if (build.isEmpty()) { throw new Exception("Please specify build version."); }
            if (imageLink.isEmpty()) { throw new Exception("Please specify image location."); }

            // Decode default color.
            if (tempColor.isEmpty()) { throw new Exception("Must include default bot color Hex value."); }
            else { color = Color.decode(tempColor); }

            //----------------------------------End Exception Checks-----------------------------------------\\

            if (!tempPrefix.isEmpty()) { defaultPrefix = tempPrefix; }

        } catch (IOException e) {
            e.printStackTrace();
            isInitialized = false;
        }

    }

}
