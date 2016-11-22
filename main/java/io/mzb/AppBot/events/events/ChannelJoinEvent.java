package io.mzb.AppBot.events.events;

import io.mzb.AppBot.events.Event;
import io.mzb.AppBot.twitch.Channel;
import io.mzb.AppBot.twitch.User;

public class ChannelJoinEvent extends Event {

    private Channel channel;
    private User user;

    public ChannelJoinEvent(Channel channel, User user) {
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
