package io.mzb.Appbot.test;

import io.mzb.Appbot.events.EventHandler;
import io.mzb.Appbot.events.EventListener;
import io.mzb.Appbot.events.events.ChannelJoinEvent;

public class JoinEventListener extends EventListener {

    @EventHandler
    public void onJoinEvent(ChannelJoinEvent event) {
        System.out.println("Channel: " + event.getChannel());
        System.out.println("User: " + event.getUser());
    }

}
