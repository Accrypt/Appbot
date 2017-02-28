package io.mzb.Appbot.events.events;

import io.mzb.Appbot.events.Event;
import io.mzb.Appbot.twitch.Channel;
import io.mzb.Appbot.twitch.User;

public class MessageEvent extends Event {

    private Channel channel;
    private User user;
    private String message;
    private String emotes;
    private int bits;

    /**
     * @param channel The channel the message was sent in
     * @param user The user who sent the message
     * @param message The message that was sent
     * @param emotes Emotes that were used in the message
     * @param bits The number of bits used in the message
     */
    public MessageEvent(Channel channel, User user, String message, String emotes, int bits) {
        this.channel = channel;
        this.user = user;
        this.message = message;
        this.emotes = emotes;
        this.bits = bits;
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

    /**
     * @return Emote list in string form, includes emote id and string index start/end
     */
    public String getEmotes() {
        return emotes;
    }

    /**
     * @return The number of bits the user used in the message
     */
    public int getBits() {
        return bits;
    }
}
