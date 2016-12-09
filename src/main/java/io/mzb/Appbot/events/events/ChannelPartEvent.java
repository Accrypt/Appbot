package io.mzb.Appbot.events.events;

import io.mzb.Appbot.events.Event;
import io.mzb.Appbot.twitch.Channel;
import io.mzb.Appbot.twitch.User;

public class ChannelPartEvent extends Event {

    private Channel channel;
    private User user;

    public ChannelPartEvent(Channel channel, User user) {
        this.channel = channel;
        this.user = user;
    }

    public Channel getChannel() {
        return channel;
    }

    public User getUser() {
        return user;
    }

}
