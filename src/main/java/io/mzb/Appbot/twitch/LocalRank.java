package io.mzb.Appbot.twitch;

public enum LocalRank {

    USER(0, "User"),
    MOD(1, "Mod"),
    OWNER(2, "Owner");

    private int permissionLevel;
    private String displayName;

    /**
     * @param permissionLevel How important is the rank (Higher is better)
     * @param displayName How should the rank be displayed
     */
    LocalRank(int permissionLevel, String displayName) {
        this.permissionLevel = permissionLevel;
        this.displayName = displayName;
    }

    /**
     * @return Integer value of their rank, higher is better
     */
    public int getPermissionLevel() {
        return permissionLevel;
    }

    /**
     * @return Standard display name of the rank
     */
    public String getDisplayName() {
        return displayName;
    }

}
