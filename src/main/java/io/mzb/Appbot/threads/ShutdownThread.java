package io.mzb.Appbot.threads;

import io.mzb.Appbot.Appbot;
import io.mzb.Appbot.plugin.AppbotPlugin;

public class ShutdownThread extends Thread {

    @Override
    public void run() {
        setName("Shutdown");
        // Unload plugins
        for (AppbotPlugin ap : Appbot.getPluginManager().getActivePlugins()) {
            System.out.println("Unloading " + Appbot.getPluginManager().getPluginName(ap));
            ap.onUnload();
            System.out.println("Done");
        }
        System.out.println("Disconnecting socket connection");
        Appbot.getIrcHandler().disconnect();
        System.out.println("Done");
    }
}
