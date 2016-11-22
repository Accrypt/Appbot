package io.mzb.AppBot.twitch;

public enum LocalRank {

    USER(0, "User"),
    MOD(1, "Mod"),
    OWNER(2, "Owner");

    private int permissionLevel;
    private String displayName;

    LocalRank(int permissionLevel, String displayName) {
        this.permissionLevel = permissionLevel;
        this.displayName = displayName;
    }

    public int getPermissionLevel() {
        return permissionLevel;
    }

    public String getDisplayName() {
        return displayName;
    }

}
