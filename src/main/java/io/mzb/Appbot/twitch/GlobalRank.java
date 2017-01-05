package io.mzb.Appbot.twitch;

public enum GlobalRank {

    TWITCH_STAFF("Staff"),
    ADMIN("Admin"),
    GLOBAL_MOD("Global Mod"),
    TURBO("Turbo"),
    PRIME("Prime");

    // Standard way to display the rank
    private String displayName;

    /**
     * @param displayName How the rank should be displayed
     */
    GlobalRank(String displayName) {
        this.displayName = displayName;
    }

    /**
     * @return How the rank should be displayed
     */
    public String getDisplayName() {
        return displayName;
    }

}
