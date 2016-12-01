package io.mzb.Appbot.twitch.irc;

import io.mzb.Appbot.Appbot;
import io.mzb.Appbot.events.events.ChannelJoinEvent;
import io.mzb.Appbot.twitch.Channel;
import io.mzb.Appbot.twitch.User;

import java.io.*;

public class IrcIn {

    private BufferedReader in;

    public IrcIn(InputStream inputStream) {
        this.in = new BufferedReader(new InputStreamReader(inputStream));
        startReading();
    }

    public void startReading() {
        Appbot.getTaskManager().runTask(() -> {
            try {
                String msg;
                while ((msg = in.readLine()) != null) {
                    printOut(msg);
                    handleInput(msg);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void handleInput(String input) {
        if(input.startsWith("PING :tmi.twitch.tv")) {
            Appbot.getIrcHandler().send(Appbot.getDefaultChannel().getName(), "PONG :tmi.twitch.tv");
        } else if (input.startsWith(":jtv MODE")) {
            String endChar = (input.contains("+o") ? "+o" : "-o");
            String channel = input.substring(11, input.indexOf(endChar) - 1);
            String user = input.substring(input.indexOf(endChar) + 3, input.length());
            Channel.getConnectedChannel(channel).onUserMode(user, (endChar.equals("+o") ? true : false));
        } else if (input.contains(".tmi.twitch.tv JOIN") && !input.contains("PRIVMSG")) {
            String name = input.substring(1, input.indexOf("!"));
            String channel = input.substring(input.indexOf("#") + 1, input.length());
            Appbot.getEventManager().callEvent(new ChannelJoinEvent(Channel.getConnectedChannel(channel), new User(name)));
        } else if (input.contains(".tmi.twitch.tv PART") && !input.contains("PRIVMSG")) {
            String name = input.substring(1, input.indexOf("!"));
            String channel = input.substring(input.indexOf("#") + 1, input.length());
            Channel.getConnectedChannel(channel).onUserQuit(name);
        } else if (input.contains(".tmi.twitch.tv PRIVMSG")) {
            String name = input.substring(1, input.indexOf("!"));
            String chanPlusMsg = input.substring(27 + (3 * name.length()), input.length());
            String channel = chanPlusMsg.substring(0, chanPlusMsg.indexOf(":") - 1);
            String msg = chanPlusMsg.substring(channel.length() + 2, chanPlusMsg.length());
            System.out.printf("Priv Msg: Chan: [%s] Msg: [%s]", channel, msg);
        }
    }

    private void printOut(Object x) {
        Thread.currentThread().setName("IRC-I");
        System.out.println("[IRC] <- " + x.toString());
    }

}
