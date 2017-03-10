package io.mzb.Appbot.twitch;

import io.mzb.Appbot.Appbot;
import io.mzb.Appbot.commands.CommandHandler;
import io.mzb.Appbot.events.events.ChannelJoinEvent;
import io.mzb.Appbot.events.events.ChannelPartEvent;
import io.mzb.Appbot.events.events.MessageEvent;
import io.mzb.Appbot.events.events.UserModeEvent;
import io.mzb.Appbot.twitch.util.TwitchAPI;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class Channel {

    // Holds all connected channels (name -> channel)
    private static HashMap<String, Channel> channelKeep = new HashMap<>();

    // Task id of the update thread
    private int updateTaskId = -1;

    private boolean loaded = false, valid = true, mature, partner, emoteOnly, r9k, subOnly, online = false, playlist;
    private HashMap<String, User> chatters = new HashMap<>();
    private int slow, delay, videoHeight;
    private String invalidReason, status, name, broadcast_lang, language, logoUrl, videoBannerUrl, profileBannerUrl, streamUrl, game, liveCreateTime;
    private ArrayList<Team> teams;
    // Unix time code for when follow only is enabled until
    private long followerOnly, viewers, id, viewCount, totalViews, followers;
    private double avrFps;

    /**
     * Channel init
     *
     * @param name The name of the channel
     */
    public Channel(String name) {
        this.name = name;
        channelKeep.put(name.toLowerCase(), this);
        load(null);

        enableUpdateTask();
    }

    public Channel(String name, Runnable loadCallback) {
        this.name = name;
        channelKeep.put(name.toLowerCase(), this);
        load(loadCallback);

        enableUpdateTask();
    }

    public static Collection<Channel> getConnectedChannels() {
        return channelKeep.values();
    }

    public static Channel getConnectedChannel(String name) {
        if (channelKeep.keySet().contains(name.toLowerCase())) {
            return channelKeep.get(name.toLowerCase());
        } else {
            return null;
        }
    }

    public static Channel getFirstChannel() {
        if(channelKeep.keySet().size() > 0) {
            Channel chan = null;
            for(String k : channelKeep.keySet()) {
                if(channelKeep.get(k) != null) {
                    chan = channelKeep.get(k);
                    break;
                }
            }
            return chan;
        } else {
            System.out.println("Error?! No channels found in the channel keep!");
            return null;
        }
    }

    private void load(Runnable callback) {
        Appbot.getTaskManager().runTask(() -> {
            JSONObject channelJson = TwitchAPI.CHANNEL.get(name);
            if (channelJson == null) {
                valid = false;
                invalidReason = "Channel json was null";
            } else if (channelJson.containsKey("error")) {
                valid = false;
                invalidReason = "Channel json contains error: " + channelJson.get("error").toString();
            } else {
                valid = true;
                invalidReason = "Channel is valid";
                this.game = jsonGet(channelJson, "game");
                this.mature = Boolean.valueOf(jsonGet(channelJson, "mature"));
                this.videoBannerUrl = jsonGet(channelJson, "video_banner");
                this.language = jsonGet(channelJson, "language");
                this.name = jsonGet(channelJson, "display_name");
                this.streamUrl = jsonGet(channelJson, "url");
                this.profileBannerUrl = jsonGet(channelJson, "profile_banner");
                this.partner = Boolean.valueOf(jsonGet(channelJson, "partner"));
                this.broadcast_lang = jsonGet(channelJson, "broadcaster_language");
                this.logoUrl = jsonGet(channelJson, "logo");
                this.id = Integer.valueOf(jsonGet(channelJson, "_id"));
                this.totalViews = Integer.valueOf(jsonGet(channelJson, "views"));
                this.status = jsonGet(channelJson, "status");


                JSONObject followJson = TwitchAPI.FOLLOWS.get(name);
                this.followers = Integer.valueOf(jsonGet(followJson, "_total"));

                updateStreamInfo();
            }

            loaded = true;
            if (callback != null) {
                callback.run();
            }
        });
        startUpdateTask();
    }

    private String jsonGet(JSONObject obj, String key) {
        if (obj == null) {
            return null;
        } else if (obj.get(key) == null) {
            return null;
        } else {
            return obj.get(key).toString();
        }
    }

    private void startUpdateTask() {
        this.updateTaskId = Appbot.getTaskManager().runTask(() -> {

            /* TODO: Add this again to remove users that are not no longer present in channel to save memory
             Include an inital creation time for the user so they don't get removed if they were added to
             the user list from a chat message but are not showing on this api yet. */
            /*JSONObject chat = TwitchAPI.CHATTERS.get(name);
            if (chat == null) {
                System.err.println("Error: Channel update task failed to get chatter json!");
                return;
            }
            JSONObject chatter = (JSONObject) chat.get("chatters");
            ArrayList<String> allNames = new ArrayList<>();
            JSONArray mods = (JSONArray) chatter.get("moderators");
            JSONArray staff = (JSONArray) chatter.get("staff");
            JSONArray admins = (JSONArray) chatter.get("admins");
            JSONArray global_mods = (JSONArray) chatter.get("global_mods");
            JSONArray viewers = (JSONArray) chatter.get("viewers");

            // Remove old users
            ArrayList<String> remove = new ArrayList<>();
            for (String key : chatters.keySet()) {
                if (!allNames.contains(key)) {
                    remove.add(key);
                }
            }
            for (String rm : remove) {
                onUserLeaveChat(chatters.get(rm));
                chatters.remove(rm);
            }
            */

            updateStreamInfo();

        }, 0, 1000 * 20);
    }

    private void updateStreamInfo() {
        JSONObject stream = TwitchAPI.STREAM.get(getName());
        if(stream == null || stream.get("stream") == null) {
            online = false;
            liveCreateTime = null;
            viewers = 0;
            avrFps = 0.0D;
            delay = 0;
            videoHeight = 0;
            return;
        } else {
            JSONObject streamInfo = (JSONObject) stream.get("stream");
            online = true;
            game = streamInfo.get("game").toString();
            viewers = Integer.parseInt(streamInfo.get("viewers").toString());
            avrFps = Double.parseDouble(streamInfo.get("average_fps").toString());
            delay = Integer.parseInt(streamInfo.get("delay").toString());
            videoHeight = Integer.parseInt(streamInfo.get("video_height").toString());
            playlist = Boolean.parseBoolean(streamInfo.get("is_playlist").toString());
            liveCreateTime = streamInfo.get("created_at").toString();
        }
    }

    private ArrayList<String> updateLocal(JSONArray names, LocalRank rank) {
        ArrayList<String> allNames = new ArrayList<>();
        if (names == null) {
            return allNames;
        }
        for (Object key : names) {
            allNames.add(key.toString());
            if (chatters.containsKey(key.toString())) {
                if (chatters.get(key.toString()).getLocalRank() != rank) {
                    if (key.toString().equalsIgnoreCase(getName())) {
                        chatters.get(key.toString()).setLocalRank(LocalRank.OWNER);
                        continue;
                    }
                    chatters.get(key.toString()).setLocalRank(rank);
                }
            } else {
                User user = new User(key.toString(), getName());
                if (user.getName().equalsIgnoreCase(getName())) {
                    user.setLocalRank(LocalRank.OWNER);
                } else {
                    user.setLocalRank(rank);
                }
                chatters.put(key.toString(), user);
                onUserEnterChat(user);
            }
        }
        return allNames;
    }

    private ArrayList<String> updateGlobal(JSONArray names, GlobalRank rank) {
        ArrayList<String> allNames = new ArrayList<>();
        if (names == null) {
            return allNames;
        }
        for (Object key : names) {
            allNames.add(key.toString());
            if (chatters.containsKey(key.toString())) {
                if (!chatters.get(key.toString()).getGlobalRanks().contains(rank)) {
                    chatters.get(key.toString()).getGlobalRanks().clear();
                    chatters.get(key.toString()).getGlobalRanks().add(rank);
                }
            } else {
                User user = new User(key.toString(), getName());
                if (user.getName().equalsIgnoreCase(getName())) {
                    user.setLocalRank(LocalRank.OWNER);
                } else {
                    user.setLocalRank(LocalRank.USER);
                }
                chatters.put(key.toString(), user);
                onUserEnterChat(user);
            }
        }
        return allNames;
    }

    public void disableUpdates() {
        Appbot.getTaskManager().killTask(updateTaskId);
        this.updateTaskId = -1;
    }

    public void enableUpdateTask() {
        if (this.updateTaskId == -1) {
            startUpdateTask();
        }
    }

    private void onUserEnterChat(User user) {
        System.out.printf("[User] Type: [Join] Name: [%s] Channel[%s] Rank: [%s]", user.getName(), getName(), user.getLocalRank().getDisplayName());
    }

    public void onUserLeaveChat(User user) {
        System.out.printf("[User] Type: [Part] Name: [%s] Channel[%s] Rank: [%s]", user.getName(), getName(), user.getLocalRank().getDisplayName());
    }

    public boolean isLoaded() {
        return loaded;
    }

    public boolean isValid() {
        return valid;
    }

    public String getInvalidReason() {
        return invalidReason;
    }

    public boolean isMature() {
        return mature;
    }

    public boolean isPartner() {
        return partner;
    }

    public Collection<User> getChatters() {
        return chatters.values();
    }

    public boolean hasChatter(String name) {
        return chatters.containsKey(name);
    }

    public User getChatter(String name) {
        if(hasChatter(name)) {
            System.out.println("Have chatter!");
            return chatters.get(name);
        } else {
            System.out.println("No have user, make da user");
            onUserJoin(name);
            return chatters.get(name);
        }
    }

    public long getViewCount() {
        return viewCount;
    }

    public long getTotalViews() {
        return totalViews;
    }

    public long getFollowers() {
        return followers;
    }

    public long getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public String getGame() {
        return game;
    }

    public String getLanguage() {
        return language;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public String getVideoBannerUrl() {
        return videoBannerUrl;
    }

    public String getProfileBannerUrl() {
        return profileBannerUrl;
    }

    public String getStreamUrl() {
        return streamUrl;
    }

    public ArrayList<Team> getTeams() {
        return teams;
    }

    public boolean isEmoteOnly() {
        return emoteOnly;
    }

    public void setEmoteOnly(boolean emoteOnly) {
        this.emoteOnly = emoteOnly;
    }

    public long getFollowerOnlyExpireTime() {
        return followerOnly;
    }

    public void setFollowerOnlyExpireTime(long followerOnly) {
        this.followerOnly = followerOnly;
    }

    public boolean isR9k() {
        return r9k;
    }

    public void setR9k(boolean r9k) {
        this.r9k = r9k;
    }

    public boolean isSubOnly() {
        return subOnly;
    }

    public void setSubOnly(boolean subOnly) {
        this.subOnly = subOnly;
    }

    public int getSlow() {
        return slow;
    }

    public void setSlow(int slow) {
        this.slow = slow;
    }

    public String getBroadcastLang() {
        return broadcast_lang;
    }

    public void setBroadcastLang(String broadcast_lang) {
        this.broadcast_lang = broadcast_lang;
    }

    public boolean isOnline() {
        return online;
    }

    public boolean isPlaylist() {
        return playlist;
    }

    public int getDelay() {
        return delay;
    }

    public int getVideoHeight() {
        return videoHeight;
    }

    public String getLiveCreateTime() {
        return liveCreateTime;
    }

    public double getAverageFps() {
        return avrFps;
    }

    public void joinIrc() {
        Appbot.getIrcHandler().send(getName(), "JOIN #" + getName().toLowerCase());
    }

    public void quitIrc() {
        Appbot.getIrcHandler().send(getName(), "PART #" + getName().toLowerCase());
    }

    public void chat(String message) {
        if (Appbot.getIrcHandler() == null || getName() == null || message == null) {
            return;
        }
        Appbot.getIrcHandler().send(getName(), "PRIVMSG #" + getName().toLowerCase() + " :" + message);
    }

    public void onUserMode(String name, boolean mod) {
        if (name.equalsIgnoreCase(getName())) {
            if (chatters.containsKey(name)) {
                chatters.get(name).setLocalRank(LocalRank.OWNER);
                System.out.printf("[%s] Mode - Name: [%s] Rank[%s]", getName(), name, LocalRank.OWNER.getDisplayName());
                Appbot.getEventManager().callEvent(new UserModeEvent(this, chatters.get(name), LocalRank.OWNER));
            } else {
                onUserJoin(name);
                chatters.get(name).setLocalRank(LocalRank.OWNER);
                System.out.printf("[%s] Mode - Name: [%s] Rank[%s]", getName(), name, LocalRank.OWNER.getDisplayName());
                Appbot.getEventManager().callEvent(new UserModeEvent(this, chatters.get(name), LocalRank.OWNER));
            }
            return;
        }
        for (User user : getChatters()) {
            if (user.getName().equalsIgnoreCase(name)) {
                user.setLocalRank((mod ? LocalRank.MOD : LocalRank.USER));
                System.out.printf("[%s] Mode - Name: [%s] Rank[%s]", getName(), name, (mod ? LocalRank.MOD : LocalRank.USER).getDisplayName());
                Appbot.getEventManager().callEvent(new UserModeEvent(this, chatters.get(name), user.getLocalRank()));
                return;
            }
        }
        User user = new User(name, getName());
        user.setLocalRank((mod ? LocalRank.MOD : LocalRank.USER));
        System.out.printf("[%s] Mode - Name: [%s] Rank[%s]", getName(), name, (mod ? LocalRank.MOD : LocalRank.USER).getDisplayName());
        Appbot.getEventManager().callEvent(new UserModeEvent(this, chatters.get(name), user.getLocalRank()));
    }

    public void onUserJoin(String name) {
        if (!chatters.keySet().contains(name)) {
            chatters.put(name, new User(name, getName()));
            System.out.printf("[%s] Join - Name: [%s]", getName(), name);
            Appbot.getEventManager().callEvent(new ChannelJoinEvent(this, chatters.get(name)));
        }
    }

    public void onUserQuit(String name) {
        if (chatters.keySet().contains(name)) {
            System.out.println(String.format("[%s] Part - Name: [%s]", getName(), name));
            Appbot.getEventManager().callEvent(new ChannelPartEvent(this, chatters.get(name)));
            chatters.remove(name);
        }
    }

    public void onMessage(User user, String message, String emotes, int bits) {
        if (!chatters.containsKey(user.getName())) {
            chatters.put(user.getName(), user);
            System.out.printf("[%s] Join - Name: [%s]", getName(), name);
            Appbot.getEventManager().callEvent(new ChannelJoinEvent(this, chatters.get(name)));
        }
        if (message.startsWith("!")) {
            System.out.println(String.format("[%s] Cmd - Name: [%s] Command: [%s]", getName(), name, message));
            String command = message.split(" ")[0];
            message = message.replaceFirst(command, "").trim();
            command = command.substring(1, command.length());
            System.out.println("Message before arg split: " + message);
            String[] args = (message.equalsIgnoreCase(" ") ? new String[0] : message.split(" "));
            CommandHandler handler = Appbot.getCommandManager().getCommandHandler(command);
            if (handler != null) {
                if (!user.isLoaded()) {
                    String finalCommand = command;
                    user.reload(() -> handler.onCommand(Channel.this, user, finalCommand, args));
                } else {
                    handler.onCommand(this, chatters.get(name), command, args);
                }
            }
        } else {
            System.out.println(String.format("[%s] Msg - Name: [%s] Message: [%s] Emotes: [%s] Bits: [%d]", getName(), name, message, emotes, bits));
            Appbot.getEventManager().callEvent(new MessageEvent(this, chatters.get(name), message, emotes, bits));
        }
    }

}
