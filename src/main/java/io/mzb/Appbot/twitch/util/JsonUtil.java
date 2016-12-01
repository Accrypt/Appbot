package io.mzb.Appbot.twitch.util;

import io.mzb.Appbot.Appbot;
import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class JsonUtil {

    public static JSONObject getJsonFromUrl(String url) {
        String fi;
        try {
            fi = JsonUtil.urlToString(url);
        } catch (Exception e) {
            return null;
        }
        JSONParser parser = new JSONParser();
        Object obj;
        try {
            obj = parser.parse(fi);
        } catch (Exception e) {
            return null;
        }
        JSONObject jsonObject = (JSONObject) obj;
        return jsonObject;
    }

    public static String urlToString(String url, String username, String password) throws IOException {
        checkMainThread();
        StringBuilder response = null;
        URL urll = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) urll.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.2; WOW64)");
        if ((username != null) && (password != null)) {
            String login = username + ":" + password;
            String encodedLogin = Base64.encodeBase64URLSafeString(login.getBytes());
            connection.setRequestProperty("Authorization", "Basic " + encodedLogin);
        }
        connection.setRequestProperty("Accept", "application/vnd.twitchtv.v3+json");
        connection.setRequestProperty("Client-ID", Appbot.getClientId());
        connection.setConnectTimeout(10000);

        if (connection.getResponseCode() != 200) {
            System.err.println("Error while getting json data from " + connection.getURL().toString());
            System.err.println("Response code: " + connection.getResponseCode() + ", Message: " + connection.getResponseMessage());
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        response = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

    public static String urlToString(String url) throws IOException {
        return urlToString(url, null, null);
    }

    private static void checkMainThread() {
        long currentThreadId = Thread.currentThread().getId();
        if (currentThreadId == (long) 1) {
            System.err.println("Network connection on main thread! Please report this as it holds up the entire program! " + currentThreadId + " = 1");
        }
    }

}
