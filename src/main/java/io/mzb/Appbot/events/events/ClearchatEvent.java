package io.mzb.Appbot.events.events;

import io.mzb.Appbot.events.Event;

public class ClearchatEvent extends Event {

    private long roomId;
    private String channel;

    public ClearchatEvent(long roomId, String channel) {
        this.roomId = roomId;
        this.channel = channel;
    }

    public long getRoomId() {
        return roomId;
    }

    public String getChannel() {
        return channel;
    }
}
