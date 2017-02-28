package io.mzb.Appbot.events.events;

import io.mzb.Appbot.events.Event;
import io.mzb.Appbot.twitch.User;

public class WhisperEvent extends Event {

    private User user;
    private String msg, emotes;

    public WhisperEvent(User user, String msg, String emotes) {
        this.user = user;
        this.msg = msg;
        this.emotes = emotes;
    }

    public User getUser() {
        return user;
    }

    public String getMsg() {
        return msg;
    }

    public String getEmotes() {
        return emotes;
    }
}
