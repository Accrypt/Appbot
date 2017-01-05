package io.mzb.Appbot.twitch.irc;

import io.mzb.Appbot.Appbot;
import io.mzb.Appbot.events.events.TwitchPingEvent;
import io.mzb.Appbot.twitch.Channel;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

class IrcIn {

    // Reads the actual incoming data
    private BufferedReader in;

    /**
     * Inits the irc input handler
     *
     * @param inputStream The stream of data from the irc socket
     */
    IrcIn(InputStream inputStream) {
        this.in = new BufferedReader(new InputStreamReader(inputStream));
        startReading();
    }

    /**
     * Starts reading inputs from the buffered reader
     * Should only be called once
     */
    private void startReading() {
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

    /**
     * Handles an input from the irc
     *
     * @param input the incoming line from the socket
     */
    private void handleInput(String input) {
        // Send every now and again to make sure the application is still alive, calls an event
        if (input.startsWith("PING :tmi.twitch.tv")) {
            handlePing(input);
        } else if (input.startsWith(":jtv MODE")) {
            handleMode(input);
        } else if (input.contains(".tmi.twitch.tv JOIN") && !input.contains("PRIVMSG")) {
            handleJoin(input);
        } else if (input.contains(".tmi.twitch.tv PART") && !input.contains("PRIVMSG")) {
            handlePart(input);
        } else if (input.contains(".tmi.twitch.tv PRIVMSG")) {
            handlePrivmsg(input);
        }
    }

    /**
     * Handles a ping input
     * @param input The original input from the socket
     */
    private void handlePing(String input) {
        Appbot.getIrcHandler().send(Appbot.getDefaultChannel().getName(), "PONG :tmi.twitch.tv");
        Appbot.getEventManager().callEvent(new TwitchPingEvent());
    }

    /**
     * Handles a mode input
     * @param input The original input from the socket
     */
    private void handleMode(String input) {
        String endChar = (input.contains("+o") ? "+o" : "-o");
        String channel = input.substring(11, input.indexOf(endChar) - 1);
        String user = input.substring(input.indexOf(endChar) + 3, input.length());
        Channel chan = Channel.getConnectedChannel(channel);
        if (chan != null) {
            chan.onUserMode(user, endChar.equals("+o"));
        }
    }

    /**
     * Handles a join input from the socket
     * @param input The original input from the socket
     */
    private void handleJoin(String input) {
        String name = input.substring(1, input.indexOf("!"));
        String channel = input.substring(input.indexOf("#") + 1, input.length());
        Channel chan = Channel.getConnectedChannel(channel);
        if (chan != null)
            chan.onUserJoin(name);
    }

    /**
     * Handles a part input from the socket
     * @param input The original input from the socket
     */
    private void handlePart(String input) {
        String name = input.substring(1, input.indexOf("!"));
        String channel = input.substring(input.indexOf("#") + 1, input.length());
        Channel chan = Channel.getConnectedChannel(channel);
        if(chan != null) {
            chan.onUserQuit(name);
        }
    }

    /**
     * Handles a private message from the socket (Any chat message)
     * @param input The original input from the socket
     */
    private void handlePrivmsg(String input) {
        String name = input.substring(1, input.indexOf("!"));
        String chanPlusMsg = input.substring(27 + (3 * name.length()), input.length());
        String channel = chanPlusMsg.substring(0, chanPlusMsg.indexOf(":") - 1);
        String msg = chanPlusMsg.substring(channel.length() + 2, chanPlusMsg.length());
        Channel chan = Channel.getConnectedChannel(channel);
        if(chan != null) {
            chan.onMessage(name, msg);
        }
    }

    /**
     * Prints the irc input to the console
     * Also sets thread name to "IRC-I" (IRC Input)
     * @param x The object to be printed, converts to a string
     */
    private void printOut(Object x) {
        Thread.currentThread().setName("IRC-I");
        System.out.println("[IRC] <- " + x.toString());
    }

}
