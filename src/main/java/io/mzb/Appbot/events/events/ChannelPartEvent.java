package io.mzb.Appbot.events.events;

import io.mzb.Appbot.events.Event;
import io.mzb.Appbot.twitch.Channel;
import io.mzb.Appbot.twitch.User;

public class ChannelPartEvent extends Event {

    private Channel channel;
    private User user;

    /**
     * @param channel The channel the user left from
     * @param user The user who left the channel
     */
    public ChannelPartEvent(Channel channel, User user) {
        this.channel = channel;
        this.user = user;
    }

    /**
     * @return The channel that the user left
     */
    public Channel getChannel() {
        return channel;
    }

    /**
     * @return The user who left the channel
     */
    public User getUser() {
        return user;
    }

}
