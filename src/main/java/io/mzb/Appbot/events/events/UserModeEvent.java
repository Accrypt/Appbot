package io.mzb.Appbot.events.events;

import io.mzb.Appbot.events.Event;
import io.mzb.Appbot.twitch.Channel;
import io.mzb.Appbot.twitch.LocalRank;
import io.mzb.Appbot.twitch.User;

public class UserModeEvent extends Event {

    private Channel channel;
    private User user;
    private LocalRank newRank;

    public UserModeEvent(Channel channel, User user, LocalRank newRank) {
        this.channel = channel;
        this.user = user;
        this.newRank = newRank;
    }

    public Channel getChannel() {
        return channel;
    }

    public User getUser() {
        return user;
    }

    public LocalRank getNewRank() {
        return newRank;
    }
}
