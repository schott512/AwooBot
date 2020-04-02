package core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.MalformedURLException;
import java.net.URL;
import com.google.gson.JsonObject;
import com.google.gson.Gson;

/**
 * Super simple class so I can send important logging info to a webhook
 * @author Ember (schott512)
 */
public class WebhookLogManager {

    private static URL url;

    public WebhookLogManager(String webhookURL) {

        // Try to open a URL to start with, if an exception is thrown turn *off* webhook logging
        try {
            url = new URL(webhookURL);
        }
        catch (MalformedURLException e) { Configuration.writeLog = false; }

    }

    /**
     * Function for sending a message to a webhook URL
     * @param content A string, the content of the message to be logged
     */
    public static void sendMessage(String content) {

        // Attempt to connect to Webhook and send content
        try {

            // Create endpoint connection to send POST request
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");

            // Set headers
            con.setRequestProperty("Content-Type", "application/json");

            // Create JSON to house content
            JsonObject jobj = new JsonObject();
            jobj.addProperty("content", content);

            // Final build and send
            con.setDoOutput(true);
            OutputStream wr = con.getOutputStream();
            wr.write(jobj.toString().getBytes("utf-8"));

            /**
            // Read response (Commented out for now)
            int responseCode = con.getResponseCode();
            BufferedReader in;
            if (responseCode == 201) {
                in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            }
            else {
                in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine())!= null) {
                response.append(inputLine);
            }
            in.close();
            String r = response.toString();
            JsonObject jresponse = new Gson().fromJson(r, JsonObject.class);
            System.out.println(jresponse.toString());
            */

        } catch (Exception e) {}

    }
}
