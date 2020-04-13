package data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Object that stores all database data for a specific guild. Acts as a cache, so we're not making constant DB calls
 */
public class GuildDBCache {

    public final String guildID;

    // A map. Contains this guilds row from the settings db
    private Map<String,Object> guildSettings = null;

    // List of message IDs which have associated reactrole data within this guild
    private List<String> reactroles = new ArrayList<>();

    // Boolean indicating if any updates have been made to settings or reactroles
    private boolean updatedInfo = false;

    public GuildDBCache(String gID, Map<String, Object> gSet) {
        guildID = gID;
        guildSettings = gSet;
    }

    public Object getSetting(String sName) { return guildSettings.get(sName); }

    public void updateSetting(String sName, Object val) {

        guildSettings.put(sName, val);
        this.updatedInfo = true;

    }

    public void addRR(String rr) { reactroles.add(rr); }
    public void removeRR(String rr) { reactroles.remove(rr); }
    public List<String> getRR() { return reactroles; }
    public boolean isUpdatedInfo() { return updatedInfo; }
    public void resetUpdatedInfo() { this.updatedInfo = false; }

}
