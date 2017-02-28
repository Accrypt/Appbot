package io.mzb.Appbot.events.events;

import io.mzb.Appbot.events.Event;

public class NoticeEvent extends Event {

    private NoticeType noticeType;
    private String channel;
    private String msg;

    public NoticeEvent(NoticeType type, String channel, String msg) {
        this.noticeType = type;
        this.channel = channel;
        this.msg = msg;
    }

    public NoticeType getNoticeType() {
        return noticeType;
    }

    public String getChannel() {
        return channel;
    }

    public String getMsg() {
        return msg;
    }

    public enum NoticeType {
        SUBS_ON,
        ALREADY_SUBS_ON,
        SUBS_OFF,
        ALREADY_SUBS_OFF,
        SLOW_ON,
        SLOW_OFF,
        R9K_ON,
        ALREADY_R9K_ON,
        R9K_OFF,
        ALREADY_R9K_OFF,
        HOST_ON,
        BAD_HOST_HOSTING,
        HOST_OFF,
        HOSTS_REMAINING,
        EMOTE_ONLY_ON,
        ALREADY_EMOTE_ONLY_ON,
        EMOTE_ONLY_OFF,
        ALREADY_EMOTE_ONLY_OFF,
        MSG_CHANNEL_SUSPENDED,
        TIMEOUT_SUCCESS,
        BAN_SUCCESS,
        UNBAN_SUCCESS,
        BAD_UNBAN_NO_BAN,
        ALREADY_BANNED,
        UNRECOGNIZED_CMD,
        FOLLOWERS_ON_ZERO,
        FOLLOWERS_OFF,
        FOLLOWERS_ON
    }

}
