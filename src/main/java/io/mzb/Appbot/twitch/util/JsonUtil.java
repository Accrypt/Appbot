package io.mzb.Appbot.twitch.util;

import com.sun.istack.internal.NotNull;
import io.mzb.Appbot.Appbot;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class JsonUtil {

    /**
     * Returns the json in an object from a website
     *
     * @param url The url of the json
     * @return Json object from the website
     */
    static JSONObject getJsonFromUrl(String url) {
        String fi;
        Object obj;
        try {
            // Get the json as a string from the url
            fi = JsonUtil.urlToString(url);
            // Parse the json string into a json object
            JSONParser parser = new JSONParser();
            obj = parser.parse(fi);
        } catch (Exception e) {
            return null;
        }
        return (JSONObject) obj;
    }

    /**
     * Get the contents of a website as a string
     * Auto accepts twitch version 3 api and provides client id automatically
     *
     * @param urlString The url for the request
     * @return String of content of the website
     * @throws IOException Failed to read website
     */
    private static String urlToString(@NotNull String urlString) throws IOException {
        // Check if being ran on main thread
        checkMainThread();
        StringBuilder response;
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.2; WOW64)");

        // Set header information required for twitch
        connection.setRequestProperty("Accept", "application/vnd.twitchtv.v3+json");
        connection.setRequestProperty("Client-ID", Appbot.getClientId());
        connection.setConnectTimeout(10000);

        // If the request returns anything but 200 (OK), print the error for debugging
        if (connection.getResponseCode() != 200) {
            System.err.println("Error while getting json data from " + connection.getURL().toString());
            System.err.println("Response code: " + connection.getResponseCode() + ", Message: " + connection.getResponseMessage());
        }

        // Start converting the url response into a string
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        response = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        // Close the connection
        in.close();

        // Return the response as a string
        return response.toString();
    }

    /**
     * Prints error to the console when called from the main thread
     */
    private static void checkMainThread() {
        long currentThreadId = Thread.currentThread().getId();
        if (currentThreadId == (long) 1) {
            System.err.println("Network connection on main thread! Please report this as it holds up the entire program! " + currentThreadId + " = 1");
        }
    }

}
