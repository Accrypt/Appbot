package io.mzb.Appbot.events.events;

import io.mzb.Appbot.events.Event;
import io.mzb.Appbot.twitch.Channel;
import io.mzb.Appbot.twitch.User;

public class MessageEvent extends Event {

    private Channel channel;
    private User user;
    private String message;

    /**
     * @param channel The channel the message was sent in
     * @param user The user who sent the message
     * @param message The message that was sent
     */
    public MessageEvent(Channel channel, User user, String message) {
        this.channel = channel;
        this.user = user;
        this.message = message;
    }

    /**
     * @return The channel the message was sent in
     */
    public Channel getChannel() {
        return channel;
    }

    /**
     * @return The user who sent the message
     */
    public User getUser() {
        return user;
    }

    /**
     * @return The message that was sent
     */
    public String getMessage() {
        return message;
    }

}
