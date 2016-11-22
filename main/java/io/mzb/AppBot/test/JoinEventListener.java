package io.mzb.AppBot.test;

import io.mzb.AppBot.events.EventHandler;
import io.mzb.AppBot.events.EventListener;
import io.mzb.AppBot.events.events.ChannelJoinEvent;

public class JoinEventListener extends EventListener {

    @EventHandler
    public void onJoinEvent(ChannelJoinEvent event) {
        System.out.println("Channel: " + event.getChannel());
        System.out.println("User: " + event.getUser());
    }


}
