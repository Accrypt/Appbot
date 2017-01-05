package io.mzb.Appbot.twitch.irc;

import io.mzb.Appbot.Appbot;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class IRCHandler {

    // Holds messages in a queue so they don't send too quickly and get the bot banned
    private HashMap<String, ArrayList<String>> sendQueue;

    // Twitch connection info
    private static final String SERVER = "irc.twitch.tv";
    private static final int PORT = 6667;

    // Handles input from twitch
    private IrcIn in;
    // Handles output from twitch
    private IrcOut out;

    // Socket that connects to twitch
    private Socket socket;

    /**
     * Irc handler init, inits the send queue
     */
    public IRCHandler() {
        sendQueue = new HashMap<>();
    }

    /**
     * Adds a message to the send queue
     * @param channel The channel the message is going to
     * @param message The message that should be sent
     */
    public void send(String channel, String message) {
        if(sendQueue.containsKey(channel)) {
            sendQueue.get(channel).add(message);
        } else {
            sendQueue.put(channel, new ArrayList<>());
            sendQueue.get(channel).add(message);
        }
    }

    /**
     * Starts the connection to twitch
     * Should only be called once
     */
    public void connect() {
        // Inits the socket and irc in/out on a new thread
        Appbot.getTaskManager().runTask(() -> {
            try {
                Socket socket = new Socket(SERVER, PORT);
                out = new IrcOut(socket.getOutputStream());
                in = new IrcIn(socket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        // Starts a looping thread, repeats every 1.5 seconds to avoid gettings banned
        Appbot.getTaskManager().runTask(() -> {
            if(sendQueue.size() > 0) {
                for(String key : sendQueue.keySet()) {
                    if(sendQueue.get(key).size() > 0) {
                        String msg = sendQueue.get(key).get(0);
                        if (out != null) {
                            out.println(msg);
                            sendQueue.get(key).remove(0);
                        }
                    }
                }
            }
        }, 1000 * 2, (1000 * 30) / 20);
    }

    /**
     * Sends authentication information to the irc server
     */
    public void sendAuth() {
        send(Appbot.getDefaultChannel().getName(), "PASS " + Appbot.getOAuthToken());
        send(Appbot.getDefaultChannel().getName(), "NICK " + Appbot.getName());
    }

}
