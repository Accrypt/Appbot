package io.mzb.Appbot.plugin;

public interface AppbotPlugin {

    /**
     * Called to load a plugin
     */
    void onLoad();

    /**
     * Called to unload a plugin
     */
    void onUnload();

}
