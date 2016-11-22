package io.mzb.AppBot.twitch.util;

import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

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
