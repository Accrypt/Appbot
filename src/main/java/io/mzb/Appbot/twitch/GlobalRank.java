package io.mzb.Appbot.twitch;

public enum GlobalRank {

    TWITCH_STAFF("Staff"),
    ADMIN("Admin"),
    GLOBAL_MOD("Global Mod"),
    TURBO("Turbo"),
    PRIME("Prime");

    private String displayName;

    GlobalRank(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

}
