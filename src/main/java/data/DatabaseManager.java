package data;

import core.autopost.AutoPost;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Class for managing AwooBot's DataBase connection
 */
public class DatabaseManager {

    // A database object, responsible for maintaining an open connection and executing sql
    private Database dbObj;

    // A map containing all cached guild entries from the DB (guild_id, cache object)
    private Map<String, GuildDBCache> guildDBCaches = new HashMap<>();

    // Maximum number of cache entries
    private int maxCache = 5;

    // List, containing guild ids. Maintains the order in which guild caches were last used
    private List<String> order = new ArrayList<>();

    public DatabaseManager(String dbName) throws Exception {

        // Initialize database Object
        dbObj = new Database(dbName, "test");

        // Read the file of database info
        String s = File.separator;
        String path = System.getProperty("user.dir")+ s + "db" + s + "DBTables.txt";

        // File reading shenanigans to get sqlite commands file into one big string ~
        FileReader fr = new FileReader(path);
        BufferedReader br = new BufferedReader(fr);
        String line = br.readLine();
        StringBuilder sb = new StringBuilder();

        while(line != null){
            sb.append(line).append("\n");
            line = br.readLine();
        }

        br.close();

        // Split the sqlite commands by double newline to get create tables commands
        String[] com_strings = sb.toString().split("\n\n");

        List<String> commands = Arrays.asList(com_strings);

        try {
            dbObj.initializeDB(commands);
        }
        catch (Exception e) { e.printStackTrace(); }

    }

    /**
     * Toggles logging of database interactions for debug purposes
     */
    public void toggleLogging() {dbObj.toggleLogStatus();}

    /**
     * Updates a setting for a guild. If forceUpdate is true it will mirror the update to the database immediately,
     * otherwise only the cache entry is updated until
     * @param gID The id of the guild whos setting is being updated
     * @param sName The name of the setting to be updated
     * @param val The new value to update the setting to
     * @param forceUpdate Whether or not the update should be immediately mirrored to the DB
     */
    public void updateSetting(String gID, String sName, Object val, boolean forceUpdate) {

        // Is the guild already in the cache? If yes awesome. Just update its cache entry. Otherwise pull it and update
        if (guildDBCaches.containsKey(gID)) { guildDBCaches.get(gID).updateSetting(sName, val); }
        else {

            addToCache(gID);
            guildDBCaches.get(gID).updateSetting(sName, val);

        }

        if (forceUpdate) { pushDB(guildDBCaches.get(gID)); }

    }

    /**
     * Retrieves a single setting from the guild settings table for that guild.
     * @param gID
     * @param sName
     * @return
     */
    public Object getSetting(String gID, String sName) {

        Object result;

        // Check the cache for this entry and retrieve the guild setting cache for ID. Grab setting value
        GuildDBCache gDBC = checkCache(gID);
        result = gDBC.getSetting(sName);
        return result;

    }

    /**
     * Grab all reactrole data associated with a messageID
     * @param mID Message ID to grab data for
     * @return
     */
    public List<Map<String, Object>> getReactRolesByMessageID(String mID) {

        // Return a db search that uses msgID
        return dbObj.searchDB("*","ReactRoles","message == '" + mID+"'","",20,"","");

    }

    /**
     * Grab all reactrole data associated with a Guild
     * @param gID Guild ID to grab data for
     * @return
     */
    public List<Map<String, Object>> getReactRolesByGuildID(String gID) {

        // Return a db search that uses guildID
        return dbObj.searchDB("*","ReactRoles","guild_id == '" + gID+"'","",9999,"","");

    }

    /**
     * Gives the list of any messages with react roles associated with a guild.
     * @param gID The guild to check associated messages for
     * @return List of strings containing message ids
     */
    public List<String> getRRMessages(String gID) {

        // Fetch the guild cache, grab its reactrole values
        GuildDBCache gDBC = checkCache(gID);
        return gDBC.getRR();

    }

    /**
     * Grab a single reactrole entry. The one associated with this unique message emote pair.
     * @param mID Message ID
     * @param eID Emote ID
     */
    public Map<String, Object> getReactRole(String mID, String eID) {

        // Search the db for a single (unique) entry composed of (messageID, emoteID). Return it if it exists, null otherwise
        List<Map<String,Object>> result = dbObj.searchDB("*","ReactRoles","message == '" + mID +"' AND emote == '" + eID+"'","",1,"","");
        if (result.size() == 0) { return null; }
        else { return result.get(0); }

    }

    /**
     * Check if a guild is cached. If not, cache it. Return the GuildDBCache
     * @param gID ID of guild to be checked
     * @return GuildDBCache object of this guild
     */
    private GuildDBCache checkCache(String gID) {

        GuildDBCache result;
        // If this guild is not cache, cache it
        if (!guildDBCaches.containsKey(gID)) { addToCache(gID); }
        result = guildDBCaches.get(gID);
        refreshGuild(gID);
        return result;

    }

    /**
     * Add a reaction role entry
     * @param gID Guild ID
     * @param mID Message ID
     * @param eID emote ID
     * @param rID Role ID
     */
    public void addReactRoleEntry(String gID, String mID, String eID, String rID) {

        // Add list of data to the DB. If it doesn't already exist, add this messageID to the GuildCache's list of mID's with reactroles enabled
        Object[] data = {gID, mID, eID, rID};
        GuildDBCache gDBC = checkCache(gID);
        if (!gDBC.getRR().contains(mID)) { gDBC.addRR(mID); }
        dbObj.addTo("ReactRoles","(guild_id, message, emote, role)", data, false);

    }

    /**
     * Remove reaction role entry.
     * @param gID Guild ID
     * @param mID Message ID
     * @param eID emote ID
     */
    public void removeReactRoleEntry(String gID, String mID, String eID) {

        // Run database remove
        dbObj.removeFrom("ReactRoles","message == '" + mID + "' AND emote == '" + eID+"'");

        // If 0 entries exist for this message ID, remove it from the guilds cached message IDs
        List<Map<String,Object>> resultSet = dbObj.searchDB("*","ReactRoles","message == '" + mID+"'","",1,"","");
        if (resultSet.size() == 0) {
            GuildDBCache gDBC = checkCache(gID);
            gDBC.removeRR(mID);
        }
    }

    /**
     * Add a guild to the cache. If this would exceed max cached number, push one out.
     * @param gID The id of the guild to add to the cache
     */
    private void addToCache(String gID) {

        // If an entry needs removed, Which entry is to be removed? (The least recently used)
        if (guildDBCaches.size() > maxCache) {
            String removal = order.get(order.size() - 1);
            order.remove(removal);

            // If the old entry has been updated, push that info to the DB before throwing out
            if (guildDBCaches.get(removal).isUpdatedInfo()) {
                pushDB(guildDBCaches.get(removal));
            }
            guildDBCaches.remove(removal);

        }

        // Push this new guild to the front of the ordered list
        order.add(0,gID);

        // Fetch the data
        List<Map<String, Object>> data = dbObj.searchDB("*", "guilds", "id == '" + gID+"'","",1,"","");
        if (data.size() == 0) { newGuild(gID); return; }
        GuildDBCache gDBC = new GuildDBCache(gID, data.get(0));
        List<Map<String,Object>> reactroleData = getReactRolesByGuildID(gID);

        // Loop through the list of rows that were retrieved
        for (Map<String, Object> rr : reactroleData) {

            // Grab the message id and add it to the list of messages with reactroles if it isn't there already
            String messageID = rr.get("message").toString();
            if (!gDBC.getRR().contains(messageID)) {
                gDBC.addRR(messageID);
            }
        }

        // Place this entry in the cache
        guildDBCaches.put(gID,gDBC);

    }

    /**
     * Pushes the info in a GuildDBCache to the actual DB
     * @param gDBC
     */
    private void pushDB(GuildDBCache gDBC) {

        // Throw the values into a list, run a DB upsert of the setting
        Object[] data = {gDBC.getSetting("id"), gDBC.getSetting("mod_role"),gDBC.getSetting("prefix")};
        dbObj.upsert("guilds", "(id, mod_role, prefix)", data, "id == '" + gDBC.guildID+"'");
        gDBC.resetUpdatedInfo();

    }

    /**
     * Adds a new guild with default data.
     * @param gID
     */
    public void newGuild(String gID) {

        // Load default data for guild and push to db
        Map<String, Object> m = new HashMap<>();
        m.put("id", gID);
        m.put("mod_role",null);
        m.put("prefix", null);
        GuildDBCache gDBC = new GuildDBCache(gID,m);
        pushDB(gDBC);

        // Push this new guild to the front of the ordered list, add to cache
        order.add(0,gID);
        guildDBCaches.put(gID,gDBC);

    }

    /**
     * Moves a guild already in the cache to the front of the ordered list, indicating it has been recently used
     */
    private void refreshGuild(String gID) { order.remove(gID); order.add(0, gID); }

    /**
     * Fetches autoposts scheduled for the next 15 minutes from the DB
     * @return
     */
    public List<AutoPost> fetchUpcomingAP() {

        List<AutoPost> fetchResults = new ArrayList<>();

        // Search db for autoposts whose time is between now and 15 minutes from now
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        String lowerLimit = now.format(DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm"));
        now = now.plusMinutes(15);
        String upperLimit = now.format(DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm"));
        String whereCondition = "post_time >= '" + lowerLimit + "' AND post_time <= '" + upperLimit+"'";

        List<Map<String,Object>> results = dbObj.searchDB("*", "AutoPosts", whereCondition, "post_time",0, "","");

        // Parse those values into a list of autopost objects
        for (Map<String,Object> row : results) {

            // Create ap and add to list
            AutoPost ap = new AutoPost((String) row.get("post_time"), (String) row.get("content"),
                                        (String) row.get("guild_id"), (String) row.get("channel_id"));

            fetchResults.add(ap);

        }

        return fetchResults;

    }

    /**
     * Adds a single instance of the Autopost class to the database
     */
    public void addAutoPost(AutoPost ap) {

        // Create object array for insertion
        Object[] data = {ap.guild,ap.contents,ap.channel,ap.postTime.format(DateTimeFormatter.ofPattern("HH:mm"))};

        // Run an insert
        dbObj.addTo("AutoPosts","(guild_id,content,channel_id,post_time)",data,true);

    }

    /**
     * Removes an autopost by using its ID
     * @param apID
     */
    public void removeAutoPost(String apID) {

        dbObj.removeFrom("AutoPosts","id == " + apID);

    }

    /**
     * Retreive an autopost by its ID
     * @param ID a string representing the ID of the autopost
     * @return A map representing a single row from the DB
     */
    public Map<String,Object> getAutoPost(String ID) {

        // Search the db for a single (unique) entry. Return it if it exists, null otherwise
        List<Map<String,Object>> result = dbObj.searchDB("*","AutoPosts","id == " + ID,"",1,"","");
        if (result.size() == 0) { return null; }
        else { return result.get(0); }

    }

    /**
     * Grab all Autopost data associated with a channelID
     * @param cID Message ID to grab data for
     * @return
     */
    public List<Map<String, Object>> getAutoPostsByChannelID(String cID) {

        // Return a db search that uses msgID
        return dbObj.searchDB("*","AutoPosts","channel_id == '" + cID+"'","",0,"","");

    }

}
