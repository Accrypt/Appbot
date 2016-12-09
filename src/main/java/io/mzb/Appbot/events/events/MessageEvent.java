package io.mzb.Appbot.events.events;

import io.mzb.Appbot.events.Event;
import io.mzb.Appbot.twitch.Channel;
import io.mzb.Appbot.twitch.User;

public class MessageEvent extends Event {

    private Channel channel;
    private User user;
    private String message;

    public MessageEvent(Channel channel, User user, String message) {
        this.channel = channel;
        this.user = user;
        this.message = message;
    }

    public Channel getChannel() {
        return channel;
    }

    public User getUser() {
        return user;
    }

    public String getMessage() {
        return message;
    }

}
