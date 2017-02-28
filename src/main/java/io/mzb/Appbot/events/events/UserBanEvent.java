package io.mzb.Appbot.events.events;

import io.mzb.Appbot.events.Event;

public class UserBanEvent extends Event {

    private int banDuration;
    private String banReason;
    private long roomId;
    private long targetUserId;
    private String channel;
    private String targetName;

    public UserBanEvent(int banDuration, String banReason, long roomId, long targetUserId, String channel, String targetName) {
        this.banDuration = banDuration;
        this.banReason = banReason;
        this.roomId = roomId;
        this.targetUserId = targetUserId;
        this.channel = channel;
        this.targetName = targetName;
    }

    public int getBanDuration() {
        return banDuration;
    }

    public String getBanReason() {
        return banReason;
    }

    public long getRoomId() {
        return roomId;
    }

    public long getTargetUserId() {
        return targetUserId;
    }

    public String getChannel() {
        return channel;
    }

    public String getTargetName() {
        return targetName;
    }
}
