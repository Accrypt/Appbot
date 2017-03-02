package io.mzb.Appbot.twitch;

import io.mzb.Appbot.Appbot;
import io.mzb.Appbot.twitch.util.TwitchAPI;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class User {

    /*
    Each instance of the user class belongs to a channel, a user can not exist outside of a channel.
    If user information is needed the channel will default to appbot as no one other than appbot will be ranked in that channel.
     */

    // Is the user information loaded from twitch
    private boolean loaded = false;
    // What rank does the user have in their channel
    private LocalRank localRank;
    // List of all ranks that apply to the users accross all channels (CURRENTLY NOT IN USE)
    private ArrayList<GlobalRank> globalRanks;
    // List of badges the user has in the chat, this may not always reflect their ranking
    private ArrayList<Badges> badges;
    // The name of the user
    private String name;
    // The name of the channel they are part of
    private String channel;
    // User display name, uses correct capitals
    private String displayName;
    // String date of when user was created and last updated
    private String dateCreated, dateUpdated;
    // The url of their logo
    private String logoUrl;
    // The user bio
    private String bio;
    // Is the user a subscriber to current channel
    private boolean subscriber = false;
    // The display color (RGB) of the user
    private int color;
    // The user id
    private long userId;
    // The id of the room they are in
    private long roomId;

    /**
     * Init user for a specific channel
     *
     * @param name    The name of the user
     * @param channel The name of the channel
     */
    public User(String name, String channel) {
        this.name = name;
        this.channel = channel;
        loadData(null);
    }

    /**
     * Init user without a specific channel
     * Used of the channel is no needed
     * The channel will default to appbot
     *
     * @param name The name of the user to get information for
     */
    public User(String name) {
        this.channel = "appbot";
        this.name = name;
        loadData(null);
    }

    /**
     * Init a user with a channel and run a callback after it is loaded
     *
     * @param name         The name of the user
     * @param channel      The name of the channel
     * @param loadCallback Ran after the user is loaded
     */
    public User(String name, String channel, Runnable loadCallback) {
        this.name = name;
        this.channel = channel;
        loadData(loadCallback);
    }

    /**
     * Init a user without a channel and run a callback after they are loaded
     * The channel will default to appbot
     *
     * @param name         The name of the user
     * @param loadCallback Ran after the user is loaded
     */
    public User(String name, Runnable loadCallback) {
        this.channel = "appbot";
        this.name = name;
        loadData(loadCallback);
    }

    /**
     * Loads the user data from twitch
     * If user init was given a load callback that will be ran after this is done
     *
     * @param loadCallback called after the user data is loaded
     */
    private void loadData(Runnable loadCallback) {
        Appbot.getTaskManager().runTask(() -> {
            System.out.println("Loading user data for " + getName() + " in " + channel);
            JSONObject user = TwitchAPI.USER.get(getName());

            if (user != null) {
                if (!user.containsKey("error")) {
                    if (user.get("name") != null)
                        this.name = user.get("name").toString();
                    if (user.get("created_at") != null)
                        this.dateCreated = user.get("created_at").toString();
                    if (user.get("updated_at") != null)
                        this.dateUpdated = user.get("updated_at").toString();
                    if (user.get("logo") != null)
                        this.logoUrl = user.get("logo").toString();
                    if (user.get("_id") != null)
                        this.userId = Long.valueOf(user.get("_id").toString());
                    if (user.get("display_name") != null)
                        this.displayName = user.get("display_name").toString();
                    if (user.get("bio") != null)
                        this.bio = user.get("bio").toString();
                } else {
                    System.out.println("Failed to load user data for " + name + ". Error: " + user.get("error").toString());
                }
            } else {
                System.out.println("Failed to load user data for " + name + ". Json is null!");
            }

            loaded = true;
            if (loadCallback != null) {
                loadCallback.run();
            }
        });
    }

    /**
     * @return true when the user has been loaded
     */
    public boolean isLoaded() {
        return loaded;
    }

    /**
     * @return The local rank the user contains in the channel they are assigned to
     */
    public LocalRank getLocalRank() {
        return localRank;
    }

    /**
     * Not to be called by plugins!
     * Does not actually change the user rank on twitch
     *
     * @param localRank Sets the local rank of the user
     */
    public void setLocalRank(LocalRank localRank) {
        this.localRank = localRank;
    }

    public void setGlobalRanks(ArrayList<GlobalRank> ranks) {
        this.globalRanks = ranks;
    }

    /**
     * @return List of all global ranks the user has (Staff, Turbo, etc.)
     */
    public ArrayList<GlobalRank> getGlobalRanks() {
        return globalRanks;
    }

    /**
     * @return The users name, lower case
     */
    public String getName() {
        return name;
    }

    /**
     * @return The name of the channel the user belongs to, channel may not be loaded
     */
    public String getChannel() {
        return channel;
    }

    /**
     * @return The users display name, case sensitive
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @return String date of when the user was created
     */
    public String getDateCreated() {
        return dateCreated;
    }

    /**
     * @return String date of when the user was last updated
     */
    public String getDateUpdated() {
        return dateUpdated;
    }

    /**
     * @return String url of the users logo
     */
    public String getLogoUrl() {
        return logoUrl;
    }

    /**
     * @return The users bio
     */
    public String getBio() {
        return bio;
    }

    /**
     * Sets the users badges, shouldn't be called by plugins
     */
    public void setBadges(ArrayList<Badges> badges) {
        this.badges = badges;
    }

    /**
     * @return List of badges the user has in the channel, does not reflect rank
     */
    public ArrayList<Badges> getBadges() {
        return badges;
    }

    /**
     * @return is the user subscriber to the current channel
     */
    public boolean isSubscriber() {
        return subscriber;
    }

    /**
     * Set the user as a subscriber (Not to be used by plugins)
     */
    public void setSubscriber(boolean subscriber) {
        this.subscriber = subscriber;
    }

    /**
     * @return The RGB color of the user
     */
    public int getColor() {
        return color;
    }

    /**
     * Set the color of the user (Not to be called by plugins)
     */
    public void setColor(int color) {
        this.color = color;
    }

    /**
     * @return The id of the user
     */
    public long getUserId() {
        return userId;
    }

    /**
     * Set the user id (not to be called by plugins)
     *
     * @param userId The id of the user
     */
    public void setUserId(long userId) {
        this.userId = userId;
    }

    /**
     * @return Get the room id
     */
    public long getRoomId() {
        return roomId;
    }

    /**
     * Set the room id (Not to be called by plugins)
     *
     * @param roomId The id of the room
     */
    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public void sendWhisper(String msg) {
        Channel.getFirstChannel().chat(".w " + getName() + " " + msg);
    }


    /**
     * Reload the users data
     */
    public void reload() {
        loadData(null);
    }

    /**
     * Reload the users data
     *
     * @param loadCallback Called after the data has been reloaded
     */
    public void reload(Runnable loadCallback) {
        loadData(loadCallback);
    }

}
