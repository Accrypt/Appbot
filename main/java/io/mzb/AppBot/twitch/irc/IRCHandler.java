package io.mzb.AppBot.twitch.irc;

import io.mzb.AppBot.AppBot;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

public class IRCHandler {

    private HashMap<String, ArrayList<String>> sendQueue;

    private static final String SERVER = "irc.twitch.tv";
    private static final int PORT = 6667;

    private IrcIn in;
    private IrcOut out;

    private Socket socket;

    public IRCHandler() {
        sendQueue = new HashMap<>();
    }

    public void send(String channel, String message) {
        if(sendQueue.containsKey(channel)) {
            sendQueue.get(channel).add(message);
        } else {
            sendQueue.put(channel, new ArrayList<String>());
            sendQueue.get(channel).add(message);
        }
    }

    public void connect() {
        AppBot.getTaskManager().runTask(() -> {
            try {
                Socket socket = new Socket(SERVER, PORT);
                out = new IrcOut(socket.getOutputStream());
                in = new IrcIn(socket.getInputStream());
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        AppBot.getTaskManager().runTask(() -> {
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

    public void sendAuth() {
        send(AppBot.getDefaultChannel().getName(), "PASS " + AppBot.getOAuthToken());
        send(AppBot.getDefaultChannel().getName(), "NICK " + AppBot.getName());
    }

}
