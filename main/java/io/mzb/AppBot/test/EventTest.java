package io.mzb.AppBot.test;

import io.mzb.AppBot.AppBot;
import io.mzb.AppBot.events.events.ChannelJoinEvent;
import io.mzb.AppBot.twitch.Channel;
import io.mzb.AppBot.twitch.User;

public class EventTest {

    public void setup() {
        AppBot.getEventManager().addListener(new JoinEventListener());

        Channel callChannel = new Channel("sopea001");
        User callUser = new User("appbot", "sopea001");
        AppBot.getEventManager().callEvent(new ChannelJoinEvent(callChannel, callUser));
    }

}
