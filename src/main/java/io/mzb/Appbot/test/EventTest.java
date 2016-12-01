package io.mzb.Appbot.test;

import io.mzb.Appbot.Appbot;
import io.mzb.Appbot.events.events.ChannelJoinEvent;
import io.mzb.Appbot.twitch.Channel;
import io.mzb.Appbot.twitch.User;

public class EventTest {

    public void setup() {
        Appbot.getEventManager().addListener(new JoinEventListener());

        Channel callChannel = new Channel("sopea001");
        User callUser = new User("appbot", "sopea001");
        Appbot.getEventManager().callEvent(new ChannelJoinEvent(callChannel, callUser));
    }

}
