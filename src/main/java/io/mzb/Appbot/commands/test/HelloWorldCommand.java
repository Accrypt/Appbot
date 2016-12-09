package io.mzb.Appbot.commands.test;

import io.mzb.Appbot.commands.CommandHandler;
import io.mzb.Appbot.twitch.Channel;
import io.mzb.Appbot.twitch.User;

public class HelloWorldCommand implements CommandHandler {
    @Override
    public void onCommand(Channel channel, User user, String command, String[] args) {
        channel.chat("Hello " + user.getName() + "!");
        channel.chat("Channel: " + channel.getName());
        channel.chat("Command: " + command);
        String argsOut = "";
        for (String arg : args) {
            argsOut += arg + ", ";
        }
        channel.chat("Args: " + argsOut + " (" + args.length + ")");
    }
}
