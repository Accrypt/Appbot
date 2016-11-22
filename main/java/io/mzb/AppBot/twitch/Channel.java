package io.mzb.AppBot.twitch;

import io.mzb.AppBot.AppBot;
import io.mzb.AppBot.twitch.util.TwitchAPI;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class Channel {

    private static HashMap<String, Channel> channelKeep = new HashMap<>();

    private int updateTaskId = -1;

    private boolean loaded = false, valid = true, mature, partner;
    private HashMap<String, User> chatters = new HashMap<>();
    private int viewCount, totalViews, followers, id;
    private String status, name, broadcast_lang, game, language, logoUrl, videoBannerUrl, profileBannerUrl, streamUrl;
    private ArrayList<Team> teams;

    public Channel(String name) {
        this.name = name;
        load(null);
        channelKeep.put(name.toLowerCase(), this);
    }

    public Channel(String name, Runnable loadCallback) {
        this.name = name;
        load(loadCallback);
        channelKeep.put(name.toLowerCase(), this);
    }

    public static Channel getConnectedChannel(String name) {
        if (channelKeep.keySet().contains(name.toLowerCase())) {
            return channelKeep.get(name.toLowerCase());
        } else {
            return null;
        }
    }

    private void load(Runnable callback) {
        AppBot.getTaskManager().runTask(() -> {
            JSONObject channelJson = TwitchAPI.CHANNEL.get(name);
            if (channelJson == null) {
                valid = false;
                System.out.println("Channel json is null!");
            } else if (channelJson.containsKey("error")) {
                valid = false;
                System.out.println("Channel json contains error!");
            } else {
                valid = true;
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
        this.updateTaskId = AppBot.getTaskManager().runTask(() -> {

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

            // Update ranks
            allNames.addAll(updateLocal(mods, LocalRank.MOD));
            allNames.addAll(updateLocal(staff, LocalRank.MOD));
            allNames.addAll(updateLocal(admins, LocalRank.MOD));
            allNames.addAll(updateLocal(global_mods, LocalRank.MOD));
            allNames.addAll(updateLocal(viewers, LocalRank.USER));
            allNames.addAll(updateGlobal(staff, GlobalRank.TWITCH_STAFF));
            allNames.addAll(updateGlobal(admins, GlobalRank.ADMIN));
            allNames.addAll(updateGlobal(global_mods, GlobalRank.GLOBAL_MOD));

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
            }*/
        }, 0, 1000 * 20);
    }

    private ArrayList<String> updateLocal(JSONArray names, LocalRank rank) {
        ArrayList<String> allNames = new ArrayList<String>();
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
        ArrayList<String> allNames = new ArrayList<String>();
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
        AppBot.getTaskManager().killTask(updateTaskId);
        this.updateTaskId = -1;
    }

    public void enableUpdateTask() {
        if (this.updateTaskId == -1) {
            startUpdateTask();
        }
    }

    public void onUserEnterChat(User user) {
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

    public boolean isMature() {
        return mature;
    }

    public boolean isPartner() {
        return partner;
    }

    public Collection<User> getChatters() {
        return chatters.values();
    }

    public int getViewCount() {
        return viewCount;
    }

    public int getTotalViews() {
        return totalViews;
    }

    public int getFollowers() {
        return followers;
    }

    public int getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public String getBroadcastLang() {
        return broadcast_lang;
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

    public void joinIrc() {
        AppBot.getIrcHandler().send(getName(), "JOIN #" + getName().toLowerCase());
    }

    public void quitIrc() {
        AppBot.getIrcHandler().send(getName(), "PART #" + getName().toLowerCase());
    }

    public void chat(String message) {
        AppBot.getIrcHandler().send(getName(), "PRIVMSG #" + getName().toLowerCase() + " :" + message);
    }

    public void onUserMode(String name, boolean mod) {
        if (name.equalsIgnoreCase(getName())) {
            if (chatters.containsKey(name)) {
                chatters.get(name).setLocalRank(LocalRank.OWNER);
                System.out.printf("[%s] Mode - Name: [%s] Rank[%s]", getName(), name, LocalRank.OWNER.getDisplayName());
            } else {
                User user = new User(name, getName());
                user.setLocalRank(LocalRank.OWNER);
                chatters.put(name, user);
                System.out.printf("[%s] Mode - Name: [%s] Rank[%s]", getName(), name, LocalRank.OWNER.getDisplayName());
            }
            return;
        }
        for (User user : getChatters()) {
            if (user.getName().equalsIgnoreCase(name)) {
                user.setLocalRank((mod ? LocalRank.MOD : LocalRank.USER));
                System.out.printf("[%s] Mode - Name: [%s] Rank[%s]", getName(), name, (mod ? LocalRank.MOD : LocalRank.USER).getDisplayName());
                return;
            }
        }
        User user = new User(name, getName());
        user.setLocalRank((mod ? LocalRank.MOD : LocalRank.USER));
        System.out.printf("[%s] Mode - Name: [%s] Rank[%s]", getName(), name, (mod ? LocalRank.MOD : LocalRank.USER).getDisplayName());
    }

    public void onUserJoin(String name) {
        if (!chatters.keySet().contains(name)) {
            chatters.put(name, new User(name, getName()));
            System.out.printf("[%s] Join - Name: [%s]", getName(), name);
        }
    }

    public void onUserQuit(String name) {
        if (chatters.keySet().contains(name)) {
            chatters.remove(name);
            System.out.printf("[%s] Part - Name: [%s]", getName(), name);
        }
    }

    public void onMessage(String name, String message) {
        if (message.startsWith("!")) {
            // Command
        } else {
            // Message
        }
        System.out.printf("[%s] Msg - Name: [%s] Message: [%s]", getName(), name, message);
    }

}
