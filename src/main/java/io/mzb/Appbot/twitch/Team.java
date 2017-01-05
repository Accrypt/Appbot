package io.mzb.Appbot.twitch;

import java.util.ArrayList;

public class Team {

    // The name of the team
    private String name;
    // List of channels in the team
    private ArrayList<String> channels;

    public Team(String name) {
        this.name = name;
        // TODO: Connect to twitch in a task and get the information about team with name "name"
    }

}
