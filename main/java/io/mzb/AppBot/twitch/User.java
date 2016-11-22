package io.mzb.AppBot.twitch;

import io.mzb.AppBot.AppBot;

import java.util.ArrayList;

public class User {

    /*
    Each instance of the user class belongs to a channel, a user can not exist outside of a channel.
    If user information is needed the channel will default to appbot as no one other than appbot will be ranked in that channel.
     */

    private boolean loaded = false;
    private Runnable loadCallback;
    private LocalRank localRank;
    private ArrayList<GlobalRank> globalRanks;
    private String name;
    private String channel;

    /*
    User init for channel
     */
    public User(String name, String channel) {
        this.name = name;
        this.channel = channel;
    }

    /*
    User init without specific channel
     */
    public User(String name) {
        this.channel = "appbot";
        this.name = name;
    }

    /*
    User init for channel with load callback
     */
    public User(String name, String channel, Runnable loadCallback) {
        this.name = name;
        this.channel = channel;
        this.loadCallback = loadCallback;
    }

    /*
    User init without specific channel with load callback
     */
    public User(String name, Runnable loadCallback) {
        this.channel = "appbot";
        this.name = name;
        this.loadCallback = loadCallback;
    }

    public void loadData() {
        AppBot.getTaskManager().runTask(new Runnable() {
            public void run() {
                // TODO: Get user information from the twitch ip, set loaded to true after data is got

                loaded = true;
                if(loadCallback != null) {
                    loadCallback.run();
                }
            }
        });
    }

    public boolean isLoaded() {
        return loaded;
    }

    public LocalRank getLocalRank() {
        return localRank;
    }

    // This does not update them on twitch! This should only be set when twitch displays this as their rank!
    public void setLocalRank(LocalRank localRank) {
        this.localRank = localRank;
    }

    public ArrayList<GlobalRank> getGlobalRanks() {
        return globalRanks;
    }

    public String getName() {
        return name;
    }

    public String getChannel() {
        return channel;
    }
}
