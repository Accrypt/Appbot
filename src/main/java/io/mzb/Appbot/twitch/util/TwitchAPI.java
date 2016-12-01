package io.mzb.Appbot.twitch.util;

import org.json.simple.JSONObject;

public enum TwitchAPI {

    CHANNEL("https://api.twitch.tv/kraken/channels/%s"),
    FOLLOWS("https://api.twitch.tv/kraken/channels/%s/follows");

    private String directory;

    TwitchAPI(String directory) {
        this.directory = directory;
    }

    public JSONObject get(String channel) {
        return JsonUtil.getJsonFromUrl(String.format(directory, channel.toLowerCase()));
    }

}
