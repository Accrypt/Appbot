package io.mzb.Appbot.commands;

import io.mzb.Appbot.twitch.Channel;
import io.mzb.Appbot.twitch.User;

public interface CommandHandler {

    void onCommand(Channel channel, User user, String command, String[] args);

}
