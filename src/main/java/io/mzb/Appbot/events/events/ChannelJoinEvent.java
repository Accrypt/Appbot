package io.mzb.Appbot.events.events;

import io.mzb.Appbot.events.Event;
import io.mzb.Appbot.twitch.Channel;
import io.mzb.Appbot.twitch.User;

public class ChannelJoinEvent extends Event {

    private Channel channel;
    private User user;

    /**
     * @param channel Channel the user joined
     * @param user The user who joined
     */
    public ChannelJoinEvent(Channel channel, User user) {
        this.channel = channel;
        this.user = user;
    }

    /**
     * @return The channel that was joined
     */
    public Channel getChannel() {
        return channel;
    }

    /**
     * @return The user who joined the channel
     */
    public User getUser() {
        return user;
    }

}
