package io.mzb.Appbot.events.events;

import io.mzb.Appbot.events.Event;
import io.mzb.Appbot.twitch.Channel;
import io.mzb.Appbot.twitch.LocalRank;
import io.mzb.Appbot.twitch.User;

public class UserModeEvent extends Event {

    private Channel channel;
    private User user;
    private LocalRank newRank;

    /**
     * @param channel The channel the user mode happened
     * @param user The user that had their rank changed
     * @param newRank The users new rank
     */
    public UserModeEvent(Channel channel, User user, LocalRank newRank) {
        this.channel = channel;
        this.user = user;
        this.newRank = newRank;
    }

    /**
     * @return The channel that the user had their rank changed in
     */
    public Channel getChannel() {
        return channel;
    }

    /**
     * @return The user that had their rank changed
     */
    public User getUser() {
        return user;
    }

    /**
     * @return The new rank the user has
     */
    public LocalRank getNewRank() {
        return newRank;
    }
}
